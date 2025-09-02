package ajaajaja.debugging_rounge.feature.user.application.port.in;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;

public interface FindOrRegisterUserUseCase {
    User findOrRegister(String email, SocialType socialType);
}
