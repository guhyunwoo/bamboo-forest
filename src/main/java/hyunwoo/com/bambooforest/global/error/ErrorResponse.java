package hyunwoo.com.bambooforest.global.error;

public record ErrorResponse(
	String message
) {


	public static ErrorResponse of(String message) {
		return new ErrorResponse(message);
	}

	public static ErrorResponse of(ErrorCode errorCode) {
		return new ErrorResponse(errorCode.message);
	}

}
