# Bamboo Forest API 명세서

## 개요
Bamboo Forest는 BSM OAuth 기반 익명 채팅 서비스입니다.

Base URL: `http://localhost:8080`

## 인증

### 일반 회원가입
```
POST /auth/register
```

**Request Body**
```json
{
  "name": "홍길동",
  "email": "hong@example.com",
  "password": "password123",
  "studentNumber": "1234",
  "studentGrade": 2,
  "studentClass": 3
}
```

**Response**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**설명**
- 모든 회원은 자동으로 STUDENT 역할로 가입됩니다.

**에러 응답**
- `409 CONFLICT`: 이미 존재하는 이메일

---

### 로그인
```
POST /auth/login
```

**Request Body**
```json
{
  "email": "hong@example.com",
  "password": "password123"
}
```

**Response**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**에러 응답**
- `401 UNAUTHORIZED`: 이메일 또는 비밀번호가 올바르지 않음

---

### 토큰 갱신
```
POST /auth/refresh
```

**Request Body**
```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Response**
```json
"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**설명**
- 새로운 Access Token을 반환합니다.
- Refresh Token은 30일 유효합니다.

**에러 응답**
- `401 UNAUTHORIZED`: 유효하지 않은 리프레시 토큰
- `401 UNAUTHORIZED`: 만료된 리프레시 토큰

---

## OAuth (BSM)

### BSM OAuth 로그인 시작
```
GET /oauth/login
```

**Response**
- `302 FOUND`: BSM OAuth 인증 페이지로 리다이렉트

**설명**
- BSM OAuth 로그인 플로우를 시작합니다.

---

### BSM OAuth 콜백
```
GET /oauth/callback?code={authorizationCode}
```

**Query Parameters**
- `code` (required): BSM OAuth 인증 코드

**Response**
- `302 FOUND`: 프론트엔드 콜백 URL로 리다이렉트 (토큰 포함)

**설명**
- BSM OAuth 인증 완료 후 호출됩니다.
- 사용자 정보를 조회하고 회원가입/로그인을 처리합니다.
- 프론트엔드 리다이렉트 URL: `http://localhost:3000/callback?token={accessToken}`

---

## 채팅 매칭

### 매칭 요청 생성
```
POST /chat/match/request
```

**인증 필요**: Yes (JWT Bearer Token)

**Request Body**
```json
{
  "chatMode": "SOFT_COFFEE_CHAT"
}
```

**ChatMode 옵션**
- `SOFT_COFFEE_CHAT`: 가벼운 커피챗
- `HARD_COFFEE_CHAT`: 매운 커피챗
- `BEER_CHAT`: 맥주챗

**Response**
```json
{
  "requestId": "550e8400-e29b-41d4-a716-446655440000",
  "userId": 1,
  "chatMode": "SOFT_COFFEE_CHAT",
  "createdAt": "2025-12-07T10:30:00"
}
```

---

### 매칭 요청 구독 (SSE)
```
GET /chat/match/subscribe
```

**인증 필요**: No

**Response (Server-Sent Events)**
```
event: match-request
data: {"requestId":"550e8400-e29b-41d4-a716-446655440000","userId":1,"chatMode":"SOFT_COFFEE_CHAT","createdAt":"2025-12-07T10:30:00"}

event: heartbeat
: keep-alive
```

**설명**
- 실시간으로 새로운 매칭 요청을 수신합니다.
- SSE 연결을 유지하기 위해 30초마다 heartbeat 이벤트가 전송됩니다.

---

### 매칭 수락
```
POST /chat/match/accept/{requestId}
```

**인증 필요**: Yes (JWT Bearer Token)

**Path Parameters**
- `requestId`: 매칭 요청 ID

**Response**
```json
{
  "roomId": "room-550e8400-e29b-41d4-a716-446655440000",
  "chatMode": "SOFT_COFFEE_CHAT",
  "creatingUserId": 1,
  "acceptingUserId": 2,
  "expiresAt": "2025-12-07T11:30:00"
}
```

**설명**
- 매칭을 수락하고 채팅방을 생성합니다.
- 채팅방은 기본적으로 1시간 후 만료됩니다.

---

### 매칭 취소
```
DELETE /chat/match/cancel/{requestId}
```

**인증 필요**: Yes (JWT Bearer Token)

**Path Parameters**
- `requestId`: 매칭 요청 ID

**Response**
- `200 OK`

---

## 채팅

### 메시지 전송
```
POST /chat/message
```

**인증 필요**: Yes (JWT Bearer Token)

**Request Body**
```json
{
  "roomId": "room-550e8400-e29b-41d4-a716-446655440000",
  "content": "안녕하세요!"
}
```

**Response**
- `200 OK`

**에러 응답**
- `400 BAD_REQUEST`: 채팅방을 찾을 수 없음
- `400 BAD_REQUEST`: 사용자가 채팅방에 속하지 않음

---

### 채팅방 메시지 구독 (SSE)
```
GET /chat/room/{roomId}/subscribe
```

**인증 필요**: No

**Path Parameters**
- `roomId`: 채팅방 ID

**Response (Server-Sent Events)**
```
event: message
data: {"roomId":"room-550e8400-e29b-41d4-a716-446655440000","senderId":1,"content":"안녕하세요!","sentAt":"2025-12-07T10:35:00","messageType":"USER"}

event: heartbeat
: keep-alive
```

**messageType 옵션**
- `USER`: 일반 사용자 메시지
- `SYSTEM`: 시스템 메시지

**설명**
- 실시간으로 채팅방의 메시지를 수신합니다.
- SSE 연결을 유지하기 위해 30초마다 heartbeat 이벤트가 전송됩니다.

---

### 채팅방 종료
```
POST /chat/room/{roomId}/close
```

**인증 필요**: Yes (JWT Bearer Token)

**Path Parameters**
- `roomId`: 채팅방 ID

**Response**
- `200 OK`

**설명**
- 채팅방을 즉시 종료합니다.

---

### 채팅 시간 연장 요청
```
POST /chat/room/{roomId}/extend
```

**인증 필요**: Yes (JWT Bearer Token)

**Path Parameters**
- `roomId`: 채팅방 ID

**Response**
```json
{
  "roomId": "room-550e8400-e29b-41d4-a716-446655440000",
  "creatingUserRequested": true,
  "acceptingUserRequested": false,
  "extended": false,
  "expiresAt": "2025-12-07T11:30:00"
}
```

**설명**
- 채팅 시간 연장을 요청합니다.
- 양측 사용자가 모두 요청하면 `extended`가 `true`가 되며 채팅방 만료 시간이 30분 연장됩니다.

---

## 에러 응답 형식

모든 에러 응답은 다음 형식을 따릅니다:

```json
{
  "status": 401,
  "message": "이메일 또는 비밀번호가 올바르지 않습니다."
}
```

### 주요 에러 코드

| HTTP Status | ErrorCode | 설명 |
|-------------|-----------|------|
| 401 | EXPIRED_TOKEN | 만료된 JWT 토큰 |
| 401 | INVALID_TOKEN | 올바르지 않은 JWT 토큰 |
| 401 | INVALID_EMAIL_OR_PASSWORD | 이메일 또는 비밀번호 불일치 |
| 401 | INVALID_REFRESH_TOKEN | 유효하지 않은 리프레시 토큰 |
| 401 | EXPIRED_REFRESH_TOKEN | 만료된 리프레시 토큰 |
| 404 | USER_NOT_FOUND | 사용자를 찾을 수 없음 |
| 409 | EMAIL_ALREADY_EXISTS | 이미 존재하는 이메일 |
| 400 | INVALID_INPUT_VALUE | 올바르지 않은 입력값 |
| 405 | METHOD_NOT_ALLOWED | 잘못된 HTTP 메서드 |
| 500 | INTERNAL_SERVER_ERROR | 서버 에러 |

---

## 인증 헤더

인증이 필요한 엔드포인트는 다음 헤더를 포함해야 합니다:

```
Authorization: Bearer {accessToken}
```
