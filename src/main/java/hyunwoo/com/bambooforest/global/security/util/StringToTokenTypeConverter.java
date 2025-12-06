package hyunwoo.com.bambooforest.global.security.util;

import hyunwoo.com.bambooforest.global.security.auth.TokenType;

public class StringToTokenTypeConverter {
	public static TokenType convert(String source) {
		return TokenType.valueOf(source);
	}
}
