package ajaajaja.debugging_rounge.feature.auth.api;

import ajaajaja.debugging_rounge.feature.auth.application.dto.AccessTokenResponse;
import ajaajaja.debugging_rounge.feature.auth.application.dto.TokenPair;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.LogoutUseCase;
import ajaajaja.debugging_rounge.feature.auth.application.port.in.ReissueTokensUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final ReissueTokensUseCase reissueTokensUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RefreshCookieFactory refreshCookieFactory;

    @Operation(
            summary = "토큰 재발급",
            description = "Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.\n\n" +
                    "**테스트 방법:**\n" +
                    "1. 구글 로그인을 통해 Refresh Token을 획득\n" +
                    "2. 브라우저 개발자 도구를 통해 refreshToken 쿠키 값을 확인(경로가 '/auth' 이어야지만 쿠키 보임)\n" +
                    "3. Swagger UI에서 상단 'Authorize' 버튼 클릭\n" +
                    "4. 'refresh-token-cookie'에 Refresh Token 값 입력\n" +
                    "5. 'Authorize' 클릭",
            security = @SecurityRequirement(name = "refresh-token-cookie")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패 또는 만료된 토큰", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/refresh")
    public AccessTokenResponse reissue(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(hidden = true) HttpServletResponse response) {
        Long userId = Long.valueOf(jwt.getSubject());
        String refreshToken = jwt.getTokenValue();

        TokenPair tokenPair = reissueTokensUseCase.reissueTokens(refreshToken, userId);
        AccessTokenResponse tokenResponse = AccessTokenResponse.of(tokenPair.accessToken());

        if (tokenPair.refreshToken() != null) {
            ResponseCookie responseCookie = refreshCookieFactory.createRefreshCookie(tokenPair.refreshToken());
            response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
            response.setContentType("application/json; charset=UTF-8");
        }

        return tokenResponse;
    }

    @Operation(
            summary = "로그아웃",
            description = "사용자를 로그아웃하고 Refresh Token을 무효화합니다.\n\n" +
                    "**테스트 방법:**\n" +
                    "1. 구글 로그인을 통해 Refresh Token을 획득\n" +
                    "2. 브라우저 개발자 도구를 통해 refreshToken 쿠키 값을 확인(경로가 '/auth' 이어야지만 쿠키 보임)\n" +
                    "3. Swagger UI에서 상단 'Authorize' 버튼 클릭\n" +
                    "4. 'refresh-token-cookie'에 Refresh Token 값 입력\n" +
                    "5. 'Authorize' 클릭",
            security = @SecurityRequirement(name = "refresh-token-cookie")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "로그아웃 성공", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt,
            @Parameter(hidden = true) HttpServletResponse response) {
        logoutUseCase.logout(jwt.getTokenValue());
        ResponseCookie expireRefreshCookie = refreshCookieFactory.expireRefreshCookie();

        response.addHeader(HttpHeaders.SET_COOKIE, expireRefreshCookie.toString());

        return ResponseEntity.noContent().build();
    }
}

