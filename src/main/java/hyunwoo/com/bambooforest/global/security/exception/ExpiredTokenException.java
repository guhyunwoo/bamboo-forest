package hyunwoo.com.bambooforest.global.security.exception;

import hyunwoo.com.bambooforest.global.error.BusinessBaseException;
import hyunwoo.com.bambooforest.global.error.ErrorCode;

public class ExpiredTokenException extends BusinessBaseException {
	public static final BusinessBaseException EXCEPTION = new ExpiredTokenException();

	private ExpiredTokenException() {
		super(ErrorCode.EXPIRED_TOKEN);
	}
}
