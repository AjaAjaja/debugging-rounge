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

    public void rotateToken(Long userId, String oldRefreshToken, String newRefreshToken) {
        int changed = refreshTokenRepository.rotate(userId, oldRefreshToken, newRefreshToken);
        if (changed != 1) {
            throw new RefreshTokenInvalidException();
        }
        blacklistedRefreshTokenRepository.save(BlacklistedRefreshToken.of(oldRefreshToken, userId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void killAllSessions(Long userId) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findAllByUserId(userId);
        List<BlacklistedRefreshToken> blacklisted = refreshTokens.stream()
                .map(rt -> BlacklistedRefreshToken.of(rt.getRefreshToken(), rt.getUserId())).toList();
        blacklistedRefreshTokenRepository.saveAll(blacklisted);
        refreshTokenRepository.deleteAllByUserId(userId);
    }

}
