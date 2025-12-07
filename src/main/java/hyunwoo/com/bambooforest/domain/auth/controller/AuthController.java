package hyunwoo.com.bambooforest.domain.auth.controller;

import hyunwoo.com.bambooforest.domain.auth.application.dto.request.AuthLoginRequest;
import hyunwoo.com.bambooforest.domain.auth.application.dto.request.AuthRegisterRequest;
import hyunwoo.com.bambooforest.domain.auth.application.dto.request.RefreshTokenRequest;
import hyunwoo.com.bambooforest.domain.auth.application.dto.response.AuthTokenResponse;
import hyunwoo.com.bambooforest.domain.auth.application.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final AuthService authService;

	@PostMapping("/register")
	public Mono<AuthTokenResponse> register(@RequestBody AuthRegisterRequest request) {
		return authService.register(request);
	}

	@PostMapping("/login")
	public Mono<AuthTokenResponse> login(@RequestBody AuthLoginRequest request) {
		return authService.login(request);
	}

	@PostMapping("/refresh")
	public Mono<String> refresh(@RequestBody RefreshTokenRequest request) {
		return authService.refresh(request);
	}
}
