package hyunwoo.com.bambooforest.global.config;

import hyunwoo.com.bambooforest.global.security.properties.JwtProperties;
import hyunwoo.com.bambooforest.global.security.util.TokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthFilter implements WebFilter {
	private final JwtProperties jwtProperties;
	private final TokenResolver tokenResolver;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		final String headerValue = exchange.getRequest().getHeaders().getFirst(jwtProperties.header());

		return tokenResolver.stripBearerScheme(headerValue)
			.flatMap(tokenResolver::getAuthentication)
			.flatMap(authentication -> chain.filter(exchange)
				.contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
			)
			.onErrorResume((exception) -> chain.filter(exchange));
	}
}
