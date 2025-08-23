package ajaajaja.debugging_rounge.feature.user.api;

import ajaajaja.debugging_rounge.common.security.annotation.CurrentUserId;
import ajaajaja.debugging_rounge.feature.user.application.UserService;
import ajaajaja.debugging_rounge.feature.user.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(@CurrentUserId Long userId) {
        UserProfileDto userProfileDto = userService.getUserProfile(userId);

        return ResponseEntity.ok(userProfileDto);
    }

}
