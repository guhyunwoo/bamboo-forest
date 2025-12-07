package hyunwoo.com.bambooforest.domain.chat.repository;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRoomRepository extends ReactiveCrudRepository<ChatRoom, String> {

    @Query("SELECT * FROM chat_rooms WHERE is_active = true")
    Flux<ChatRoom> findAllActiveRooms();

    @Query("SELECT * FROM chat_rooms WHERE room_id = :roomId AND is_active = true")
    Mono<ChatRoom> findActiveRoomById(String roomId);

    @Query("SELECT * FROM chat_rooms WHERE expires_at < NOW() AND is_active = true")
    Flux<ChatRoom> findExpiredRooms();
}
