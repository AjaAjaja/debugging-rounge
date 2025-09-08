package ajaajaja.debugging_rounge.feature.auth.api;

import ajaajaja.debugging_rounge.feature.auth.application.dto.AccessTokenResponse;
import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.LogoutUseCase;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.ReissueTokensUseCase;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final ReissueTokensUseCase reissueTokensUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshCookieFactory refreshCookieFactory;

    @PostMapping("/refresh")
    public AccessTokenResponse reissue(@AuthenticationPrincipal Jwt jwt, HttpServletResponse response) {
        Long userId = Long.valueOf(jwt.getSubject());
        String refreshToken = jwt.getTokenValue();

        TokenPair tokenPair = reissueTokensUseCase.reissueTokens(refreshToken, userId);
        AccessTokenResponse tokenResponse = AccessTokenResponse.of(tokenPair.accessToken());

        if (tokenPair.refreshToken() != null) {
            ResponseCookie responseCookie = refreshCookieFactory.createRefreshCookie(tokenPair.refreshToken());
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.setContentType("application/json; charset=UTF-8");
        }

        return tokenResponse;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt, HttpServletResponse response) {
        logoutUseCase.logout(jwt.getTokenValue());
        ResponseCookie expireRefreshCookie = refreshCookieFactory.expireRefreshCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, expireRefreshCookie.toString());

        return ResponseEntity.noContent().build();
    }
}

