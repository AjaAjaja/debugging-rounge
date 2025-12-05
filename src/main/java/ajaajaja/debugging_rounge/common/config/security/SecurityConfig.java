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

    /**
     * REFRESH 토큰이 필요한 경로 목록
     * 새로운 경로가 추가되면 이 Set에 추가하면 됨
     */
    public static final Set<String> REFRESH_TOKEN_REQUIRED_PATHS = Set.of(
            "/auth/refresh",
            "/auth/logout"
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
                        .requestMatchers(HttpMethod.GET, "/oauth2/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/questions", "/questions/**").permitAll()
                        .anyRequest().authenticated())

                .oauth2Login(login -> login.
                        userInfoEndpoint(userInfo -> userInfo.
                                userService(customOAuth2UserService)).
                        successHandler(authenticationSuccessHandler))

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
