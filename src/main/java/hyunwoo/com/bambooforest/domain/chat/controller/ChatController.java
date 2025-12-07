package hyunwoo.com.bambooforest.domain.chat.controller;

import hyunwoo.com.bambooforest.domain.chat.domain.AnonymousUser;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatMessage;
import hyunwoo.com.bambooforest.domain.chat.domain.ChatRoom;
import hyunwoo.com.bambooforest.domain.chat.domain.MatchRequest;
import hyunwoo.com.bambooforest.domain.chat.dto.request.MatchRequestDto;
import hyunwoo.com.bambooforest.domain.chat.dto.request.SendMessageDto;
import hyunwoo.com.bambooforest.domain.chat.dto.response.ChatMessageResponseDto;
import hyunwoo.com.bambooforest.domain.chat.dto.response.ChatRoomResponseDto;
import hyunwoo.com.bambooforest.domain.chat.dto.response.MatchRequestResponseDto;
import hyunwoo.com.bambooforest.domain.chat.service.ChatMatchingService;
import hyunwoo.com.bambooforest.domain.chat.service.ChatRoomManager;
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
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatMatchingService chatMatchingService;
    private final ChatRoomManager chatRoomManager;

    @PostMapping("/match/request")
    public Mono<MatchRequestResponseDto> requestMatch(@RequestBody MatchRequestDto dto) {
        MatchRequest request = chatMatchingService.createMatchRequest(dto.chatMode());
        return Mono.just(MatchRequestResponseDto.from(request));
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
        ChatRoom chatRoom = chatMatchingService.acceptMatch(requestId);
        chatRoomManager.createRoom(chatRoom);
        return Mono.just(ChatRoomResponseDto.from(chatRoom));
    }

    @DeleteMapping("/match/cancel/{requestId}")
    public Mono<Void> cancelMatch(@PathVariable String requestId) {
        chatMatchingService.cancelMatchRequest(requestId);
        return Mono.empty();
    }

    @PostMapping("/message")
    public Mono<Void> sendMessage(@RequestBody SendMessageDto dto) {
        ChatRoom room = chatRoomManager.getRoom(dto.roomId());

        if (room == null) {
            return Mono.error(new IllegalArgumentException("Room not found"));
        }

        AnonymousUser sender = null;
        if (room.getUser1().getUserId().equals(dto.userId())) {
            sender = room.getUser1();
        } else if (room.getUser2() != null && room.getUser2().getUserId().equals(dto.userId())) {
            sender = room.getUser2();
        }

        if (sender == null) {
            return Mono.error(new IllegalArgumentException("User not in room"));
        }

        ChatMessage message = ChatMessage.chat(dto.roomId(), sender, dto.content());
        chatRoomManager.sendMessage(dto.roomId(), message);

        return Mono.empty();
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
        chatRoomManager.closeRoom(roomId);
        return Mono.empty();
    }
}
