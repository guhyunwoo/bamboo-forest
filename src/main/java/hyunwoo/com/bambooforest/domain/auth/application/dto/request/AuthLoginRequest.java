package hyunwoo.com.bambooforest.domain.auth.application.dto.request;

public record AuthLoginRequest(
	String email,
	String password
) { }
