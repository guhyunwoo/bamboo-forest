package hyunwoo.com.bambooforest.global.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import reactor.core.publisher.Mono;

public class SecurityUtil {

	public static Mono<Long> getCurrentUserId() {
		return ReactiveSecurityContextHolder.getContext()
			.map(securityContext -> securityContext.getAuthentication())
			.filter(Authentication::isAuthenticated)
			.map(Authentication::getName)
			.map(Long::parseLong)
			.switchIfEmpty(Mono.error(new IllegalStateException("User not authenticated")));
	}
}
