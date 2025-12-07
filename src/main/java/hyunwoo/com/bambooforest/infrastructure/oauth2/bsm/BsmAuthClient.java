package hyunwoo.com.bambooforest.infrastructure.oauth2.bsm;

import hyunwoo.com.bambooforest.domain.oauth.application.dto.BsmResourceRequest;
import hyunwoo.com.bambooforest.domain.oauth.application.dto.BsmResourceResponse;
import hyunwoo.com.bambooforest.domain.oauth.application.dto.BsmTokenRequest;
import hyunwoo.com.bambooforest.domain.oauth.application.dto.BsmTokenResponse;
import hyunwoo.com.bambooforest.global.security.properties.BsmOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BsmAuthClient {
	private final WebClient webClient;
	private final BsmOAuthProperties properties;

	public Mono<String> exchangeToken(String authorizationCode) {
		BsmTokenRequest request = new BsmTokenRequest(
			properties.clientId(),
			properties.clientSecret(),
			authorizationCode
		);

		return webClient.post()
			.uri(properties.tokenUri())
			.bodyValue(request)
			.retrieve()
			.bodyToMono(BsmTokenResponse.class)
			.map(BsmTokenResponse::token);
	}

	public Mono<BsmResourceResponse> fetchUserResource(String accessToken) {
		BsmResourceRequest request = new BsmResourceRequest(
			properties.clientId(),
			properties.clientSecret(),
			accessToken
		);

		return webClient.post()
			.uri(properties.resourceUri())
			.bodyValue(request)
			.retrieve()
			.bodyToMono(BsmResourceResponse.class);
	}
}
