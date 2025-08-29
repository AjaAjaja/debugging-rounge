package ajaajaja.debugging_rounge.feature.user.application.port.out;

import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;

import java.util.Optional;

public interface LoadUserProfileViewPort {
    Optional<UserProfileDto> findUserProfileViewById(Long id);
}
