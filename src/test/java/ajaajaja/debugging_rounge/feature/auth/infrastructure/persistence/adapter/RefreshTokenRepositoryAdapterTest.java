package ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.BlacklistedRefreshTokenRepository;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.persistence.RefreshTokenRepository;
import ajaajaja.debugging_rounge.support.MysqlJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({RefreshTokenRepositoryAdapter.class, BlacklistedRefreshTokenRepositoryAdapter.class})
class RefreshTokenRepositoryAdapterTest extends MysqlJpaTestSupport {

    @Autowired
    RefreshTokenRepositoryAdapter adapter;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    BlacklistedRefreshTokenRepository blacklistedRefreshTokenRepository;

    // BINARY(32)를 위한 32바이트 해시 생성 헬퍼
    private byte[] createHash(String seed) {
        return Arrays.copyOf(seed.getBytes(), 32);
    }

    @Test
    @DisplayName("새로운 리프레시 토큰을 저장한다")
    void save_새로운토큰_저장() {
        // given
        Long userId = 1L;
        byte[] tokenHash = createHash("test-hash-1");
        RefreshToken refreshToken = RefreshToken.of(tokenHash, userId);

        // when
        adapter.save(refreshToken);

        // then
        Optional<RefreshToken> result = refreshTokenRepository.findByUserId(userId);
        assertThat(result).isPresent();
        assertThat(Arrays.equals(result.get().getTokenHash(), tokenHash)).isTrue();
        assertThat(result.get().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("토큰 해시로 리프레시 토큰을 조회한다")
    void findByTokenHash_토큰조회() {
        // given
        Long userId = 1L;
        byte[] tokenHash = createHash("test-hash-2");
        RefreshToken saved = refreshTokenRepository.save(RefreshToken.of(tokenHash, userId));

        // when
        Optional<RefreshToken> result = adapter.findByTokenHash(tokenHash);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(Arrays.equals(result.get().getTokenHash(), tokenHash)).isTrue();
    }

    @Test
    @DisplayName("사용자 ID로 리프레시 토큰을 조회한다")
    void findByUserId_사용자토큰조회() {
        // given
        Long userId = 1L;
        byte[] tokenHash = createHash("test-hash-3");
        RefreshToken saved = refreshTokenRepository.save(RefreshToken.of(tokenHash, userId));

        // when
        Optional<RefreshToken> result = adapter.findByUserId(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(result.get().getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("사용자의 모든 리프레시 토큰을 조회한다")
    void findAllByUserId_모든토큰조회() {
        // given
        Long userId = 1L;
        byte[] tokenHash1 = createHash("test-hash-4");
        
        // Note: userId는 unique constraint가 있어서 한 사용자당 1개만 가능
        // 하지만 findAllByUserId 메서드는 여러 개를 반환할 수 있도록 설계됨
        refreshTokenRepository.save(RefreshToken.of(tokenHash1, userId));

        // when
        List<RefreshToken> result = adapter.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("토큰을 성공적으로 회전한다")
    void rotate_토큰회전_성공() {
        // given
        Long userId = 1L;
        byte[] oldHash = createHash("old-hash-1");
        byte[] newHash = createHash("new-hash-1");
        refreshTokenRepository.save(RefreshToken.of(oldHash, userId));

        // when
        int affected = adapter.rotate(oldHash, newHash, userId);

        // then
        assertThat(affected).isEqualTo(1);
        
        Optional<RefreshToken> result = refreshTokenRepository.findByUserId(userId);
        assertThat(result).isPresent();
        assertThat(Arrays.equals(result.get().getTokenHash(), newHash)).isTrue();
        
        // 기존 토큰으로는 조회 불가
        assertThat(refreshTokenRepository.findByTokenHash(oldHash)).isEmpty();
    }

    @Test
    @DisplayName("같은 해시값의 토큰으로 회전 시도하면 0을 반환한다")
    void rotate_같은해시_0반환() {
        // given
        Long userId = 1L;
        byte[] sameHash = createHash("same-hash");
        refreshTokenRepository.save(RefreshToken.of(sameHash, userId));

        // when
        int affected = adapter.rotate(sameHash, sameHash, userId);

        // then
        assertThat(affected).isEqualTo(0);
    }

    @Test
    @DisplayName("존재하지 않는 토큰을 회전하면 0을 반환한다")
    void rotate_존재하지않는토큰_0반환() {
        // given
        Long userId = 6L;
        byte[] oldHash = createHash("non-existent-hash");
        byte[] newHash = createHash("new-hash-2");

        // when
        int affected = adapter.rotate(oldHash, newHash, userId);

        // then
        assertThat(affected).isEqualTo(0);
    }

    @Test
    @DisplayName("다른 사용자의 토큰을 회전하면 0을 반환한다")
    void rotate_다른사용자토큰_0반환() {
        // given
        Long userId = 1L;
        Long otherUserId = 999L;
        byte[] oldHash = createHash("old-hash-3");
        byte[] newHash = createHash("new-hash-3");

        refreshTokenRepository.save(RefreshToken.of(oldHash, userId));

        // when
        int affected = adapter.rotate(oldHash, newHash, otherUserId);

        // then
        assertThat(affected).isEqualTo(0);

        // 원래 토큰은 그대로 유지
        Optional<RefreshToken> result = refreshTokenRepository.findByUserId(userId);
        assertThat(result).isPresent();
        assertThat(Arrays.equals(result.get().getTokenHash(), oldHash)).isTrue();
    }

    @Test
    @DisplayName("토큰 삭제 시 블랙리스트에 추가된다")
    void deleteByTokenHashAndUserId_삭제후_블랙리스트추가() {
        // given
        Long userId = 1L;
        byte[] tokenHash = createHash("test-hash-7");
        refreshTokenRepository.save(RefreshToken.of(tokenHash, userId));

        // when
        adapter.deleteByTokenHashAndUserId(tokenHash, userId);

        // then
        // 토큰이 삭제됨
        assertThat(refreshTokenRepository.findByTokenHash(tokenHash)).isEmpty();

        // 블랙리스트에 추가됨
        assertThat(blacklistedRefreshTokenRepository.existsByTokenHash(tokenHash)).isTrue();
    }

    @Test
    @DisplayName("사용자의 모든 세션을 만료시킨다")
    void killAllSessions_모든세션만료() {
        // given
        Long userId = 1L;
        byte[] tokenHash1 = createHash("test-hash-8");

        refreshTokenRepository.save(RefreshToken.of(tokenHash1, userId));

        List<RefreshToken> tokens = refreshTokenRepository.findAllByUserId(userId);
        List<BlacklistedRefreshToken> blacklisted = tokens.stream()
                .map(rt -> BlacklistedRefreshToken.of(rt.getTokenHash(), rt.getUserId()))
                .toList();

        // when
        adapter.killAllSessions(blacklisted, userId);

        // then
        // 모든 토큰이 삭제됨
        assertThat(refreshTokenRepository.findAllByUserId(userId)).isEmpty();

        // 블랙리스트에 추가됨
        assertThat(blacklistedRefreshTokenRepository.existsByTokenHash(tokenHash1)).isTrue();
    }
}

