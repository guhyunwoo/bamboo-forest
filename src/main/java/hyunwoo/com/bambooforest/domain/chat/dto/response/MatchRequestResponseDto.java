package hyunwoo.com.bambooforest.domain.chat.dto.response;

import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

public record MatchRequestResponseDto(
        String requestId,
        String userId,
        String nickname,
        ChatMode chatMode,
        String createdAt
) {
    public static MatchRequestResponseDto from(MatchRequest request) {
        return new MatchRequestResponseDto(
                request.getRequestId(),
                request.getUser().getUserId(),
                request.getUser().getNickname(),
                request.getChatMode(),
                request.getCreatedAt().toString()
        );
    }
}
