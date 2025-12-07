package hyunwoo.com.bambooforest.domain.oauth.application.service;

import hyunwoo.com.bambooforest.domain.oauth.application.mapper.OAuth2Mapper;
import hyunwoo.com.bambooforest.domain.oauth.domain.BsmOAuth2UserInfo;
import hyunwoo.com.bambooforest.domain.oauth.domain.OAuthUserInfo;
import hyunwoo.com.bambooforest.domain.user.domain.User;
import hyunwoo.com.bambooforest.domain.user.repository.UserRepository;
import hyunwoo.com.bambooforest.global.security.util.TokenProvider;
import hyunwoo.com.bambooforest.infrastructure.oauth2.bsm.BsmAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2AuthFacade {
	private final UserRepository userRepository;
	private final TokenProvider tokenProvider;
	private final BsmAuthService bsmAuthService;

	public Mono<String> bsmLogin(String authorizationCode) {
		return bsmAuthService.getUserInfo(authorizationCode)
			.map(BsmOAuth2UserInfo::new)
			.flatMap(this::processOAuth2Login);
	}

	private Mono<String> processOAuth2Login(OAuthUserInfo oAuthUserInfo) {
		String email = oAuthUserInfo.getEmail();

		return userRepository.findByEmail(email)
			.switchIfEmpty(createUser(oAuthUserInfo))
			.flatMap(user -> tokenProvider.generateTokenHandle(user.getUserId(), user.getRole()));
	}

	private Mono<User> createUser(OAuthUserInfo oAuthUserInfo) {
		User newUser = OAuth2Mapper.toUserEntity(oAuthUserInfo);
		return userRepository.save(newUser);
	}
}
