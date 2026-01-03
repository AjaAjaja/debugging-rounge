package ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.auth.application.port.out.RefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenPort {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Optional<RefreshToken> findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    @Override
    public List<RefreshToken> findAllByUserId(Long userId) {
        return refreshTokenRepository.findAllByUserId(userId);
    }

    @Override
    public void deleteByTokenHashAndUserId(byte[] tokenHash, Long userId) {
        refreshTokenRepository.deleteByTokenHashAndUserId(tokenHash, userId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Boolean existsByTokenHashAndUserId(byte[] tokenHash, Long userId) {
        return refreshTokenRepository.existsByTokenHashAndUserId(tokenHash, userId);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(byte[] tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash);
    }

    @Override
    public int rotate(byte[] oldTokenHash, byte[] newTokenHash, Long userId) {
        return refreshTokenRepository.rotate(oldTokenHash, newTokenHash, userId);
    }

}
