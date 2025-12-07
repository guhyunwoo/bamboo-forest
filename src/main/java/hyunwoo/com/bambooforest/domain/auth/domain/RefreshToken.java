package hyunwoo.com.bambooforest.domain.auth.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Table("tbl_refresh_token")
public class RefreshToken {
	@Id
	@Column("refresh_token_id")
	private Long refreshTokenId;

	@Column("user_id")
	private Long userId;

	@Column("token")
	private String token;

	@Column("expires_at")
	private LocalDateTime expiresAt;

	public RefreshToken(Long refreshTokenId, Long userId, String token, LocalDateTime expiresAt) {
		this.refreshTokenId = refreshTokenId;
		this.userId = userId;
		this.token = token;
		this.expiresAt = expiresAt;
	}

	public boolean isExpired() {
		return LocalDateTime.now().isAfter(expiresAt);
	}
}
