package hyunwoo.com.bambooforest.domain.chat.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class AnonymousUser {
    private final String userId;
    private final String nickname;

    public static AnonymousUser create() {
        String userId = UUID.randomUUID().toString();
        String nickname = "익명" + userId.substring(0, 6);
        return new AnonymousUser(userId, nickname);
    }
}
