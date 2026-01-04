package ajaajaja.debugging_rounge.feature.auth.infrastructure.oauth;

import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class CustomOAuth2User implements OAuth2User, OidcUser {

    private final User user;
    private final Map<String, Object> attributes;
    private final OAuth2User delegate;
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getName() {
        return String.valueOf(user.getId());
    }

    // OidcUser 메서드들
    @Override
    public Map<String, Object> getClaims() {
        return delegate instanceof OidcUser ? ((OidcUser) delegate).getClaims() : attributes;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate instanceof OidcUser ? ((OidcUser) delegate).getUserInfo() : null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate instanceof OidcUser ? ((OidcUser) delegate).getIdToken() : null;
    }
}
