package hyunwoo.com.bambooforest.domain.oauth.domain;

import hyunwoo.com.bambooforest.domain.user.domain.type.Role;

import java.util.Map;

public class BsmOAuth2UserInfo implements OAuthUserInfo {
	private final Map<String, Object> attributes;

	public BsmOAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getProviderId() {
		return String.valueOf(attributes.get("code"));
	}

	@Override
	public String getName() {
		return (String) attributes.get("name");
	}

	@Override
	public String getEmail() {
		return (String) attributes.get("email");
	}

	@Override
	public String getStudentNumber() {
		return (String) attributes.get("studentNo");
	}

	@Override
	public Integer getGrade() {
		return (Integer) attributes.get("grade");
	}

	@Override
	public Integer getClassNumber() {
		return (Integer) attributes.get("classNo");
	}

	@Override
	public Role getRole() {
		String role = (String) attributes.get("role");
		return Role.valueOf(role.toUpperCase());
	}
}
