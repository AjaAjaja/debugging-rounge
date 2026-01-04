package ajaajaja.debugging_rounge.common.config.security;

import ajaajaja.debugging_rounge.common.security.filter.BearerTokenExceptionFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final Set<String> REFRESH_TOKEN_REQUIRED_PATHS = Set.of(
            "/api/auth/refresh",
            "/api/auth/logout",
            "/auth/refresh",  // 테스트 환경용 (context-path 없음)
            "/auth/logout"    // 테스트 환경용 (context-path 없음)
    );

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            BearerTokenResolver bearerTokenResolver,
            @Qualifier("accessAuthenticationManager") AuthenticationManager accessManager,
            @Qualifier("refreshAuthenticationManager") AuthenticationManager refreshManager,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler,
            BearerTokenExceptionFilter bearerTokenExceptionFilter)
            throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // OAuth2 로그인 관련 경로 허용
                        .requestMatchers("oauth2/**", "login/oauth2/**").permitAll()
                        // 공개 API
                        .requestMatchers(HttpMethod.GET, "/questions", "/questions/**").permitAll()
                        // Swagger UI 및 API 문서 경로 허용
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(login -> login
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                                .oidcUserService(req -> (org.springframework.security.oauth2.core.oidc.user.OidcUser) customOAuth2UserService.loadUser(req)))
                        .successHandler(authenticationSuccessHandler))

                .oauth2ResourceServer(rs -> rs
                        .bearerTokenResolver(bearerTokenResolver)
                        .authenticationManagerResolver(req ->
                                resolveAuthManager(req, accessManager, refreshManager))
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .addFilterBefore(bearerTokenExceptionFilter, BearerTokenAuthenticationFilter.class)

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    private AuthenticationManager resolveAuthManager(
            HttpServletRequest request,
            AuthenticationManager accessManager,
            AuthenticationManager refreshManager
    ) {
        String requestURI = request.getRequestURI();
        // REFRESH 토큰이 필요한 경로인지 확인
        return REFRESH_TOKEN_REQUIRED_PATHS.contains(requestURI)
                ? refreshManager
                : accessManager;
    }

}
