package ajaajaja.debugging_rounge.feature.auth.domain.repository;

import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Boolean existsByUserIdAndRefreshToken(Long userId, String refreshToken);
    Optional<RefreshToken> findByUserId(Long userId);
    List<RefreshToken> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);
    @Modifying(clearAutomatically = true)
    @Query("update RefreshToken rt" +
            " set rt.refreshToken = :newRefreshToken" +
            " where rt.userId = :userId" +
            " and rt.refreshToken = :oldRefreshToken " +
            "and :newRefreshToken <> :oldRefreshToken")
    int rotate(Long userId, String oldRefreshToken, String newRefreshToken);

}
