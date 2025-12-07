package hyunwoo.com.bambooforest.domain.chat.repository;

import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.domain.type.ChatMode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MatchRequestRepository extends ReactiveCrudRepository<MatchRequest, String> {

    @Query("SELECT * FROM match_requests WHERE chat_mode = :chatMode ORDER BY created_at ASC LIMIT 1")
    Mono<MatchRequest> findOldestByChatMode(ChatMode chatMode);

    Flux<MatchRequest> findByChatMode(ChatMode chatMode);

    @Query("DELETE FROM match_requests WHERE created_at < NOW() - INTERVAL '10 minutes'")
    Mono<Void> deleteExpiredRequests();
}
