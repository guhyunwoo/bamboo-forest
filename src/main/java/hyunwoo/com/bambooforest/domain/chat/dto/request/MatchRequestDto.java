package hyunwoo.com.bambooforest.domain.chat.dto.request;

import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

public record MatchRequestDto(
        ChatMode chatMode
) {
}
