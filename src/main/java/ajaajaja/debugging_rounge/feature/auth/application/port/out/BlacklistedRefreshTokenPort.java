package ajaajaja.debugging_rounge.feature.auth.application.port.out;

import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;

public interface BlacklistedRefreshTokenPort {

    BlacklistedRefreshToken revoke(BlacklistedRefreshToken blacklistedRefreshToken);

    Boolean isRevoked(byte[] tokenHash);

    int insertIfNotExists(byte[] tokenHash, Long userId);
}
