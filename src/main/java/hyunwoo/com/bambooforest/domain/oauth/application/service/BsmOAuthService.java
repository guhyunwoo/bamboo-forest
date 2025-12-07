package hyunwoo.com.bambooforest.domain.oauth.application.service;

import hyunwoo.com.bambooforest.global.security.properties.BsmOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BsmOAuthService {
	private final BsmOAuthProperties bsmOAuthProperties;
	private final OAuth2AuthFacade oAuth2AuthFacade;

	public String getOAuthRedirectUrl() {
		return bsmOAuthProperties.oauthUri() + "?" +
			"clientId=" + bsmOAuthProperties.clientId() + "&" +
			"redirectURI=" + bsmOAuthProperties.redirectUri();
	}

	public Mono<String> handleCallback(String authCode) {
		return oAuth2AuthFacade.bsmLogin(authCode);
	}

	public String getFrontEndRedirectUrl(String token) {
		return bsmOAuthProperties.frontEndRedirectUri() + "?token=" + token;
	}
}
