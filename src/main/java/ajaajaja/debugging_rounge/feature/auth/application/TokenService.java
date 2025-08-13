package ajaajaja.debugging_rounge.feature.auth.application;

import ajaajaja.debugging_rounge.common.config.jwt.JwtProperties;
import ajaajaja.debugging_rounge.common.config.jwt.TokenHasher;
import ajaajaja.debugging_rounge.feature.auth.api.dto.TokenDto;
import ajaajaja.debugging_rounge.feature.auth.api.exception.RefreshTokenInvalidException;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.TokenType;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.BlacklistedRefreshTokenRepository;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.RefreshTokenRepository;
import ajaajaja.debugging_rounge.common.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final TokenHasher tokenHasher;
    private final AuthSessionService authSessionService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    @Transactional
    public TokenDto issueTokens(Long userId) {

        String newRefreshToken = createRefreshToken(userId);
        byte[] newRefreshTokenHash = tokenHasher.hmacSha256(newRefreshToken);

        Optional<RefreshToken> optionalRefreshToken = refreshTokenRepository.findByUserId(userId);
        if (optionalRefreshToken.isPresent()) {
            RefreshToken oldRefreshToken = optionalRefreshToken.get();
            authSessionService.rotateToken(oldRefreshToken.getTokenHash(), newRefreshTokenHash, userId);
        } else {
            refreshTokenRepository.save(RefreshToken.of(newRefreshTokenHash, userId));
        }

        String newAccessToken = createAccessToken(userId);
        return TokenDto.of(newAccessToken, newRefreshToken);
    }

    @Transactional
    public TokenDto reissueTokens(Long userId, String refreshToken) {

        byte[] oldRefreshTokenHash = tokenHasher.hmacSha256(refreshToken);
        if (blacklistedRefreshTokenRepository.existsByTokenHash(oldRefreshTokenHash)) {
            authSessionService.killAllSessions(userId);
            throw new RefreshTokenInvalidException();
        }

        Boolean owned = refreshTokenRepository.existsByTokenHashAndUserId(oldRefreshTokenHash, userId);
        if (!owned) {
            authSessionService.killAllSessions(userId);
            throw new RefreshTokenInvalidException();
        }

        String newRefreshToken = createRefreshToken(userId);
        byte[] newRefreshTokenHash = tokenHasher.hmacSha256(newRefreshToken);

        authSessionService.rotateToken(oldRefreshTokenHash, newRefreshTokenHash, userId);

        String newAccessToken = createAccessToken(userId);
        return TokenDto.of(newAccessToken, newRefreshToken);
    }

    public ResponseCookie createRefreshCookie(String refreshToken) {
        return ResponseCookie.from(jwtProperties.getCookie().getName(), refreshToken)
                .httpOnly(jwtProperties.getCookie().isHttpOnly())
                .secure(jwtProperties.getCookie().isSecure())
                .sameSite(jwtProperties.getCookie().getSameSite()) // TODO 추후에 프론트와 같은 도메인으로 만들어 csrf 공격 방지
                .path(jwtProperties.getCookie().getPath())
                .maxAge(jwtProperties.getCookie().getMaxAge())
                .build();
    }

    private String createAccessToken(Long userId) {
        return jwtProvider.createToken(userId.toString(), TokenType.ACCESS);
    }

    private String createRefreshToken(Long userId) {
        return jwtProvider.createToken(userId.toString(), TokenType.REFRESH);
    }
}
