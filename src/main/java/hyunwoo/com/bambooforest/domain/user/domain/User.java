package hyunwoo.com.bambooforest.domain.user.domain;

import hyunwoo.com.bambooforest.domain.user.domain.type.Role;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("tbl_user")
public class User {
	@Id
	private Long userId;


	private Role role;
}
