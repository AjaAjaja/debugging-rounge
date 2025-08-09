package ajaajaja.debuging_rounge.global.auth;

import ajaajaja.debuging_rounge.global.auth.exception.UnsupportedSocialTypeException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialType {
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private final String registrationId;

    public static SocialType from(String registrationId) {
        for (SocialType socialType : SocialType.values()) {
            if (socialType.getRegistrationId().equals(registrationId)) {
                return socialType;
            }
        }
        throw new UnsupportedSocialTypeException();
    }
}
