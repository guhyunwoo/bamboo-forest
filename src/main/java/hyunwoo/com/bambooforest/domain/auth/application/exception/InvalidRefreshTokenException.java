package hyunwoo.com.bambooforest.domain.auth.application.exception;

import hyunwoo.com.bambooforest.global.error.BusinessBaseException;
import hyunwoo.com.bambooforest.global.error.ErrorCode;

public class InvalidRefreshTokenException extends BusinessBaseException {
	public InvalidRefreshTokenException() {
		super(ErrorCode.INVALID_REFRESH_TOKEN);
	}
}
