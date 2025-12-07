package hyunwoo.com.bambooforest.domain.chat.controller;

import hyunwoo.com.bambooforest.domain.chat.application.dto.request.MatchRequestDto;
import hyunwoo.com.bambooforest.domain.chat.application.dto.request.SendMessageDto;
import hyunwoo.com.bambooforest.domain.chat.application.dto.response.ChatMessageResponseDto;
import hyunwoo.com.bambooforest.domain.chat.application.dto.response.ChatRoomResponseDto;
import hyunwoo.com.bambooforest.domain.chat.application.dto.response.ExtensionResponseDto;
import hyunwoo.com.bambooforest.domain.chat.application.dto.response.MatchRequestResponseDto;
import hyunwoo.com.bambooforest.domain.chat.application.mapper.ChatMessageMapper;
import hyunwoo.com.bambooforest.domain.chat.application.service.ChatMatchingService;
import hyunwoo.com.bambooforest.domain.chat.application.service.ChatRoomManager;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;
import hyunwoo.com.bambooforest.global.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatMatchingService chatMatchingService;
    private final ChatRoomManager chatRoomManager;

    @PostMapping("/match/request")
    public Mono<MatchRequestResponseDto> requestMatch(@RequestBody MatchRequestDto dto) {
		return SecurityUtil.getCurrentUserId()
			.flatMap(userId -> chatMatchingService.createMatchRequest(userId, dto.chatMode()))
			.map(MatchRequestResponseDto::from);
    }

    @GetMapping(value = "/match/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<MatchRequestResponseDto>> subscribeMatchRequests() {
        return chatMatchingService.subscribeMatchRequests()
                .map(MatchRequestResponseDto::from)
                .map(dto -> ServerSentEvent.<MatchRequestResponseDto>builder()
                        .event("match-request")
                        .data(dto)
                        .build())
                .mergeWith(Flux.interval(Duration.ofSeconds(30))
                        .map(tick -> ServerSentEvent.<MatchRequestResponseDto>builder()
                                .event("heartbeat")
                                .comment("keep-alive")
                                .build()));
    }

    @PostMapping("/match/accept/{requestId}")
    public Mono<ChatRoomResponseDto> acceptMatch(@PathVariable String requestId) {
		return SecurityUtil.getCurrentUserId()
			.flatMap(userId -> chatMatchingService.acceptMatch(requestId, userId))
			.flatMap(chatRoomManager::createRoom)
			.map(ChatRoomResponseDto::from);
    }

    @DeleteMapping("/match/cancel/{requestId}")
    public Mono<Void> cancelMatch(@PathVariable String requestId) {
		return chatMatchingService.cancelMatchRequest(requestId);
    }

    @PostMapping("/message")
    public Mono<Void> sendMessage(@RequestBody SendMessageDto dto) {
		return SecurityUtil.getCurrentUserId()
			.flatMap(userId -> chatRoomManager.getRoom(dto.roomId())
				.switchIfEmpty(Mono.error(new IllegalArgumentException("Room not found")))
				.flatMap(room -> {
					if (!room.getCreatingUserId().equals(userId) &&
						!room.getAcceptingUserId().equals(userId)) {
						return Mono.error(new IllegalArgumentException("User not in room"));
					}

					ChatMessage message = ChatMessageMapper.createChatMessage(
						dto.roomId(),
						userId,
						dto.content()
					);
					return chatRoomManager.sendMessage(dto.roomId(), message);
				}))
			.then();
    }

    @GetMapping(value = "/room/{roomId}/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ChatMessageResponseDto>> subscribeToRoom(@PathVariable String roomId) {
        return chatRoomManager.subscribeToRoom(roomId)
                .map(ChatMessageResponseDto::from)
                .map(dto -> ServerSentEvent.<ChatMessageResponseDto>builder()
                        .event("message")
                        .data(dto)
                        .build())
                .mergeWith(Flux.interval(Duration.ofSeconds(30))
                        .map(tick -> ServerSentEvent.<ChatMessageResponseDto>builder()
                                .event("heartbeat")
                                .comment("keep-alive")
                                .build()));
    }

    @PostMapping("/room/{roomId}/close")
    public Mono<Void> closeRoom(@PathVariable String roomId) {
		return chatRoomManager.closeRoom(roomId);
	}

	@PostMapping("/room/{roomId}/extend")
	public Mono<ExtensionResponseDto> requestExtension(@PathVariable String roomId) {
		return SecurityUtil.getCurrentUserId()
			.flatMap(userId -> chatRoomManager.requestExtension(roomId, userId))
			.map(room -> ExtensionResponseDto.from(
				room.getRoomId(),
				room.isExtensionRequestedByCreatingUser(),
				room.isExtensionRequestedByAcceptingUser(),
				room.isBothUsersRequestedExtension(),
				room.getExpiresAt().toString()
			));
    }
}
