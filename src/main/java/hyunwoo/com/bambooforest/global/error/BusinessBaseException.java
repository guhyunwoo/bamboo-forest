package hyunwoo.com.bambooforest.global.error;

public class BusinessBaseException extends RuntimeException {

	public final ErrorCode errorCode;

	public BusinessBaseException(ErrorCode errorCode) {
		super(errorCode.message);
		this.errorCode = errorCode;
	}

}
