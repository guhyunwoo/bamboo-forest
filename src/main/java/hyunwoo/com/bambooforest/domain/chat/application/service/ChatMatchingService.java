package hyunwoo.com.bambooforest.domain.chat.application.service;

import hyunwoo.com.bambooforest.domain.chat.application.mapper.ChatRoomMapper;
import hyunwoo.com.bambooforest.domain.chat.application.mapper.MatchRequestMapper;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import hyunwoo.com.bambooforest.domain.chat.repository.MatchRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMatchingService {

	private final MatchRequestRepository matchRequestRepository;
    private final Sinks.Many<MatchRequest> matchRequestSink = Sinks.many().multicast().onBackpressureBuffer();

	public Mono<MatchRequest> createMatchRequest(Long userId, ChatMode chatMode) {
		MatchRequest request = MatchRequestMapper.create(userId, chatMode);

		return matchRequestRepository.save(request)
			.doOnSuccess(savedRequest -> {
				matchRequestSink.tryEmitNext(savedRequest);
				log.info("Match request created: userId={}, chatMode={}", userId, chatMode);
			});
    }

    public Flux<MatchRequest> subscribeMatchRequests() {
        return matchRequestSink.asFlux();
    }

	public Mono<ChatRoom> acceptMatch(String requestId, Long acceptingUserId) {
		return matchRequestRepository.findById(requestId)
			.switchIfEmpty(Mono.error(new IllegalStateException("Match request not found or already accepted")))
			.flatMap(originalRequest -> matchRequestRepository.deleteById(requestId)
				.thenReturn(originalRequest))
			.map(originalRequest -> {
				ChatRoom chatRoom = ChatRoomMapper.create(originalRequest.getChatMode(), originalRequest.getUserId());
				chatRoom.join(acceptingUserId);

				log.info("Match accepted: roomId={}, creatingUser={}, acceptingUser={}",
					chatRoom.getRoomId(),
					originalRequest.getUserId(),
					acceptingUserId);

				return chatRoom;
			});
	}

	public Mono<Void> cancelMatchRequest(String requestId) {
		return matchRequestRepository.deleteById(requestId)
			.doOnSuccess(unused -> log.info("Match request cancelled: requestId={}", requestId));
    }

	public Mono<Boolean> hasWaitingRequest(String requestId) {
		return matchRequestRepository.existsById(requestId);
    }

	public Mono<Void> cleanupExpiredRequests() {
		return matchRequestRepository.deleteExpiredRequests();
    }
}
