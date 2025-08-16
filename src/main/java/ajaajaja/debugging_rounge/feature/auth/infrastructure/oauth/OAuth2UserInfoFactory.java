package ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.GoogleOAuth2UserInfo;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.KakaoOAuth2UserInfo;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.NaverOAuth2UserInfo;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.OAuth2UserInfo;

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
