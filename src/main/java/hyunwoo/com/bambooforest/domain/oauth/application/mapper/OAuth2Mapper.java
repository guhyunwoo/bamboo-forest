package hyunwoo.com.bambooforest.domain.oauth.application.mapper;

import hyunwoo.com.bambooforest.domain.oauth.domain.OAuthUserInfo;
import hyunwoo.com.bambooforest.domain.user.domain.User;

public final class OAuth2Mapper {

	private OAuth2Mapper() {
	}

	public static User toUserEntity(OAuthUserInfo oAuthUserInfo) {
		return new User(
			null,
			oAuthUserInfo.getName(),
			oAuthUserInfo.getEmail(),
			null,
			oAuthUserInfo.getStudentNumber(),
			oAuthUserInfo.getGrade(),
			oAuthUserInfo.getClassNumber(),
			oAuthUserInfo.getRole()
		);
	}
}
