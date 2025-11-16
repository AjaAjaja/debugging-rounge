package ajaajaja.debugging_rounge.feature.auth.application;

import ajaajaja.debugging_rounge.common.jwt.exception.RefreshTokenInvalidException;
import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.*;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.BlacklistedRefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.JwtPort;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.RefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.TokenHasherPort;
import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthFacade implements IssueTokensUseCase, ReissueTokensUseCase, LogoutUseCase {

    private final JwtPort jwtPort;
    private final TokenHasherPort tokenHasherPort;
    private final RefreshTokenPort refreshTokenPort;
    private final BlacklistedRefreshTokenPort blacklistedRefreshTokenPort;

    @Override
    @Transactional
    public TokenPair issueTokens(Long userId) {

        String newRefreshToken = jwtPort.createRefreshToken(userId);
        byte[] newRefreshTokenHash = tokenHasherPort.hash(newRefreshToken);

        Optional<RefreshToken> optionalRefreshToken = refreshTokenPort.findByUserId(userId);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken oldRefreshToken = optionalRefreshToken.get();
            rotateToken(oldRefreshToken.getTokenHash(), newRefreshTokenHash, userId);
        } else {
            refreshTokenPort.save(RefreshToken.of(newRefreshTokenHash, userId));
        }

        String newAccessToken = jwtPort.createAccessToken(userId);
        return TokenPair.of(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public TokenPair reissueTokens(String rawRefreshToken, Long userId) {

        byte[] oldRefreshTokenHash = tokenHasherPort.hash(rawRefreshToken);
        if (blacklistedRefreshTokenPort.isRevoked(oldRefreshTokenHash)) {
            killAllSessions(userId);
            throw new RefreshTokenInvalidException();
        }

        Boolean owned = refreshTokenPort.existsByTokenHashAndUserId(oldRefreshTokenHash, userId);
        if (!owned) {
            killAllSessions(userId);
            throw new RefreshTokenInvalidException();
        }

        String newRefreshToken = jwtPort.createRefreshToken(userId);
        byte[] newRefreshTokenHash = tokenHasherPort.hash(newRefreshToken);

        rotateToken(oldRefreshTokenHash, newRefreshTokenHash, userId);

        String newAccessToken = jwtPort.createAccessToken(userId);
        return TokenPair.of(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }
        byte[] tokenHash = tokenHasherPort.hash(refreshToken);
        revokeToken(tokenHash);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void killAllSessions(Long userId) {
        List<RefreshToken> refreshTokens = refreshTokenPort.findAllByUserId(userId);
        List<BlacklistedRefreshToken> blacklisted = refreshTokens.stream()
                .map(rt -> BlacklistedRefreshToken.of(rt.getTokenHash(), rt.getUserId())).toList();
        refreshTokenPort.killAllSessions(blacklisted, userId);
    }

    private void revokeToken(byte[] tokenHash) {
        Optional<RefreshToken> optionalRefreshToken = refreshTokenPort.findByTokenHash(tokenHash);
        if (optionalRefreshToken.isEmpty()) {
            blacklistedRefreshTokenPort.insertIfNotExists(tokenHash, null);
            return;
        }

        Long userId = optionalRefreshToken.get().getUserId();
        blacklistedRefreshTokenPort.insertIfNotExists(tokenHash, userId);

        refreshTokenPort.deleteByTokenHashAndUserId(tokenHash, userId);
    }

    private void rotateToken(byte[] oldTokenHash, byte[] newTokenHash, Long userId) {
        int changed = refreshTokenPort.rotate(oldTokenHash, newTokenHash, userId);
        if (changed != 1) {
            throw new RefreshTokenInvalidException();
        }
        blacklistedRefreshTokenPort.revoke(BlacklistedRefreshToken.of(oldTokenHash, userId));
    }

}
