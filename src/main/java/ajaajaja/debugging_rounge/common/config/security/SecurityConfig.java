package ajaajaja.debugging_rounge.common.config.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            BearerTokenResolver bearerTokenResolver,
            @Qualifier("accessAuthenticationManager") AuthenticationManager accessManager,
            @Qualifier("refreshAuthenticationManager") AuthenticationManager refreshManager,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler)
            throws Exception {

        http.csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth.
                        requestMatchers("/oauth2/**","/auth/logout").permitAll()
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
        return "/auth/refresh".equals(request.getRequestURI())
                ? refreshManager
                : accessManager;
    }

}
