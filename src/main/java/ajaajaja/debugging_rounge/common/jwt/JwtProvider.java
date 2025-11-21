package ajaajaja.debugging_rounge.common.jwt;

import ajaajaja.debugging_rounge.common.jwt.exception.JwtCreationException;
import ajaajaja.debugging_rounge.common.jwt.exception.JwtParsingException;
import ajaajaja.debugging_rounge.common.jwt.exception.JwtValidationException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final byte[] hmacKey;

    public JwtProvider(
            JwtProperties jwtProperties,
            @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}") String secretKey) {
        this.jwtProperties = jwtProperties;
        hmacKey = Base64.getDecoder().decode(secretKey);
    }

    public String createToken(String subject, TokenType tokenType) {

        try {
            Instant nowInstant = Instant.now();
            Duration expiresIn = tokenType == TokenType.ACCESS
                    ? jwtProperties.getToken().getAccessExpiration()
                    : jwtProperties.getToken().getRefreshExpiration();
            Instant expiryInstant = nowInstant.plus(expiresIn);
            Date issueTime  = Date.from(nowInstant);
            Date expirationTime = Date.from(expiryInstant);

            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(issueTime)
                    .expirationTime(expirationTime)
                    .claim("type", String.valueOf(tokenType))
                    .jwtID(UUID.randomUUID().toString())
                    .build();

            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claims);
            signedJWT.sign(new MACSigner(hmacKey));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new JwtCreationException();
        }
    }

    public Date extractExpiration(String token) {
        JWTClaimsSet claimsSet = parseClaims(token);
        validateExpiration(claimsSet);
        return claimsSet.getExpirationTime();
    }

    public String extractSubject(String token, TokenType expectedType) {
        JWTClaimsSet claims = parseClaims(token);
        validateType(claims, expectedType);
        validateExpiration(claims);
        return claims.getSubject();
    }

    private SignedJWT parseSignedJwt(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            MACVerifier verifier = new MACVerifier(hmacKey);
            if (!signedJWT.verify(verifier)) {
                throw new JwtValidationException();
            }

            return signedJWT;
        } catch (ParseException e) {
            throw new JwtParsingException();
        } catch (JOSEException e) {
            throw new JwtValidationException();
        }
    }

    private JWTClaimsSet parseClaims(String token) {
        SignedJWT signedJWT = parseSignedJwt(token);
        try {
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new JwtParsingException();
        }
    }
    private void validateType(JWTClaimsSet claims, TokenType expectedType) {
        Object typeClaim = claims.getClaim("type");
        if (typeClaim == null) {
            throw new JwtValidationException();
        }

        TokenType actualType;
        try {
            actualType = TokenType.valueOf(String.valueOf(typeClaim));
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException();
        }

        if (actualType != expectedType) {
            throw new JwtValidationException();
        }
    }

    private void validateExpiration(JWTClaimsSet claims) {
        Date expirationTime = claims.getExpirationTime();
        if (expirationTime == null || expirationTime.before(new Date())) {
            throw new JwtValidationException();
        }
    }

}
