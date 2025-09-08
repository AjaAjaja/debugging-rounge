package ajaajaja.debugging_rounge.feature.auth.application.dto;

public record AccessTokenResponse(String accessToken) {

    public static AccessTokenResponse of(String accessToken) {
        return new AccessTokenResponse(accessToken);
    }
}
