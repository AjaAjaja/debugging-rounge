package ajaajaja.debugging_rounge.common.config.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class TokenHasher {

    private final byte[] pepper;

    public TokenHasher(@Value("${jwt.token.pepper}") String pepper) {
        this.pepper = Base64.getDecoder().decode(pepper);
    }

    public byte[] hmacSha256(String token) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(pepper, "HmacSHA256"));
            return mac.doFinal(token.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hash token", e);
        }
    }
}
