package ajaajaja.debugging_rounge.feature.auth.application;

import ajaajaja.debugging_rounge.common.jwt.exception.RefreshTokenInvalidException;
import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.BlacklistedRefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.JwtPort;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.RefreshTokenPort;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.TokenHasherPort;
import ajaajaja.debugging_rounge.feature.auth.domain.BlacklistedRefreshToken;
import ajaajaja.debugging_rounge.feature.auth.domain.RefreshToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFacadeTest {

    @Mock
    JwtPort jwtPort;
    @Mock
    TokenHasherPort tokenHasherPort;
    @Mock
    RefreshTokenPort refreshTokenPort;
    @Mock
    BlacklistedRefreshTokenPort blacklistedRefreshTokenPort;

    @InjectMocks
    AuthFacade authFacade;

    private final Long USER_ID = 543L;
    private final String OLD_REFRESH_RAW = "REF.OLD.543";
    private final String NEW_REFRESH_RAW = "REF.NEW.543";
    private final String ACC_RAW = "ACC.543";
    private final String ACC_NEW_RAW = "ACC.NEW.543";

    private final byte[] OLD_HASH = "old-hash".getBytes();
    private final byte[] NEW_HASH = "new-hash".getBytes();
    private final byte[] ANY_HASH = "any-hash".getBytes();


    @Nested
    @DisplayName("issueTokens")
    class IssueTokens {

        @BeforeEach
        void commonIssueToken() {
            when(jwtPort.createRefreshToken(USER_ID)).thenReturn(NEW_REFRESH_RAW);
            when(tokenHasherPort.hash(NEW_REFRESH_RAW)).thenReturn(NEW_HASH);
        }

        @Test
        @DisplayName("토큰 최초 발급")
        void 토큰_최초_발급() {

            // given
            when(refreshTokenPort.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(jwtPort.createAccessToken(USER_ID)).thenReturn(ACC_NEW_RAW);

            // when
            TokenPair tokenPair = authFacade.issueTokens(USER_ID);

            // then
            assertThat(tokenPair.accessToken()).isEqualTo(ACC_NEW_RAW);
            assertThat(tokenPair.refreshToken()).isEqualTo(NEW_REFRESH_RAW);

            ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
            verify(refreshTokenPort).save(captor.capture());
            RefreshToken refreshToken = captor.getValue();
            assertThat(refreshToken.getTokenHash()).isEqualTo(NEW_HASH);
            assertThat(refreshToken.getUserId()).isEqualTo(USER_ID);

            verify(refreshTokenPort, never()).rotate(any(), any(), any());
            verify(blacklistedRefreshTokenPort, never()).revoke(any());
        }

        @Test
        @DisplayName("로그인했는데 기존 리프레쉬 존재 -> 재발급 실행")
        void 로그인시_토큰_재발급() {

            // given
            RefreshToken refreshToken = mock(RefreshToken.class);
            when(refreshToken.getTokenHash()).thenReturn(OLD_HASH);

            when(refreshTokenPort.findByUserId(USER_ID)).thenReturn(Optional.of(refreshToken));
            when(refreshTokenPort.rotate(OLD_HASH, NEW_HASH, USER_ID)).thenReturn(1);

            when(jwtPort.createAccessToken(USER_ID)).thenReturn(ACC_NEW_RAW);

            // when
            TokenPair tokenPair = authFacade.issueTokens(USER_ID);

            // then
            assertThat(tokenPair.accessToken()).isEqualTo(ACC_NEW_RAW);
            assertThat(tokenPair.refreshToken()).isEqualTo(NEW_REFRESH_RAW);

            verify(refreshTokenPort).rotate(OLD_HASH, NEW_HASH, USER_ID);
            ArgumentCaptor<BlacklistedRefreshToken> captor = ArgumentCaptor.forClass(BlacklistedRefreshToken.class);
            verify(blacklistedRefreshTokenPort).revoke(captor.capture());
            BlacklistedRefreshToken blacklistedRefreshToken = captor.getValue();
            assertThat(blacklistedRefreshToken.getTokenHash()).isEqualTo(OLD_HASH);
            assertThat(blacklistedRefreshToken.getUserId()).isEqualTo(USER_ID);

            verify(refreshTokenPort, never()).save(any());
        }

        @Test
        @DisplayName("로그인 했는데 기존 토큰 존재 -> 재발급 도중 토큰 회전 실패")
        void 토큰_회전_실패() {

            // given
            RefreshToken existing = mock(RefreshToken.class);
            when(existing.getTokenHash()).thenReturn(OLD_HASH);

            when(refreshTokenPort.findByUserId(USER_ID)).thenReturn(Optional.of(existing));
            when(refreshTokenPort.rotate(OLD_HASH, NEW_HASH, USER_ID)).thenReturn(0);

            // when & then
            assertThatThrownBy(() -> authFacade.issueTokens(USER_ID)).isInstanceOf(RefreshTokenInvalidException.class);
        }
    }

    @Nested
    @DisplayName("reissueTokens")
    class ReissueTokens {

        @BeforeEach
        void commonReissueToken() {
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);
        }


        @Test
        @DisplayName("정상 발급: 블랙리스트 X & 현재 DB 소유 토큰 -> roate = 1")
        void 정상_재발급() {

            // given
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);
            when(blacklistedRefreshTokenPort.isRevoked(OLD_HASH)).thenReturn(Boolean.FALSE);
            when(refreshTokenPort.existsByTokenHashAndUserId(OLD_HASH, USER_ID)).thenReturn(Boolean.TRUE);
            when(jwtPort.createRefreshToken(USER_ID)).thenReturn(NEW_REFRESH_RAW);
            when(tokenHasherPort.hash(NEW_REFRESH_RAW)).thenReturn(NEW_HASH);
            when(refreshTokenPort.rotate(OLD_HASH, NEW_HASH, USER_ID)).thenReturn(1);
            when(jwtPort.createAccessToken(USER_ID)).thenReturn(ACC_NEW_RAW);

            // when
            TokenPair tokenPair = authFacade.reissueTokens(OLD_REFRESH_RAW, USER_ID);

            // then
            assertThat(tokenPair.accessToken()).isEqualTo(ACC_NEW_RAW);
            assertThat(tokenPair.refreshToken()).isEqualTo(NEW_REFRESH_RAW);

            verify(refreshTokenPort).rotate(OLD_HASH, NEW_HASH, USER_ID);
            ArgumentCaptor<BlacklistedRefreshToken> captor = ArgumentCaptor.forClass(BlacklistedRefreshToken.class);
            verify(blacklistedRefreshTokenPort).revoke(captor.capture());
            BlacklistedRefreshToken blacklistedRefreshToken = captor.getValue();
            assertThat(blacklistedRefreshToken.getTokenHash()).isEqualTo(OLD_HASH);
            assertThat(blacklistedRefreshToken.getUserId()).isEqualTo(USER_ID);
        }

        @Test
        @DisplayName("블랙리스트 X & 현재 DB 소유 토큰 O -> roate = 0 토큰 회전 실패")
        void 토큰_회전_실패() {

            // given
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);
            when(blacklistedRefreshTokenPort.isRevoked(OLD_HASH)).thenReturn(Boolean.FALSE);
            when(refreshTokenPort.existsByTokenHashAndUserId(OLD_HASH, USER_ID)).thenReturn(Boolean.TRUE);
            when(jwtPort.createRefreshToken(USER_ID)).thenReturn(NEW_REFRESH_RAW);
            when(tokenHasherPort.hash(NEW_REFRESH_RAW)).thenReturn(NEW_HASH);
            when(refreshTokenPort.rotate(OLD_HASH, NEW_HASH, USER_ID)).thenReturn(0);

            // when & then
            assertThatThrownBy(() -> authFacade.reissueTokens(OLD_REFRESH_RAW, USER_ID)).isInstanceOf(RefreshTokenInvalidException.class);
        }

        @Test
        @DisplayName("블랙리스트 O -> 예외 발생")
        void 블랙리스트토큰_재사용() {

            // given
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);
            when(blacklistedRefreshTokenPort.isRevoked(OLD_HASH)).thenReturn(Boolean.TRUE);

            // when & then
            assertThatThrownBy(() -> authFacade.reissueTokens(OLD_REFRESH_RAW, USER_ID))
                    .isInstanceOf(RefreshTokenInvalidException.class);

            verify(refreshTokenPort).findAllByUserId(USER_ID);
            verify(refreshTokenPort).killAllSessions(anyList(), any());
        }

        @Test
        @DisplayName("블랙리스트 X, 현재 DB 미소유 토큰 -> 예외 발생")
        void DB_미소유_토큰() {

            // given
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);
            when(blacklistedRefreshTokenPort.isRevoked(OLD_HASH)).thenReturn(Boolean.FALSE);
            when(refreshTokenPort.existsByTokenHashAndUserId(OLD_HASH, USER_ID)).thenReturn(Boolean.FALSE);

            // when & then
            assertThatThrownBy(() -> authFacade.reissueTokens(OLD_REFRESH_RAW, USER_ID))
                    .isInstanceOf(RefreshTokenInvalidException.class);

            verify(refreshTokenPort).findAllByUserId(USER_ID);
            verify(refreshTokenPort).killAllSessions(anyList(), any());

        }
    }

    @Nested
    @DisplayName("logout")
    class Logout {

        @Test
        @DisplayName("정상 로그아웃, DB 소유 토큰")
        void 정상_로그아웃_DB_소유() {

            // given
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);

            RefreshToken refreshToken = mock(RefreshToken.class);
            when(refreshToken.getUserId()).thenReturn(USER_ID);
            when(refreshTokenPort.findByTokenHash(OLD_HASH)).thenReturn(Optional.of(refreshToken));

            // when
            authFacade.logout(OLD_REFRESH_RAW);

            // then
            verify(blacklistedRefreshTokenPort).insertIfNotExists(OLD_HASH, USER_ID);
            verify(refreshTokenPort).deleteByTokenHashAndUserId(OLD_HASH, USER_ID);
        }

        @Test
        @DisplayName("정상 로그아웃, DB 미소유 토큰")
        void 정상_로그아웃_DB_미소유() {

            // given
            when(tokenHasherPort.hash(OLD_REFRESH_RAW)).thenReturn(OLD_HASH);
            when(refreshTokenPort.findByTokenHash(OLD_HASH)).thenReturn(Optional.empty());

            // when
            authFacade.logout(OLD_REFRESH_RAW);

            // then
            verify(blacklistedRefreshTokenPort).insertIfNotExists(OLD_HASH, null);
            verify(refreshTokenPort, never()).deleteByTokenHashAndUserId(any(), any());
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "\t", "\n"})
        @DisplayName("null/blank 입력이면 바로 반환")
        void logout_blank(String refreshToken) {

            // given & when
            authFacade.logout(null);
            authFacade.logout("  ");

            // then
            verifyNoInteractions(tokenHasherPort, refreshTokenPort, blacklistedRefreshTokenPort, jwtPort);
        }

    }

    @Nested
    @DisplayName("killAllSessions")
    class killAllSessions{
        @Test
        @DisplayName("DB에 사용자 토큰 2개 조회 후 만료 처리")
        void 사용자의_모든_토큰_만료() {

            // given
            byte[] ANOTHER_HASH = "another-hash".getBytes();
            RefreshToken rt1 = mock(RefreshToken.class);
            RefreshToken rt2 = mock(RefreshToken.class);

            when(rt1.getTokenHash()).thenReturn(OLD_HASH);
            when(rt1.getUserId()).thenReturn(USER_ID);

            when(rt2.getTokenHash()).thenReturn(ANOTHER_HASH);
            when(rt2.getUserId()).thenReturn(USER_ID);

            when(refreshTokenPort.findAllByUserId(USER_ID)).thenReturn(List.of(rt1, rt2));

            // when
            authFacade.killAllSessions(USER_ID);

            // then
            verify(refreshTokenPort).findAllByUserId(USER_ID);

            ArgumentCaptor<List<BlacklistedRefreshToken>> captor =
                    ArgumentCaptor.forClass(List.class);
            verify(refreshTokenPort).killAllSessions(captor.capture(), eq(USER_ID));
            List<BlacklistedRefreshToken> blackList = captor.getValue();

            assertThat(blackList).hasSize(2);
            assertThat(blackList).extracting(BlacklistedRefreshToken::getTokenHash)
                    .containsExactlyInAnyOrder(OLD_HASH, ANOTHER_HASH);
            assertThat(blackList).extracting(BlacklistedRefreshToken::getUserId)
                    .containsOnly(USER_ID);
        }

        @Test
        @DisplayName("DB에 사용자 토큰 0개가 조회되도 만료 처리")
        void killAllSessions_empty_tokens() {
            // given
            when(refreshTokenPort.findAllByUserId(USER_ID))
                    .thenReturn(List.of());

            ArgumentCaptor<List<BlacklistedRefreshToken>> captor =
                    ArgumentCaptor.forClass(List.class);

            // when
            authFacade.killAllSessions(USER_ID);

            // then
            verify(refreshTokenPort).findAllByUserId(USER_ID);
            verify(refreshTokenPort).killAllSessions(captor.capture(), eq(USER_ID));

            List<BlacklistedRefreshToken> passed = captor.getValue();
            assertThat(passed).isEmpty();
        }


    }
}