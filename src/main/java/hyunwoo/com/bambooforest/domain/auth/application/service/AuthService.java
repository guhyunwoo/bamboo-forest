package hyunwoo.com.bambooforest.domain.auth.application.service;

import hyunwoo.com.bambooforest.domain.auth.application.dto.request.AuthLoginRequest;
import hyunwoo.com.bambooforest.domain.auth.application.dto.request.AuthRegisterRequest;
import hyunwoo.com.bambooforest.domain.auth.application.dto.request.RefreshTokenRequest;
import hyunwoo.com.bambooforest.domain.auth.application.dto.response.AuthTokenResponse;
import hyunwoo.com.bambooforest.domain.auth.domain.RefreshToken;
import hyunwoo.com.bambooforest.domain.auth.application.exception.EmailAlreadyExistsException;
import hyunwoo.com.bambooforest.domain.auth.application.exception.ExpiredRefreshTokenException;
import hyunwoo.com.bambooforest.domain.auth.application.exception.InvalidEmailOrPasswordException;
import hyunwoo.com.bambooforest.domain.auth.application.exception.InvalidRefreshTokenException;
import hyunwoo.com.bambooforest.domain.auth.repository.RefreshTokenRepository;
import hyunwoo.com.bambooforest.domain.user.domain.User;
import hyunwoo.com.bambooforest.domain.user.domain.type.Role;
import hyunwoo.com.bambooforest.domain.user.repository.UserRepository;
import hyunwoo.com.bambooforest.global.security.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final TokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;

	public Mono<AuthTokenResponse> register(AuthRegisterRequest request) {
		return userRepository.findByEmail(request.email())
			.flatMap(existingUser -> Mono.<AuthTokenResponse>error(
				new EmailAlreadyExistsException()
			))
			.switchIfEmpty(Mono.defer(() -> {
				User newUser = new User(
					null,
					request.name(),
					request.email(),
					passwordEncoder.encode(request.password()),
					request.studentNumber(),
					request.studentGrade(),
					request.studentClass(),
					Role.STUDENT
				);

				return userRepository.save(newUser)
					.flatMap(this::generateTokens);
			}));
	}

	public Mono<AuthTokenResponse> login(AuthLoginRequest request) {
		return userRepository.findByEmail(request.email())
			.switchIfEmpty(Mono.error(new InvalidEmailOrPasswordException()))
			.flatMap(user -> {
				if (!passwordEncoder.matches(request.password(), user.getPassword())) {
					return Mono.error(new InvalidEmailOrPasswordException());
				}
				return generateTokens(user);
			});
	}

	public Mono<String> refresh(RefreshTokenRequest request) {
		return refreshTokenRepository.findByToken(request.refreshToken())
			.switchIfEmpty(Mono.error(new InvalidRefreshTokenException()))
			.flatMap(refreshToken -> {
				if (refreshToken.isExpired()) {
					return refreshTokenRepository.delete(refreshToken)
						.then(Mono.error(new ExpiredRefreshTokenException()));
				}

				return userRepository.findById(refreshToken.getUserId())
					.flatMap(user -> tokenProvider.generateTokenHandle(user.getUserId(), user.getRole()));
			});
	}

	private Mono<AuthTokenResponse> generateTokens(User user) {
		return Mono.zip(
			tokenProvider.generateTokenHandle(user.getUserId(), user.getRole()),
			tokenProvider.generateRefreshToken()
		).flatMap(tuple -> {
			String accessToken = tuple.getT1();
			String refreshToken = tuple.getT2();

			LocalDateTime expiresAt = LocalDateTime.now()
				.plusSeconds(tokenProvider.getRefreshTokenExpiration() / 1000);

			RefreshToken refreshTokenEntity = new RefreshToken(
				null,
				user.getUserId(),
				refreshToken,
				expiresAt
			);

			return refreshTokenRepository.deleteByUserId(user.getUserId())
				.then(refreshTokenRepository.save(refreshTokenEntity))
				.thenReturn(new AuthTokenResponse(accessToken, refreshToken));
		});
	}
}
