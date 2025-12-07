package hyunwoo.com.bambooforest.domain.auth.application.exception;

import hyunwoo.com.bambooforest.global.error.BusinessBaseException;
import hyunwoo.com.bambooforest.global.error.ErrorCode;

public class ExpiredRefreshTokenException extends BusinessBaseException {
	public ExpiredRefreshTokenException() {
		super(ErrorCode.EXPIRED_REFRESH_TOKEN);
	}
}
