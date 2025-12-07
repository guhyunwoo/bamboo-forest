package hyunwoo.com.bambooforest.domain.chat.domain;

import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MatchRequest {
    private final String requestId;
    private final AnonymousUser user;
    private final ChatMode chatMode;
    private final LocalDateTime createdAt;

    public static MatchRequest create(AnonymousUser user, ChatMode chatMode) {
        return new MatchRequest(
                user.getUserId(),
                user,
                chatMode,
                LocalDateTime.now()
        );
    }
}
