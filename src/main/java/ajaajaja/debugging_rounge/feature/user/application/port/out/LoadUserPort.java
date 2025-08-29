package ajaajaja.debugging_rounge.feature.user.application.port.out;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;

import java.util.Optional;

public interface LoadUserPort {
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);

}
