package hyunwoo.com.bambooforest.domain.chat.domain;

import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Table("chat_rooms")
public class ChatRoom {
    @Id
    @Column("room_id")
    private String roomId;

    @Column("chat_mode")
    private ChatMode chatMode;

    @Column("creating_user_id")
    private Long creatingUserId;

    @Column("accepting_user_id")
    private Long acceptingUserId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("expires_at")
    private LocalDateTime expiresAt;

    @Column("is_active")
    private boolean isActive;

    @Column("extension_requested_by_creating_user")
    private boolean extensionRequestedByCreatingUser;

    @Column("extension_requested_by_accepting_user")
    private boolean extensionRequestedByAcceptingUser;

    public ChatRoom(String roomId, ChatMode chatMode, Long creatingUserId, Long acceptingUserId,
                    LocalDateTime createdAt, LocalDateTime expiresAt, boolean isActive,
                    boolean extensionRequestedByCreatingUser, boolean extensionRequestedByAcceptingUser) {
        this.roomId = roomId;
        this.chatMode = chatMode;
        this.creatingUserId = creatingUserId;
        this.acceptingUserId = acceptingUserId;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
        this.extensionRequestedByCreatingUser = extensionRequestedByCreatingUser;
        this.extensionRequestedByAcceptingUser = extensionRequestedByAcceptingUser;
    }

    public void join(Long acceptingUserId) {
        if (this.acceptingUserId != null) {
            throw new IllegalStateException("Room is already full");
        }
        this.acceptingUserId = acceptingUserId;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public void close() {
        this.isActive = false;
    }

    public boolean isFull() {
        return acceptingUserId != null;
    }

    public void requestExtension(Long userId) {
        if (userId.equals(creatingUserId)) {
            this.extensionRequestedByCreatingUser = true;
        } else if (userId.equals(acceptingUserId)) {
            this.extensionRequestedByAcceptingUser = true;
        } else {
            throw new IllegalArgumentException("User not in room");
        }
    }

    public boolean isBothUsersRequestedExtension() {
        return extensionRequestedByCreatingUser && extensionRequestedByAcceptingUser;
    }

    public void extend(int minutes) {
        this.expiresAt = this.expiresAt.plusMinutes(minutes);
        this.extensionRequestedByCreatingUser = false;
        this.extensionRequestedByAcceptingUser = false;
    }
}
