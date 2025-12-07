package hyunwoo.com.bambooforest.domain.chat.application.service;

import hyunwoo.com.bambooforest.domain.chat.application.mapper.ChatMessageMapper;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.repository.ChatMessageRepository;
import hyunwoo.com.bambooforest.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomManager {

	private final ChatRoomRepository chatRoomRepository;
	private final ChatMessageRepository chatMessageRepository;
    private final Map<String, Sinks.Many<ChatMessage>> roomMessageSinks = new ConcurrentHashMap<>();

	public Mono<ChatRoom> createRoom(ChatRoom chatRoom) {
		return chatRoomRepository.save(chatRoom)
			.doOnSuccess(savedRoom -> {
				roomMessageSinks.put(savedRoom.getRoomId(), Sinks.many().multicast().onBackpressureBuffer());

				ChatMessage joinMessage = ChatMessageMapper.createSystemMessage(
					savedRoom.getRoomId(),
					"채팅방이 생성되었습니다. 5분간 대화를 나눌 수 있습니다.",
					ChatMessage.MessageType.JOIN
				);

				sendMessage(savedRoom.getRoomId(), joinMessage).subscribe();
				scheduleTimeWarnings(savedRoom);

				log.info("Chat room created: roomId={}", savedRoom.getRoomId());
			});
    }

	public Mono<ChatMessage> sendMessage(String roomId, ChatMessage message) {
		return chatMessageRepository.save(message)
			.doOnSuccess(savedMessage -> {
				Sinks.Many<ChatMessage> sink = roomMessageSinks.get(roomId);
				if (sink != null) {
					sink.tryEmitNext(savedMessage);
				}
			});
    }

    public Flux<ChatMessage> subscribeToRoom(String roomId) {
        Sinks.Many<ChatMessage> sink = roomMessageSinks.get(roomId);
        if (sink == null) {
            return Flux.error(new IllegalArgumentException("Room not found: " + roomId));
        }
        return sink.asFlux();
    }

	public Mono<ChatRoom> getRoom(String roomId) {
		return chatRoomRepository.findById(roomId);
    }

	public Mono<Void> closeRoom(String roomId) {
		return chatRoomRepository.findById(roomId)
			.flatMap(room -> {
				room.close();

				ChatMessage closeMessage = ChatMessageMapper.createSystemMessage(
					roomId,
					"채팅방이 종료되었습니다.",
					ChatMessage.MessageType.ROOM_CLOSED
				);

				return sendMessage(roomId, closeMessage)
					.then(chatRoomRepository.save(room))
					.then();
			})
			.doOnSuccess(unused -> {
				Sinks.Many<ChatMessage> sink = roomMessageSinks.remove(roomId);
				if (sink != null) {
					sink.tryEmitComplete();
				}
				log.info("Chat room closed: roomId={}", roomId);
			});
    }

    private void scheduleTimeWarnings(ChatRoom chatRoom) {
        String roomId = chatRoom.getRoomId();

        Flux.interval(Duration.ofMinutes(3))
                .take(1)
			.flatMap(tick -> chatRoomRepository.existsById(roomId)
				.flatMap(exists -> {
					if (exists) {
						ChatMessage warning = ChatMessageMapper.createSystemMessage(
							roomId,
							"채팅 종료까지 2분 남았습니다.",
							ChatMessage.MessageType.TIME_WARNING
						);
						return sendMessage(roomId, warning);
					}
					return Mono.empty();
				}))
			.subscribe();

        Flux.interval(Duration.ofMinutes(4))
                .take(1)
			.flatMap(tick -> chatRoomRepository.existsById(roomId)
				.flatMap(exists -> {
					if (exists) {
						ChatMessage warning = ChatMessageMapper.createSystemMessage(
							roomId,
							"채팅 종료까지 1분 남았습니다.",
							ChatMessage.MessageType.TIME_WARNING
						);
						return sendMessage(roomId, warning);
					}
					return Mono.empty();
				}))
			.subscribe();

        Flux.interval(Duration.ofMinutes(5))
                .take(1)
			.flatMap(tick -> closeRoom(roomId))
			.subscribe();
    }

	public Flux<ChatMessage> getMessageHistory(String roomId) {
		return chatMessageRepository.findByRoomId(roomId);
	}

	public Flux<ChatMessage> getRecentMessages(String roomId, int limit) {
		return chatMessageRepository.findRecentMessagesByRoomId(roomId, limit);
	}

	public Mono<ChatRoom> requestExtension(String roomId, Long userId) {
		return chatRoomRepository.findById(roomId)
			.switchIfEmpty(Mono.error(new IllegalArgumentException("Room not found")))
			.flatMap(room -> {
				room.requestExtension(userId);

				if (room.isBothUsersRequestedExtension()) {
					room.extend(5);

					ChatMessage extensionMessage = ChatMessageMapper.createSystemMessage(
						roomId,
						"양쪽 사용자가 동의하여 채팅 시간이 5분 연장되었습니다.",
						ChatMessage.MessageType.TIME_WARNING
					);

					return sendMessage(roomId, extensionMessage)
						.then(chatRoomRepository.save(room))
						.doOnSuccess(savedRoom -> {
							log.info("Chat room extended: roomId={}, newExpiresAt={}",
								roomId, savedRoom.getExpiresAt());
						});
				} else {
					ChatMessage requestMessage = ChatMessageMapper.createSystemMessage(
						roomId,
						"시간 연장 요청이 전송되었습니다. 상대방도 동의하면 5분 연장됩니다.",
						ChatMessage.MessageType.TIME_WARNING
					);

					return sendMessage(roomId, requestMessage)
						.then(chatRoomRepository.save(room))
						.doOnSuccess(savedRoom -> {
							log.info("Extension requested: roomId={}, userId={}", roomId, userId);
						});
				}
			});
	}
}
