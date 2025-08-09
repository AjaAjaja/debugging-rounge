package ajaajaja.debugging_rounge.feature.auth.domain.repository;

import ajaajaja.debugging_rounge.feature.auth.domain.BlackRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackRefreshTokenRepository extends JpaRepository<BlackRefreshToken, Long> {
    Boolean existsByRefreshToken(String refreshToken);
}
