package ajaajaja.debugging_rounge.feature.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "refresh_token",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_refresh_token_user",
                        columnNames = {"user_id"}     // 사용자당 1개 제한
                ),
                @UniqueConstraint(
                        name = "uk_refresh_token_hash",
                        columnNames = {"token_hash"}
                )
        },
        indexes = {
                @Index(name = "idx_refresh_token_hash_user", columnList = "token_hash, user_id")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(nullable = false, columnDefinition = "BINARY(32)")
    private byte[] tokenHash;

    @Column(nullable = false)
    private Long userId;

    public RefreshToken(byte[] tokenHash, Long userId) {
        this.tokenHash = tokenHash;
        this.userId = userId;
    }

    public static RefreshToken of(byte[] tokenHash, Long userId) {
        return new RefreshToken(tokenHash, userId);
    }
}
