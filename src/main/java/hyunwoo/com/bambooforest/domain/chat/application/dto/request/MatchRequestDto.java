package hyunwoo.com.bambooforest.domain.chat.application.dto.request;

import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

public record MatchRequestDto(
	ChatMode chatMode
) {
}
