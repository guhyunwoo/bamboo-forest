package hyunwoo.com.bambooforest.global.security.util;

import hyunwoo.com.bambooforest.domain.user.domain.type.Role;
import hyunwoo.com.bambooforest.global.security.auth.TokenType;
import hyunwoo.com.bambooforest.global.security.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {
	private final JwtProperties jwtProperties;
	private final TokenSecretKeyManager tokenSecretKeyManager;
	private static final String ROLE_PREFIX = "ROLE_";

	public Mono<String> generateTokenHandle(Long userId, Role role) {
		return Mono.fromCallable(() -> generateToken(userId, role))
			.subscribeOn(Schedulers.boundedElastic());
	}

	private String generateToken(Long userId, Role role) {
		Date now = new Date();

		return Jwts.builder()
			.signWith(tokenSecretKeyManager.getDecodedKey())
			.header()
			.type("jwt")
			.and()
			.subject(userId.toString())
			.claim("role", ROLE_PREFIX + role.name())
			.issuedAt(now)
			.expiration(new Date(now.getTime() + jwtProperties.accessTime()))
			.compact();
	}

	private TokenType extractTokenType(Claims tokenBody) {
		return StringToTokenTypeConverter.convert(tokenBody.get("tokenType", String.class));
	}
}
