package ajaajaja.debugging_rounge.feature.auth.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
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
        name = "blacklisted_refresh_token",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_blacklisted_refresh_token_hash",
                        columnNames = {"token_hash"}
                )
        }
)
public class BlacklistedRefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.BINARY) // Hibernate 6+
    @Column(nullable = false, columnDefinition = "BINARY(32)")
    private byte[] tokenHash;

    private Long userId;

    public BlacklistedRefreshToken(byte[] tokenHash, Long userId) {
        this.tokenHash = tokenHash;
        this.userId = userId;
    }

    public static BlacklistedRefreshToken of(byte[] tokenHash, Long userId) {
        return new BlacklistedRefreshToken(tokenHash, userId);
    }
}
