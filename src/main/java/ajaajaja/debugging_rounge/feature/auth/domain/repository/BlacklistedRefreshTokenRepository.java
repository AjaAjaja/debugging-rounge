package ajaajaja.debugging_rounge.feature.auth.domain.repository;

import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlacklistedRefreshTokenRepository extends JpaRepository<BlacklistedRefreshToken, Long> {
    Boolean existsByTokenHash(byte[] tokenHash);

    @Modifying
    @Query(value = "INSERT IGNORE INTO blacklisted_refresh_token (token_hash, user_id, created_date, last_modified_date) " +
            "VALUES (:hash, :userId, NOW(6), NOW(6))", nativeQuery = true)
    int insertIgnore(@Param("hash") byte[] hash, @Param("authorId") Long userId);
}
