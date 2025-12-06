package hyunwoo.com.bambooforest.global.security.util;

import hyunwoo.com.bambooforest.global.security.exception.ExpiredTokenException;
import hyunwoo.com.bambooforest.global.security.exception.InvalidTokenException;
import hyunwoo.com.bambooforest.global.security.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenResolver {
	private final JwtProperties jwtProperties;
	private final TokenSecretKeyManager tokenSecretKeyManager;
	private final TokenValidator tokenValidator;

	public Mono<String> stripBearerScheme(String headerValue) {
		return Mono.fromCallable(() -> {
			tokenValidator.validateAuthorizationHeader(headerValue);
			return headerValue.trim().substring(jwtProperties.prefix().length());
		});
	}

	public Mono<Authentication> getAuthentication(String token) {
		return getTokenBody(token)
			.map(claims -> {
				String userId = claims.getSubject();
				String role = claims.get("role",  String.class);

				return new UsernamePasswordAuthenticationToken(
					userId,
					null,
					List.of(new SimpleGrantedAuthority(role))
				);
			});
	}

	private Mono<Claims> getTokenBody(String token) {
		return Mono.fromCallable(() -> {
			try {
				return Jwts.parser()
					.verifyWith(tokenSecretKeyManager.getDecodedKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
			} catch (ExpiredJwtException e) {
				throw ExpiredTokenException.EXCEPTION;
			} catch (JwtException e) {
				throw InvalidTokenException.EXCEPTION;
			}
		});
	}
}
