package ajaajaja.debugging_rounge.feature.auth.api.application;

import ajaajaja.debugging_rounge.feature.user.domain.User;
import ajaajaja.debugging_rounge.feature.user.application.UserService;
import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.CustomOAuth2User;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.OAuth2UserInfo;
import ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth.OAuth2UserInfoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService = new DefaultOAuth2UserService();
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = SocialType.from(registrationId);

        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.create(socialType, oAuth2User.getAttributes());

        User user = userService.findOrRegister(oAuth2UserInfo.getEmail(), socialType);

        return new CustomOAuth2User(user, oAuth2User.getAttributes());
    }
}
