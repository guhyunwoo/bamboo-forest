package hyunwoo.com.bambooforest.domain.auth.application.dto.request;

public record AuthRegisterRequest(
	String name,
	String email,
	String password,
	String studentNumber,
	Integer studentGrade,
	Integer studentClass
) { }
