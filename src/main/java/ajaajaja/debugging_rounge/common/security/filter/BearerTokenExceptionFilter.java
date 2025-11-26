package ajaajaja.debugging_rounge.common.security.filter;

import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class BearerTokenExceptionFilter extends OncePerRequestFilter {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (CustomAuthenticationException e) {
            authenticationEntryPoint.commence(request, response, e);
        }
    }
}

