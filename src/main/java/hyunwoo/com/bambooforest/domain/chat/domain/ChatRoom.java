package hyunwoo.com.bambooforest.domain.chat.domain;

import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ChatRoom {
    private final String roomId;
    private final ChatMode chatMode;
    private final AnonymousUser user1;
    private AnonymousUser user2;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private boolean isActive;

    public ChatRoom(ChatMode chatMode, AnonymousUser user1) {
        this.roomId = UUID.randomUUID().toString();
        this.chatMode = chatMode;
        this.user1 = user1;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = this.createdAt.plusMinutes(5);
        this.isActive = true;
    }

    public void joinUser2(AnonymousUser user2) {
        if (this.user2 != null) {
            throw new IllegalStateException("Room is already full");
        }
        this.user2 = user2;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void close() {
        this.isActive = false;
    }

    public boolean isFull() {
        return user2 != null;
    }
}
