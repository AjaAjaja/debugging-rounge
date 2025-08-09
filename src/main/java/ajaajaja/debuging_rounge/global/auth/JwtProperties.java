package ajaajaja.debuging_rounge.global.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Getter @Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private Token token;
    private Cookie cookie;

    @Getter @Setter
    public static class Token {
        private Duration accessExpiration;
        private Duration refreshExpiration;
        private Duration renewThreshold;
    }

    @Getter @Setter
    public static class Cookie{
        private String name;
        private String path;
        private Duration maxAge;
        private String sameSite;
        private boolean secure;
        private boolean httpOnly;
    }
}
