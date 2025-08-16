package ajaajaja.debugging_rounge.feature.auth.infrastructure.security;

import ajaajaja.debugging_rounge.feature.auth.api.exception.RefreshTokenNotFoundException;
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
        if ("/auth/refresh".equals(request.getRequestURI())) {
            return Optional.ofNullable(WebUtils.getCookie(request, "refreshToken"))
                    .map(Cookie::getValue)
                    .orElseThrow(RefreshTokenNotFoundException::new);
        }
        return defaultResolver.resolve(request);
    }
}
