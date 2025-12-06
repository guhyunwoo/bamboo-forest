package hyunwoo.com.bambooforest.global.security.exception;

import hyunwoo.com.bambooforest.global.error.BusinessBaseException;
import hyunwoo.com.bambooforest.global.error.ErrorCode;

public class InvalidTokenException extends BusinessBaseException {
	public static final BusinessBaseException EXCEPTION = new InvalidTokenException();

	private InvalidTokenException() {
		super(ErrorCode.INVALID_TOKEN);
	}
}
