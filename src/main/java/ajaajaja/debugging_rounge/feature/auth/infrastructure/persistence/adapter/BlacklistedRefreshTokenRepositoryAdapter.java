package ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.auth.application.port.out.BlacklistedRefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.BlacklistedRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlacklistedRefreshTokenRepositoryAdapter implements BlacklistedRefreshTokenPort {

    private final BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    @Override
    public Boolean isRevoked(byte[] tokenHash) {
        return blacklistedRefreshTokenRepository.existsByTokenHash(tokenHash);
    }

    @Override
    public void addToBlacklist(byte[] tokenHash, Long userId) {
        blacklistedRefreshTokenRepository.insertIgnore(tokenHash, userId);
    }

    @Override
    public void addAllToBlacklist(List<byte[]> tokenHashes, Long userId) {
        List<BlacklistedRefreshToken> blacklisted = tokenHashes.stream()
                .map(tokenHash -> BlacklistedRefreshToken.of(tokenHash, userId))
                .toList();
        blacklistedRefreshTokenRepository.saveAll(blacklisted);
    }
}
