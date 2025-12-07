package hyunwoo.com.bambooforest.domain.chat.service;

import hyunwoo.com.bambooforest.domain.chat.domain.AnonymousUser;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMatchingService {

    private final Map<String, MatchRequest> waitingRequests = new ConcurrentHashMap<>();
    private final Sinks.Many<MatchRequest> matchRequestSink = Sinks.many().multicast().onBackpressureBuffer();

    public MatchRequest createMatchRequest(ChatMode chatMode) {
        AnonymousUser user = AnonymousUser.create();
        MatchRequest request = MatchRequest.create(user, chatMode);

        waitingRequests.put(request.getRequestId(), request);

        matchRequestSink.tryEmitNext(request);
        log.info("Match request created: userId={}, chatMode={}", user.getUserId(), chatMode);

        return request;
    }

    public Flux<MatchRequest> subscribeMatchRequests() {
        return matchRequestSink.asFlux();
    }

    public ChatRoom acceptMatch(String requestId) {
        MatchRequest originalRequest = waitingRequests.remove(requestId);

        if (originalRequest == null) {
            throw new IllegalStateException("Match request not found or already accepted");
        }

        AnonymousUser user2 = AnonymousUser.create();
        ChatRoom chatRoom = new ChatRoom(originalRequest.getChatMode(), originalRequest.getUser());
        chatRoom.joinUser2(user2);

        log.info("Match accepted: roomId={}, user1={}, user2={}",
                chatRoom.getRoomId(),
                originalRequest.getUser().getUserId(),
                user2.getUserId());

        return chatRoom;
    }

    public void cancelMatchRequest(String requestId) {
        waitingRequests.remove(requestId);
        log.info("Match request cancelled: requestId={}", requestId);
    }

    public boolean hasWaitingRequest(String requestId) {
        return waitingRequests.containsKey(requestId);
    }
}
