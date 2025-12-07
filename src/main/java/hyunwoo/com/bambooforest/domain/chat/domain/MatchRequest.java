package hyunwoo.com.bambooforest.domain.chat.domain;

import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Table("match_requests")
public class MatchRequest {
    @Id
    @Column("request_id")
    private String requestId;

    @Column("user_id")
    private Long userId;

    @Column("chat_mode")
    private ChatMode chatMode;

    @Column("created_at")
    private LocalDateTime createdAt;

    public MatchRequest(String requestId, Long userId, ChatMode chatMode, LocalDateTime createdAt) {
        this.requestId = requestId;
        this.userId = userId;
        this.chatMode = chatMode;
        this.createdAt = createdAt;
    }
}
