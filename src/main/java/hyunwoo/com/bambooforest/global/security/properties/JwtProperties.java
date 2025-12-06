package hyunwoo.com.bambooforest.global.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(
        long accessTime,
        String prefix,
        String header,
        String secretKey
) { }
