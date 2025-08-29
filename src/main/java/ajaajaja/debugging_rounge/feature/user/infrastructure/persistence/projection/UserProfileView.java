package ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.projection;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;

public interface UserProfileView {
    Long getId();
    String getEmail();
    SocialType getSocialType();
}
