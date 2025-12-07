package hyunwoo.com.bambooforest.domain.chat.application.mapper;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatMessageMapper {
	public static ChatMessage createChatMessage(String roomId, Long senderId, String content) {
		return new ChatMessage(
			UUID.randomUUID().toString(),
			roomId,
			senderId,
			content,
			LocalDateTime.now(),
			ChatMessage.MessageType.CHAT
		);
	}

	public static ChatMessage createSystemMessage(String roomId, String content, ChatMessage.MessageType type) {
		return new ChatMessage(
			UUID.randomUUID().toString(),
			roomId,
			null,
			content,
			LocalDateTime.now(),
			type
		);
	}
}
