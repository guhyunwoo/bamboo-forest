package hyunwoo.com.bambooforest.domain.oauth.application.dto;

public record BsmResourceRequest(
        String clientId,
        String clientSecret,
        String token
) { }
