package ajaajaja.debuging_rounge.global.auth.refreshtoken.repository;

import ajaajaja.debuging_rounge.global.auth.refreshtoken.entity.BlackRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackRefreshTokenRepository extends JpaRepository<BlackRefreshToken, Long> {
    Boolean existsByRefreshToken(String refreshToken);
}
