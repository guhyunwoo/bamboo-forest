package hyunwoo.com.bambooforest.domain.oauth.domain;

import hyunwoo.com.bambooforest.domain.user.domain.type.Role;

public interface OAuthUserInfo {
    String getProviderId();
    String getName();
    String getEmail();
    String getStudentNumber();
    Integer getGrade();
    Integer getClassNumber();
    Role getRole();
}
