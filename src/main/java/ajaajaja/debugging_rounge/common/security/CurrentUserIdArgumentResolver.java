package ajaajaja.debugging_rounge.common.security;

import ajaajaja.debugging_rounge.common.exception.auth.AuthenticationPrincipalInvalidException;
import ajaajaja.debugging_rounge.common.exception.auth.AuthenticationRequiredException;
import ajaajaja.debugging_rounge.common.exception.auth.UserIdentifierInvalidException;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        CurrentUserId ann = parameter.getParameterAnnotation(CurrentUserId.class);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!ann.required()) {
            if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
                return null;
            }
            return extractUserIdOrNull(auth);
        }

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new AuthenticationRequiredException();
        }

        Long userId = extractUserIdOrThrow(auth);
        return userId;
    }

    private Long extractUserIdOrThrow(Authentication auth) {
        if (!(auth.getPrincipal() instanceof Jwt jwt)) {
                throw new AuthenticationPrincipalInvalidException();
        }
        String sub = jwt.getClaimAsString("sub");
        try {
            return Long.valueOf(sub);
        } catch (Exception e) {
            throw new UserIdentifierInvalidException();
        }
    }

    private Long extractUserIdOrNull(Authentication auth) {
        try {
            return extractUserIdOrThrow(auth);
        } catch (InsufficientAuthenticationException ex) {
            return null;
        }
    }
}
