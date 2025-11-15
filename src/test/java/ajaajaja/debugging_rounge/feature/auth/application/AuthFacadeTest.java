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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private final String ACC_RAW       = "ACC.543";
    private final String ACC_NEW_RAW   = "ACC.NEW.543";

    private final byte[] OLD_HASH = "old-hash".getBytes();
    private final byte[] NEW_HASH = "new-hash".getBytes();
    private final byte[] ANY_HASH = "any-hash".getBytes();

    @Nested
    @DisplayName("issueTokens")
    class IssueTokens {

        @BeforeEach
        void common() {
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
            RefreshToken existing = mock(RefreshToken.class);
            when(existing.getTokenHash()).thenReturn(OLD_HASH);

            when(refreshTokenPort.findByUserId(USER_ID)).thenReturn(Optional.of(existing));
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


}