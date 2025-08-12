package ajaajaja.debugging_rounge.common.config.jwt;

import ajaajaja.debugging_rounge.feature.auth.api.exception.JwtCreationException;
import ajaajaja.debugging_rounge.feature.auth.api.exception.JwtParsingException;
import ajaajaja.debugging_rounge.feature.auth.api.exception.JwtValidationException;
import ajaajaja.debugging_rounge.feature.auth.domain.TokenType;
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
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            MACVerifier verifier = new MACVerifier(hmacKey);
            if (!signedJWT.verify(verifier)) {
                throw new JwtValidationException();
            }

            return signedJWT.getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            throw new JwtParsingException();
        } catch (JOSEException e) {
            throw new JwtValidationException();
        }

    }

}
