package ajaajaja.debugging_rounge.feature.user.api;

import ajaajaja.debugging_rounge.common.security.annotation.LoginUserId;
import ajaajaja.debugging_rounge.feature.user.api.dto.UserProfileResponse;
import ajaajaja.debugging_rounge.feature.user.api.mapper.UserMapper;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.application.port.in.GetUserProfileQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final GetUserProfileQuery getUserProfileQuery;
    private final UserMapper userMapper;

    @Operation(summary = "현재 사용자 프로필 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(
            @Parameter(hidden = true) @LoginUserId Long userId) {
        UserProfileDto userProfile = getUserProfileQuery.getUserProfile(userId);
        return ResponseEntity.ok(userMapper.toResponse(userProfile));
    }

}
