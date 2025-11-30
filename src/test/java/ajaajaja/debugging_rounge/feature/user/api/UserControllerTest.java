package ajaajaja.debugging_rounge.feature.user.api;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.api.mapper.UserMapperImpl;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.application.port.in.GetUserProfileQuery;
import ajaajaja.debugging_rounge.feature.user.domain.exception.UserNotFoundException;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTest;
import ajaajaja.debugging_rounge.support.WebMvcSecurityTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcSecurityTest(UserController.class)
@Import(UserMapperImpl.class)
class UserControllerTest extends WebMvcSecurityTestSupport {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    GetUserProfileQuery getUserProfileQuery;

    @Nested
    @DisplayName("GET /users/me - 현재 사용자 프로필 조회")
    class GetCurrentUserProfile {

        @Test
        @DisplayName("유효한 JWT로 요청하면 200과 사용자 프로필을 반환한다")
        void 유효한JWT_프로필조회_200() throws Exception {
            // given
            Long userId = 1L;
            String email = "test@example.com";
            UserProfileDto profileDto = new UserProfileDto(userId, email, SocialType.GOOGLE);

            given(getUserProfileQuery.getUserProfile(userId)).willReturn(profileDto);

            // when & then
            mockMvc.perform(get("/users/me")
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.userId").value(userId))
                    .andExpect(jsonPath("$.email").value(email));
        }

        @Test
        @DisplayName("인증 없이 요청하면 401 + AUTHENTICATION_FAILED를 반환한다")
        void 인증없이_요청_401() throws Exception {
            // when & then
            mockMvc.perform(get("/users/me"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }

        @Test
        @DisplayName("유효하지 않은 JWT로 요청하면 401을 반환한다")
        void 유효하지않은JWT_401() throws Exception {
            // when & then
            mockMvc.perform(get("/users/me")
                            .header("Authorization", "Bearer invalid.jwt.token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(content().contentType("application/json;charset=UTF-8"))
                    .andExpect(jsonPath("$.code").value("AUTHENTICATION_FAILED"));
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID면 404를 반환한다")
        void 사용자없음_404() throws Exception {
            // given
            Long userId = 999L;
            given(getUserProfileQuery.getUserProfile(userId))
                    .willThrow(new UserNotFoundException());

            // when & then
            mockMvc.perform(get("/users/me")
                            .with(jwt().jwt(jwt -> jwt.subject(userId.toString()))))
                    .andExpect(status().isNotFound());
        }
    }
}

