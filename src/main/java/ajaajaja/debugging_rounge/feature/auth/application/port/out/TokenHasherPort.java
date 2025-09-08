package ajaajaja.debugging_rounge.feature.auth.application.port.out;

public interface TokenHasherPort {
    byte[] hash(String rawToken);
}
