package hyunwoo.com.bambooforest.domain.chat.repository;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChatMessageRepository extends ReactiveCrudRepository<ChatMessage, String> {

    @Query("SELECT * FROM chat_messages WHERE room_id = :roomId ORDER BY sent_at ASC")
    Flux<ChatMessage> findByRoomId(String roomId);

    @Query("SELECT * FROM chat_messages WHERE room_id = :roomId ORDER BY sent_at DESC LIMIT :limit")
    Flux<ChatMessage> findRecentMessagesByRoomId(String roomId, int limit);
}
