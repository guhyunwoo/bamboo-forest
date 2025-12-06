package hyunwoo.com.bambooforest.global.security.util;

import hyunwoo.com.bambooforest.global.security.auth.TokenType;
import hyunwoo.com.bambooforest.global.security.exception.InvalidTokenException;
import hyunwoo.com.bambooforest.global.security.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenValidator {
	private final JwtProperties jwtProperties;

	public void validateAuthorizationHeader(String headerValue) {
		if (headerValue == null || !headerValue.startsWith(jwtProperties.prefix())) {
			throw  InvalidTokenException.EXCEPTION;
		}
	}

	public void validateRefreshToken(TokenType tokenType) {
		if (tokenType != TokenType.REFRESH) {
			throw InvalidTokenException.EXCEPTION;
		}
	}
}
