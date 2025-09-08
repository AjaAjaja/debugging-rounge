package ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth;

import ajaajaja.debugging_rounge.feature.auth.api.RefreshCookieFactory;
import ajaajaja.debugging_rounge.feature.auth.application.dto.AccessTokenResponse;
import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.IssueTokensUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final IssueTokensUseCase issueTokensUseCase;
    private final RefreshCookieFactory refreshCookieFactory;
    private final ObjectMapper objectMapper;

    @Value("${frontend.base-url}")
    private String frontendBase;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = Long.valueOf(oAuth2User.getName());

        TokenPair tokenPair = issueTokensUseCase.issueTokens(userId);

        ResponseCookie cookie = refreshCookieFactory.createRefreshCookie(tokenPair.refreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter()
                .write(objectMapper.writeValueAsString(AccessTokenResponse.of(tokenPair.accessToken())));
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", frontendBase + "/oauth2/redirect");
    }

}
