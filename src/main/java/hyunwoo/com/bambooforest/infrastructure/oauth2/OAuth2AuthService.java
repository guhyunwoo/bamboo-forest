package hyunwoo.com.bambooforest.infrastructure.oauth2;

import reactor.core.publisher.Mono;

import java.util.Map;

public interface OAuth2AuthService {
	Mono<Map<String, Object>> getUserInfo(String accessToken);
}
