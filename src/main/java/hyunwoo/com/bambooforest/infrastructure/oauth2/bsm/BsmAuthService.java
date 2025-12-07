package hyunwoo.com.bambooforest.infrastructure.oauth2.bsm;

import hyunwoo.com.bambooforest.domain.oauth.application.dto.BsmResourceResponse;
import hyunwoo.com.bambooforest.infrastructure.oauth2.OAuth2AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BsmAuthService implements OAuth2AuthService {
	private final BsmAuthClient bsmAuthClient;

	@Override
	public Mono<Map<String, Object>> getUserInfo(String authorizationCode) {
		return bsmAuthClient.exchangeToken(authorizationCode)
			.flatMap(bsmAuthClient::fetchUserResource)
			.map(this::convertToMap);
	}

	private Map<String, Object> convertToMap(BsmResourceResponse response) {
		Map<String, Object> map = new HashMap<>();
		map.put("name", response.name());
		map.put("email", response.email());
		map.put("studentNo", response.studentNo());
		map.put("grade", response.grade());
		map.put("classNo", response.classNo());
		map.put("role", response.role());
		return map;
	}
}
