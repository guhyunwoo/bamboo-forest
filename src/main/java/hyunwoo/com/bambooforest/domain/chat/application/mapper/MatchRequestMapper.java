package hyunwoo.com.bambooforest.domain.chat.application.mapper;

import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatchRequestMapper {
	public static MatchRequest create(Long userId, ChatMode chatMode) {
		return new MatchRequest(
			UUID.randomUUID().toString(),
			userId,
			chatMode,
			LocalDateTime.now()
		);
	}
}
