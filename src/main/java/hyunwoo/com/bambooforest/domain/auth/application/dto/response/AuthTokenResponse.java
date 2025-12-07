package hyunwoo.com.bambooforest.domain.auth.application.dto.response;

public record AuthTokenResponse(
	String accessToken,
	String refreshToken
) { }
