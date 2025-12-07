package hyunwoo.com.bambooforest.domain.oauth.controller;

import hyunwoo.com.bambooforest.domain.oauth.application.service.BsmOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class OAuthController {
	private final BsmOAuthService bsmOAuthService;

	@GetMapping("/login")
	public Mono<ResponseEntity<Void>> login() {
		String redirectUrl = bsmOAuthService.getOAuthRedirectUrl();
		return Mono.just(
			ResponseEntity.status(HttpStatus.FOUND)
				.location(URI.create(redirectUrl))
				.build()
		);
	}

	@GetMapping("/callback")
	public Mono<ResponseEntity<Void>> callback(@RequestParam String code) {
		return bsmOAuthService.handleCallback(code)
			.map(token -> {
				String frontEndRedirectUrl = bsmOAuthService.getFrontEndRedirectUrl(token);
				return ResponseEntity.status(HttpStatus.FOUND)
					.location(URI.create(frontEndRedirectUrl))
					.build();
			});
	}
}
