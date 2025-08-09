package ajaajaja.debuging_rounge.global.auth.refreshtoken.repository;

import ajaajaja.debuging_rounge.global.auth.refreshtoken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByRefreshTokenAndUserId(String refreshToken, Long userId);

    void deleteByUserId(Long userId);

    List<RefreshToken> findAllByUserId(Long userId);
    void deleteAllByUserId(Long userId);

    void deleteByRefreshToken(String refreshToken);

}
