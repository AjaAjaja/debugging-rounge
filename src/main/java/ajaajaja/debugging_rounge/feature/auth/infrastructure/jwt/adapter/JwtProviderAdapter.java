package ajaajaja.debugging_rounge.feature.auth.infrastructure.jwt.adapter;

import ajaajaja.debugging_rounge.common.jwt.JwtProvider;
import ajaajaja.debugging_rounge.common.jwt.TokenType;
import ajaajaja.debugging_rounge.feature.auth.application.port.out.JwtPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtProviderAdapter implements JwtPort {

    private final JwtProvider jwtProvider;

    @Override
    public String createAccessToken(Long userId) {
        return jwtProvider.createToken(userId.toString(), TokenType.ACCESS);
    }

    @Override
    public String createRefreshToken(Long userId) {
        return jwtProvider.createToken(userId.toString(), TokenType.REFRESH);
    }

}
