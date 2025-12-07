package hyunwoo.com.bambooforest.domain.chat.dto.response;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;

public record ChatMessageResponseDto(
        String roomId,
        String senderId,
        String senderNickname,
        String content,
        String sentAt,
        String messageType
) {
    public static ChatMessageResponseDto from(ChatMessage message) {
        return new ChatMessageResponseDto(
                message.getRoomId(),
                message.getSenderId(),
                message.getSenderNickname(),
                message.getContent(),
                message.getSentAt().toString(),
                message.getType().name()
        );
    }
}
