package ajaajaja.debugging_rounge.common.config.jwt;

import ajaajaja.debugging_rounge.feature.auth.domain.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    private String secretKey;

    @Bean("accessAuthenticationManager")
    @Primary
    public AuthenticationManager accessAuthenticationManager() {
        NimbusJwtDecoder decoder = buildDecoder();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                new JwtClaimValidator<String>("type", t -> TokenType.ACCESS.name().equals(t))
        ));
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
        return new ProviderManager(provider);
    }

    @Bean("refreshAuthenticationManager")
    public AuthenticationManager refreshAuthenticationManager() {
        NimbusJwtDecoder decoder = buildDecoder();
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                JwtValidators.createDefault(),
                new JwtClaimValidator<String>("type", t -> TokenType.REFRESH.name().equals(t))
        ));
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(decoder);
        return new ProviderManager(provider);
    }

    private NimbusJwtDecoder buildDecoder() {
        byte[] secretKeyByte = Base64.getDecoder().decode(secretKey);
        SecretKey key = new SecretKeySpec(secretKeyByte, "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(key).build();
    }
}
