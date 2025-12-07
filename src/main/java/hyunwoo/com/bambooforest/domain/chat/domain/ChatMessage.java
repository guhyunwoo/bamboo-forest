package hyunwoo.com.bambooforest.domain.chat.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Table("chat_messages")
public class ChatMessage {
    @Id
    @Column("message_id")
    private String messageId;

    @Column("room_id")
    private String roomId;

    @Column("sender_id")
    private Long senderId;

    @Column("content")
    private String content;

    @Column("sent_at")
    private LocalDateTime sentAt;

    @Column("type")
    private MessageType type;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        ROOM_CLOSED,
        TIME_WARNING
    }

    public ChatMessage(String messageId, String roomId, Long senderId, String content,
                       LocalDateTime sentAt, MessageType type) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
        this.sentAt = sentAt;
        this.type = type;
    }
}
