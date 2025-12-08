package ajaajaja.debugging_rounge.common.security.token;

import ajaajaja.debugging_rounge.common.config.security.SecurityConfig;
import ajaajaja.debugging_rounge.common.jwt.exception.RefreshTokenNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.util.Optional;

@Component
public class CustomBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        // 쿠키에서 토큰을 읽어야 하는 경로인지 확인
        if (SecurityConfig.REFRESH_TOKEN_REQUIRED_PATHS.contains(requestURI)) {
            return Optional.ofNullable(WebUtils.getCookie(request, "refreshToken"))
                    .map(Cookie::getValue)
                    .orElseThrow(RefreshTokenNotFoundException::new);
        }
        // 기본 동작: Authorization 헤더에서 토큰 읽기
        return defaultResolver.resolve(request);
    }
}
