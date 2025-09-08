package ajaajaja.debugging_rounge.feature.auth.application.port.out;

import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenPort {
    Optional<RefreshToken> findByUserId(Long userId);

    List<RefreshToken> findAllByUserId(Long userId);

    void deleteByTokenHashAndUserId(byte[] tokenHash, Long userId);

    void killAllSessions(List<BlacklistedRefreshToken> blacklisted, Long userId);

    void save(RefreshToken refreshToken);

    Boolean existsByTokenHashAndUserId(byte[] tokenHash, Long userId);

    Optional<RefreshToken> findByTokenHash(byte[] tokenHash);

    int rotate(byte[] oldTokenHash, byte[] newTokenHash, Long userId);
}
