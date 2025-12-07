package hyunwoo.com.bambooforest.domain.chat.application.dto.response;

public record ExtensionResponseDto(
	String roomId,
	boolean creatingUserRequested,
	boolean acceptingUserRequested,
	boolean extended,
	String expiresAt
) {
	public static ExtensionResponseDto from(String roomId, boolean creatingUserRequested,
	                                        boolean acceptingUserRequested, boolean extended, String expiresAt) {
		return new ExtensionResponseDto(
			roomId,
			creatingUserRequested,
			acceptingUserRequested,
			extended,
			expiresAt
		);
	}
}
