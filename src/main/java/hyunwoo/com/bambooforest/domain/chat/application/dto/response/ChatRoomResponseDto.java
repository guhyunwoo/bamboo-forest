package hyunwoo.com.bambooforest.domain.chat.application.dto.response;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

public record ChatRoomResponseDto(
        String roomId,
        ChatMode chatMode,
        Long creatingUserId,
        Long acceptingUserId,
        String expiresAt
) {
    public static ChatRoomResponseDto from(ChatRoom room) {
        return new ChatRoomResponseDto(
                room.getRoomId(),
                room.getChatMode(),
                room.getCreatingUserId(),
                room.getAcceptingUserId(),
                room.getExpiresAt().toString()
        );
    }
}
