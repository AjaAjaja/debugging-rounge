package ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.auth.application.port.out.BlacklistedRefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.BlacklistedRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BlacklistedRefreshTokenRepositoryAdapter implements BlacklistedRefreshTokenPort {

    private final BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    @Override
    public BlacklistedRefreshToken revoke(BlacklistedRefreshToken blacklistedRefreshToken) {
        return blacklistedRefreshTokenRepository.save(blacklistedRefreshToken);
    }

    @Override
    public List<BlacklistedRefreshToken> revokeAll(List<BlacklistedRefreshToken> blacklistedRefreshTokenList) {
        return blacklistedRefreshTokenRepository.saveAll(blacklistedRefreshTokenList);
    }

    @Override
    public Boolean isRevoked(byte[] tokenHash) {
        return blacklistedRefreshTokenRepository.existsByTokenHash(tokenHash);
    }

    @Override
    public int insertIgnore(byte[] tokenHash, Long userId) {
        return blacklistedRefreshTokenRepository.insertIgnore(tokenHash, userId);
    }
}
