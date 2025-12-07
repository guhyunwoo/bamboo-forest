package hyunwoo.com.bambooforest.domain.chat.application.dto.response;

import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

public record MatchRequestResponseDto(
        String requestId,
        Long userId,
        ChatMode chatMode,
        String createdAt
) {
    public static MatchRequestResponseDto from(MatchRequest request) {
        return new MatchRequestResponseDto(
                request.getRequestId(),
                request.getUserId(),
                request.getChatMode(),
                request.getCreatedAt().toString()
        );
    }
}
