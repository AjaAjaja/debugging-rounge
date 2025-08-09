package ajaajaja.debugging_rounge.feature.auth.api.application;

import ajaajaja.debugging_rounge.feature.auth.infrastructure.security.JwtProvider;
import ajaajaja.debugging_rounge.feature.auth.domain.TokenType;
import ajaajaja.debugging_rounge.common.config.jwt.JwtProperties;
import ajaajaja.debugging_rounge.feature.auth.api.exception.RefreshTokenInvalidException;
import ajaajaja.debugging_rounge.feature.auth.domain.BlackRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.BlackRefreshTokenRepository;
import ajaajaja.debugging_rounge.feature.auth.domain.repository.RefreshTokenRepository;
import ajaajaja.debugging_rounge.feature.auth.api.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackRefreshTokenRepository blackRefreshTokenRepository;

    public TokenDto issueTokens(Long userId) {
        String accessToken = createAccessToken(userId);

        refreshTokenRepository.findByUserId(userId).ifPresent(this::addBlackList);
        String refreshToken = createRefreshToken(userId);
        refreshTokenRepository.save(RefreshToken.of(refreshToken, userId));

        return TokenDto.of(accessToken, refreshToken);
    }

    public String createAccessToken(Long userId) {
        return jwtProvider.createToken(userId.toString(), TokenType.ACCESS);
    }

    public String createRefreshToken(Long userId) {
        return jwtProvider.createToken(userId.toString(), TokenType.REFRESH);
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

    @Transactional
    public TokenDto reIssueTokens(Long userId, String refreshToken) {

        validateRefreshToken(userId, refreshToken);

        Instant expiryInstant = jwtProvider.extractExpiration(refreshToken).toInstant();
        Duration remaining = Duration.between(Instant.now(), expiryInstant);
        Duration renewThreshold = jwtProperties.getToken().getRenewThreshold();
        if (remaining.compareTo(renewThreshold) <= 0) {
            refreshTokenRepository.deleteByRefreshToken(refreshToken);
            blackRefreshTokenRepository.save(BlackRefreshToken.of(refreshToken, userId));
            return issueTokens(userId);
        }
        return TokenDto.of(createAccessToken(userId), null);
    }

    private void validateRefreshToken(Long userId, String refreshToken) {
        if (blackRefreshTokenRepository.existsByRefreshToken(refreshToken)) {
            refreshTokenRepository.findAllByUserId(userId).forEach(this::addBlackList);
            refreshTokenRepository.deleteAllByUserId(userId);
            throw new RefreshTokenInvalidException();
        }
    }

    private void addBlackList(RefreshToken refreshToken) {
        blackRefreshTokenRepository.save(BlackRefreshToken.of(refreshToken.getRefreshToken(), refreshToken.getUserId()));
    }


}
