package hyunwoo.com.bambooforest.domain.chat.dto.request;

public record SendMessageDto(
        String roomId,
        String userId,
        String content
) {
}
