package ajaajaja.debuging_rounge.global.auth.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = Long.valueOf(oAuth2User.getName());

        TokenDto tokenDto = tokenService.issueTokens(userId);

        ResponseCookie cookie = tokenService.createRefreshCookie(tokenDto.getRefreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(AccessTokenResponse.of(tokenDto.getAccessToken())));
    }

}
