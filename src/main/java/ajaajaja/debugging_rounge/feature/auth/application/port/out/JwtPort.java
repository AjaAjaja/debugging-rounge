package ajaajaja.debugging_rounge.feature.auth.application.port.out;

public interface JwtPort {
    String createAccessToken(Long userId);

    String createRefreshToken(Long userId);
}
