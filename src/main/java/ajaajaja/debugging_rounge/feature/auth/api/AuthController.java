package ajaajaja.debugging_rounge.feature.auth.api;

import ajaajaja.debugging_rounge.feature.auth.api.dto.AccessTokenResponse;
import ajaajaja.debugging_rounge.feature.auth.api.dto.TokenDto;
import ajaajaja.debugging_rounge.feature.auth.application.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    @PostMapping("/refresh")
    public AccessTokenResponse reIssue(@AuthenticationPrincipal Jwt jwt,
                                       HttpServletResponse response) {

        Long userId = Long.valueOf(jwt.getSubject());
        String refreshToken = jwt.getTokenValue();

        TokenDto tokenDto = tokenService.reissueTokens(userId, refreshToken);
        AccessTokenResponse tokenResponse = AccessTokenResponse.of(tokenDto.getAccessToken());

        if (tokenDto.getRefreshToken() != null) {
            ResponseCookie responseCookie = tokenService.createRefreshCookie(tokenDto.getRefreshToken());
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.setContentType("application/json; charset=UTF-8");
        }

        return tokenResponse;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name="#{@jwtProperties.cookie.name}", required=false) String refreshToken,
            HttpServletResponse response) {
        tokenService.logout(refreshToken);
        ResponseCookie expireRefreshCookie = tokenService.expireRefreshCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, expireRefreshCookie.toString());

        return ResponseEntity.noContent().build();
    }
}

