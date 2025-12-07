package hyunwoo.com.bambooforest.domain.chat.application.dto.request;

public record SendMessageDto(
	String roomId,
	String content
) {
}
