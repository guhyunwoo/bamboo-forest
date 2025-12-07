package hyunwoo.com.bambooforest.global.security.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bsm.oauth")
public record BsmOAuthProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String oauthUri,
        String tokenUri,
        String resourceUri,
        String frontEndRedirectUri
) { }
