package hyunwoo.com.bambooforest.domain.chat.service;

import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChatRoomManager {

    private final Map<String, ChatRoom> chatRooms = new ConcurrentHashMap<>();
    private final Map<String, Sinks.Many<ChatMessage>> roomMessageSinks = new ConcurrentHashMap<>();

    public void createRoom(ChatRoom chatRoom) {
        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        roomMessageSinks.put(chatRoom.getRoomId(), Sinks.many().multicast().onBackpressureBuffer());

        ChatMessage joinMessage = ChatMessage.system(
                chatRoom.getRoomId(),
                "채팅방이 생성되었습니다. 5분간 대화를 나눌 수 있습니다.",
                ChatMessage.MessageType.JOIN
        );
        sendMessage(chatRoom.getRoomId(), joinMessage);

        scheduleTimeWarnings(chatRoom);

        log.info("Chat room created: roomId={}", chatRoom.getRoomId());
    }

    public void sendMessage(String roomId, ChatMessage message) {
        Sinks.Many<ChatMessage> sink = roomMessageSinks.get(roomId);
        if (sink != null) {
            sink.tryEmitNext(message);
        }
    }

    public Flux<ChatMessage> subscribeToRoom(String roomId) {
        Sinks.Many<ChatMessage> sink = roomMessageSinks.get(roomId);
        if (sink == null) {
            return Flux.error(new IllegalArgumentException("Room not found: " + roomId));
        }
        return sink.asFlux();
    }

    public ChatRoom getRoom(String roomId) {
        return chatRooms.get(roomId);
    }

    public void closeRoom(String roomId) {
        ChatRoom room = chatRooms.get(roomId);
        if (room != null) {
            room.close();

            ChatMessage closeMessage = ChatMessage.system(
                    roomId,
                    "채팅방이 종료되었습니다.",
                    ChatMessage.MessageType.ROOM_CLOSED
            );
            sendMessage(roomId, closeMessage);

            Sinks.Many<ChatMessage> sink = roomMessageSinks.remove(roomId);
            if (sink != null) {
                sink.tryEmitComplete();
            }

            chatRooms.remove(roomId);
            log.info("Chat room closed: roomId={}", roomId);
        }
    }

    private void scheduleTimeWarnings(ChatRoom chatRoom) {
        String roomId = chatRoom.getRoomId();

        Flux.interval(Duration.ofMinutes(3))
                .take(1)
                .subscribe(tick -> {
                    if (chatRooms.containsKey(roomId)) {
                        ChatMessage warning = ChatMessage.system(
                                roomId,
                                "채팅 종료까지 2분 남았습니다.",
                                ChatMessage.MessageType.TIME_WARNING
                        );
                        sendMessage(roomId, warning);
                    }
                });

        Flux.interval(Duration.ofMinutes(4))
                .take(1)
                .subscribe(tick -> {
                    if (chatRooms.containsKey(roomId)) {
                        ChatMessage warning = ChatMessage.system(
                                roomId,
                                "채팅 종료까지 1분 남았습니다.",
                                ChatMessage.MessageType.TIME_WARNING
                        );
                        sendMessage(roomId, warning);
                    }
                });

        Flux.interval(Duration.ofMinutes(5))
                .take(1)
                .subscribe(tick -> closeRoom(roomId));
    }
}
