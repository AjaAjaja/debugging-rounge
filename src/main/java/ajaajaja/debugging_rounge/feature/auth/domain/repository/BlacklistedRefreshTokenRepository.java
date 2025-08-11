package ajaajaja.debugging_rounge.feature.auth.domain.repository;

import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistedRefreshTokenRepository extends JpaRepository<BlacklistedRefreshToken, Long> {
    Boolean existsByRefreshToken(String refreshToken);
}
