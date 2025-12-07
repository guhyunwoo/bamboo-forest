package hyunwoo.com.bambooforest.domain.chat.dto.response;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

public record ChatRoomResponseDto(
        String roomId,
        ChatMode chatMode,
        String user1Id,
        String user1Nickname,
        String user2Id,
        String user2Nickname,
        String expiresAt
) {
    public static ChatRoomResponseDto from(ChatRoom room) {
        return new ChatRoomResponseDto(
                room.getRoomId(),
                room.getChatMode(),
                room.getUser1().getUserId(),
                room.getUser1().getNickname(),
                room.getUser2().getUserId(),
                room.getUser2().getNickname(),
                room.getExpiresAt().toString()
        );
    }
}
