package hyunwoo.com.bambooforest.domain.user.domain;

import hyunwoo.com.bambooforest.domain.user.domain.type.Role;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Table("tbl_user")
public class User {
	@Id
	@Column("user_id")
	private Long userId;

	@Column("name")
	private String name;

	@Column("email")
	private String email;

	@Column("password")
	private String password;

	@Column("student_number")
	private String studentNumber;

	@Column("student_grade")
	private Integer studentGrade;

	@Column("student_class")
	private Integer studentClass;

	@Column("role")
	private Role role;

	public User(Long userId, String name, String email, String password,
	            String studentNumber, Integer studentGrade, Integer studentClass, Role role) {
		this.userId = userId;
		this.name = name;
		this.email = email;
		this.password = password;
		this.studentNumber = studentNumber;
		this.studentGrade = studentGrade;
		this.studentClass = studentClass;
		this.role = role;
	}
}
