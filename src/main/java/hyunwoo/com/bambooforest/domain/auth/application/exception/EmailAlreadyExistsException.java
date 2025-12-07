package hyunwoo.com.bambooforest.domain.auth.application.exception;

import hyunwoo.com.bambooforest.global.error.BusinessBaseException;
import hyunwoo.com.bambooforest.global.error.ErrorCode;

public class EmailAlreadyExistsException extends BusinessBaseException {
	public EmailAlreadyExistsException() {
		super(ErrorCode.EMAIL_ALREADY_EXISTS);
	}
}
