package hyunwoo.com.bambooforest.domain.oauth.application.dto;

public record BsmTokenRequest(
        String clientId,
        String clientSecret,
        String authCode
) { }
