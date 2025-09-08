package ajaajaja.debugging_rounge.feature.auth.application.port.out;

import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;

import java.util.List;

public interface BlacklistedRefreshTokenPort {

    BlacklistedRefreshToken revoke(BlacklistedRefreshToken blacklistedRefreshToken);

    List<BlacklistedRefreshToken> revokeAll(List<BlacklistedRefreshToken> blacklistedRefreshTokenList);

    Boolean isRevoked(byte[] tokenHash);

    int insertIgnore(byte[] tokenHash, Long userId);
}
