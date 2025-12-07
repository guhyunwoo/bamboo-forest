package hyunwoo.com.bambooforest.domain.auth.application.exception;

import hyunwoo.com.bambooforest.global.error.BusinessBaseException;
import hyunwoo.com.bambooforest.global.error.ErrorCode;

public class InvalidEmailOrPasswordException extends BusinessBaseException {
	public InvalidEmailOrPasswordException() {
		super(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
	}
}
