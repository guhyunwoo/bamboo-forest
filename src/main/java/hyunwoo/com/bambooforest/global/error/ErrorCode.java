package hyunwoo.com.bambooforest.global.error;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ErrorCode {
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바르지 않은 JWT 토큰입니다."),
	USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 유저입니다."),
	INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "올바르지 않은 비밀번호입니다."),

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 입력값입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "잘못된 HTTP 메서드를 호출했습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
	;


	public final HttpStatus status;
	public final String message;
}
