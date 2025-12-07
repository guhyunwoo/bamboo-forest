package hyunwoo.com.bambooforest.domain.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessage {
    private final String roomId;
    private final String senderId;
    private final String senderNickname;
    private final String content;
    private final LocalDateTime sentAt;
    private final MessageType type;

    public enum MessageType {
        CHAT,           // 일반 채팅 메시지
        JOIN,           // 사용자 입장
        LEAVE,          // 사용자 퇴장
        ROOM_CLOSED,    // 방 종료
        TIME_WARNING    // 시간 경고
    }

    public static ChatMessage chat(String roomId, AnonymousUser sender, String content) {
        return new ChatMessage(
                roomId,
                sender.getUserId(),
                sender.getNickname(),
                content,
                LocalDateTime.now(),
                MessageType.CHAT
        );
    }

    public static ChatMessage system(String roomId, String content, MessageType type) {
        return new ChatMessage(
                roomId,
                "SYSTEM",
                "시스템",
                content,
                LocalDateTime.now(),
                type
        );
    }
}
