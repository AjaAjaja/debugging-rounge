package ajaajaja.debugging_rounge.feature.auth.infrastructure.jwt.adapter;

import ajaajaja.debugging_rounge.common.jwt.TokenHasher;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.TokenHasherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenHasherAdapter implements TokenHasherPort {

    private final TokenHasher tokenHasher;

    @Override
    public byte[] hash(String rawToken) {
        return tokenHasher.hmacSha256(rawToken);
    }
}
