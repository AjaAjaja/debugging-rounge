package ajaajaja.debuging_rounge.global.auth.controller;

import ajaajaja.debuging_rounge.global.auth.oauth.AccessTokenResponse;
import ajaajaja.debuging_rounge.global.auth.oauth.TokenDto;
import ajaajaja.debuging_rounge.global.auth.oauth.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final TokenService tokenService;

    @GetMapping("/refresh")
    public AccessTokenResponse reIssue(@AuthenticationPrincipal Jwt jwt,
                                       HttpServletResponse response) {

        Long userId = Long.valueOf(jwt.getSubject());
        String refreshToken = jwt.getTokenValue();

        TokenDto tokenDto = tokenService.reIssueTokens(userId, refreshToken);
        AccessTokenResponse tokenResponse = AccessTokenResponse.of(tokenDto.getAccessToken());

        if (tokenDto.getRefreshToken() != null) {
            ResponseCookie responseCookie = tokenService.createRefreshCookie(tokenDto.getRefreshToken());
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.setContentType("application/json; charset=UTF-8");
        }

        return tokenResponse;
    }
}

