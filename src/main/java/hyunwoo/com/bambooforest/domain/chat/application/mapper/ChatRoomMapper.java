package hyunwoo.com.bambooforest.domain.chat.application.mapper;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

import java.time.LocalDateTime;
import java.util.UUID;

public class ChatRoomMapper {
	public static ChatRoom create(ChatMode chatMode, Long creatingUserId) {
		LocalDateTime now = LocalDateTime.now();
		return new ChatRoom(
			UUID.randomUUID().toString(),
			chatMode,
			creatingUserId,
			null,
			now,
			now.plusMinutes(5),
			true,
			false,
			false
		);
	}
}
