package ajaajaja.debuging_rounge.global.auth.oauth;

import ajaajaja.debuging_rounge.global.auth.SocialType;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo create(SocialType socialType, Map<String, Object> attributes) {
        return switch (socialType) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case KAKAO -> new KakaoOAuth2UserInfo(attributes);
            case NAVER -> new NaverOAuth2UserInfo(attributes);
        };
    }
}
