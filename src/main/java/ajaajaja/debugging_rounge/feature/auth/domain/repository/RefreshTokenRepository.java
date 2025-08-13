package ajaajaja.debugging_rounge.feature.auth.domain.repository;

import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByTokenHashAndUserId(byte[] tokenHash, Long userId);
    Optional<RefreshToken> findByUserId(Long userId);
    List<RefreshToken> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    @Modifying(clearAutomatically = true)
    @Query("update RefreshToken rt" +
            " set rt.tokenHash = :newRefreshTokenHash" +
            " where rt.userId = :userId" +
            " and rt.tokenHash = :oldRefreshTokenHash " +
            "and :newRefreshTokenHash <> :oldRefreshTokenHash")
    int rotate(@Param("userId") Long userId,
               @Param("oldRefreshTokenHash") byte[] oldRefreshTokenHash,
               @Param("newRefreshTokenHash") byte[] newRefreshTokenHash);

}
