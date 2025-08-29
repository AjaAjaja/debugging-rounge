package ajaajaja.debugging_rounge.feature.user.api;

import ajaajaja.debugging_rounge.common.security.annotation.CurrentUserId;
import ajaajaja.debugging_rounge.feature.user.api.dto.UserProfileResponse;
import ajaajaja.debugging_rounge.feature.user.api.mapper.UserProfileMapper;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.application.port.in.GetUserProfileQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final GetUserProfileQuery getUserProfileQuery;
    private final UserProfileMapper userProfileMapper;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(@CurrentUserId Long userId) {
        UserProfileDto userProfile = getUserProfileQuery.getUserProfile(userId);
        return ResponseEntity.ok(userProfileMapper.toResponse(userProfile));
    }

}
