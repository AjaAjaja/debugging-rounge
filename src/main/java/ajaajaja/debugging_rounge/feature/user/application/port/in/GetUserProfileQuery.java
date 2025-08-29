package ajaajaja.debugging_rounge.feature.user.application.port.in;

import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;

public interface GetUserProfileQuery {
    UserProfileDto getUserProfile(Long userId);
}
