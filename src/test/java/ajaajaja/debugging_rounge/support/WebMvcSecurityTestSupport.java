package ajaajaja.debugging_rounge.support;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

public abstract class WebMvcSecurityTestSupport {

    // SecurityConfig 의존성 - 모든 WebMvcTest에서 필요
    @MockitoBean
    protected OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    @MockitoBean
    protected AuthenticationSuccessHandler authenticationSuccessHandler;

    @MockitoBean(name = "accessAuthenticationManager")
    protected AuthenticationManager accessAuthenticationManager;

    @MockitoBean(name = "refreshAuthenticationManager")
    protected AuthenticationManager refreshAuthenticationManager;
}

