package ajaajaja.debugging_rounge.feature.auth.application;

import ajaajaja.debugging_rounge.feature.auth.api.exception.RefreshTokenInvalidException;
import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.BlacklistedRefreshTokenRepository;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthSessionService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    public void rotateToken(byte[] oldTokenHash, byte[] newTokenHash, Long userId) {
        int changed = refreshTokenRepository.rotate(userId, oldTokenHash, newTokenHash);
        if (changed != 1) {
            throw new RefreshTokenInvalidException();
        }
        blacklistedRefreshTokenRepository.save(BlacklistedRefreshToken.of(oldTokenHash, userId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void killAllSessions(Long userId) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId);
        List<BlacklistedRefreshToken> blacklisted = refreshTokens.stream()
                .map(rt -> BlacklistedRefreshToken.of(rt.getTokenHash(), rt.getUserId())).toList();
        blacklistedRefreshTokenRepository.saveAll(blacklisted);
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    public void revokeToken(byte[] tokenHash) {
        var optionalRefreshToken = refreshTokenRepository.findByTokenHash(tokenHash);
        if (optionalRefreshToken.isEmpty()) {
            blacklistedRefreshTokenRepository.insertIgnore(tokenHash, null);
            return;
        }

        Long userId = optionalRefreshToken.get().getUserId();
        blacklistedRefreshTokenRepository.insertIgnore(tokenHash, userId);

        refreshTokenRepository.deleteByTokenHashAndUserId(tokenHash, userId);
    }

}
