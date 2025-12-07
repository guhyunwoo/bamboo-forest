# Random Chat API Documentation

WebFlux 기반의 익명 랜덤 채팅 서비스 API입니다.

## 주요 기능

- **3가지 채팅 모드**: CASUAL(가벼운 대화), SERIOUS(진지한 대화), RANDOM(완전 랜덤)
- **익명 채팅**: 자동 생성되는 익명 닉네임 사용
- **5분 타임제한**: 모든 채팅방은 생성 후 5분 뒤 자동 종료
- **SSE 실시간 통신**: Server-Sent Events를 통한 실시간 메시지 전송
- **브로드캐스트 매칭**: 매칭 요청을 모든 접속자에게 브로드캐스트

## API Endpoints

### 1. 매칭 요청 생성
```http
POST /api/chat/match/request
Content-Type: application/json

{
  "chatMode": "CASUAL"  // CASUAL, SERIOUS, RANDOM 중 선택
}
```

**Response:**
```json
{
  "requestId": "uuid",
  "userId": "user-uuid",
  "nickname": "익명abc123",
  "chatMode": "CASUAL",
  "createdAt": "2025-12-07T12:00:00"
}
```

### 2. 매칭 요청 구독 (SSE)
```http
GET /api/chat/match/subscribe
Accept: text/event-stream
```

**SSE Events:**
```
event: match-request
data: {
  "requestId": "uuid",
  "userId": "user-uuid",
  "nickname": "익명abc123",
  "chatMode": "CASUAL",
  "createdAt": "2025-12-07T12:00:00"
}

event: heartbeat
: keep-alive
```

### 3. 매칭 수락
```http
POST /api/chat/match/accept/{requestId}
```

**Response:**
```json
{
  "roomId": "room-uuid",
  "chatMode": "CASUAL",
  "user1Id": "user1-uuid",
  "user1Nickname": "익명abc123",
  "user2Id": "user2-uuid",
  "user2Nickname": "익명def456",
  "expiresAt": "2025-12-07T12:05:00"
}
```

### 4. 매칭 취소
```http
DELETE /api/chat/match/cancel/{requestId}
```

### 5. 메시지 전송
```http
POST /api/chat/message
Content-Type: application/json

{
  "roomId": "room-uuid",
  "userId": "user-uuid",
  "content": "안녕하세요!"
}
```

### 6. 채팅방 구독 (SSE)
```http
GET /api/chat/room/{roomId}/subscribe
Accept: text/event-stream
```

**SSE Events:**
```
event: message
data: {
  "roomId": "room-uuid",
  "senderId": "user-uuid",
  "senderNickname": "익명abc123",
  "content": "안녕하세요!",
  "sentAt": "2025-12-07T12:00:00",
  "messageType": "CHAT"
}
```

**Message Types:**
- `CHAT`: 일반 채팅 메시지
- `JOIN`: 사용자 입장
- `LEAVE`: 사용자 퇴장
- `ROOM_CLOSED`: 방 종료
- `TIME_WARNING`: 시간 경고 (3분 후, 4분 후)

### 7. 채팅방 종료
```http
POST /api/chat/room/{roomId}/close
```

## 사용 흐름

### 매칭 프로세스

1. **사용자 A**: 매칭 요청 생성
   ```
   POST /api/chat/match/request
   { "chatMode": "CASUAL" }
   ```

2. **모든 접속자**: 매칭 요청 수신 (SSE 구독 중)
   ```
   GET /api/chat/match/subscribe
   → event: match-request 수신
   ```

3. **사용자 B**: 매칭 수락 (먼저 요청한 사람이 매칭됨)
   ```
   POST /api/chat/match/accept/{requestId}
   → 채팅방 생성
   ```

### 채팅 프로세스

1. **양쪽 사용자**: 채팅방 구독
   ```
   GET /api/chat/room/{roomId}/subscribe
   ```

2. **메시지 전송**
   ```
   POST /api/chat/message
   {
     "roomId": "room-uuid",
     "userId": "user-uuid",
     "content": "안녕하세요!"
   }
   ```

3. **타임 워닝**:
   - 3분 경과: "채팅 종료까지 2분 남았습니다."
   - 4분 경과: "채팅 종료까지 1분 남았습니다."
   - 5분 경과: 자동 종료

## 채팅 모드

- **CASUAL**: 가벼운 일상 대화를 원하는 사용자끼리 매칭
- **SERIOUS**: 진지한 고민이나 대화를 원하는 사용자끼리 매칭
- **RANDOM**: 모든 사용자와 랜덤 매칭

## 기술 스택

- **Spring WebFlux**: 비동기 논블로킹 처리
- **SSE (Server-Sent Events)**: 실시간 메시지 전송
- **Reactor**: 리액티브 프로그래밍
- **Project Reactor Sinks**: 멀티캐스트 메시지 브로드캐스팅

## 주요 클래스

- `ChatController`: SSE 엔드포인트 및 채팅 API
- `ChatMatchingService`: 매칭 요청 관리 및 브로드캐스트
- `ChatRoomManager`: 채팅방 생성, 메시지 전송, 타임아웃 관리
- `ChatRoom`: 채팅방 도메인 (5분 만료 시간 포함)
- `AnonymousUser`: 익명 사용자 (자동 생성 닉네임)
