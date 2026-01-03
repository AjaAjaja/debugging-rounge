package ajaajaja.debugging_rounge.feature.auth.application.port.out;

import java.util.List;

public interface BlacklistedRefreshTokenPort {

    Boolean isRevoked(byte[] tokenHash);

    void addToBlacklist(byte[] tokenHash, Long userId);

    void addAllToBlacklist(List<byte[]> tokenHashes, Long userId);
}
