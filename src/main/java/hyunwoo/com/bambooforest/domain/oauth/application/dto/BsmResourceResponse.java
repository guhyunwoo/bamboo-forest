package hyunwoo.com.bambooforest.domain.oauth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BsmResourceResponse(
	String name,
	String email,
	@JsonProperty("studentNo")
	String studentNo,
	Integer grade,
	@JsonProperty("classNo")
	Integer classNo,
	String role
) {
}
