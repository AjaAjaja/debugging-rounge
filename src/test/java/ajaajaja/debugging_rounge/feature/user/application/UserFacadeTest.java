package ajaajaja.debugging_rounge.feature.user.application;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.application.port.out.LoadUserPort;
import ajaajaja.debugging_rounge.feature.user.application.port.out.LoadUserProfileViewPort;
import ajaajaja.debugging_rounge.feature.user.application.port.out.SaveUserPort;
import ajaajaja.debugging_rounge.feature.user.domain.exception.UserNotFoundException;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserFacadeTest {

    @Mock
    LoadUserPort loadUserPort;

    @Mock
    SaveUserPort saveUserPort;

    @Mock
    LoadUserProfileViewPort loadUserProfileViewPort;

    @InjectMocks
    UserFacade userFacade;

    @Nested
    @DisplayName("사용자 찾기 또는 등록 테스트")
    class FindOrRegisterTests {

        @Test
        @DisplayName("기존 사용자가 있으면 해당 사용자를 반환한다")
        void findOrRegister_기존사용자_조회() {
            // given
            String email = "test@example.com";
            SocialType socialType = SocialType.GOOGLE; // 어떤 소셜 타입도 상관없음.

            User existingUser = User.of(email, socialType);

            when(loadUserPort.findByEmailAndSocialType(email, socialType))
                    .thenReturn(Optional.of(existingUser));

            // when
            User result = userFacade.findOrRegister(email, socialType);

            // then
            assertThat(result).isEqualTo(existingUser);
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getSocialType()).isEqualTo(socialType);

            verify(loadUserPort).findByEmailAndSocialType(email, socialType);
            verify(saveUserPort, never()).save(any());
        }

        @Test
        @DisplayName("사용자가 없으면 새로운 사용자를 등록한다")
        void findOrRegister_신규사용자_등록() {
            // given
            String email = "newuser@example.com";
            SocialType socialType = SocialType.KAKAO;

            User newUser = User.of(email, socialType);

            when(loadUserPort.findByEmailAndSocialType(email, socialType))
                    .thenReturn(Optional.empty());
            when(saveUserPort.save(any(User.class))).thenReturn(newUser);

            // when
            User result = userFacade.findOrRegister(email, socialType);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getSocialType()).isEqualTo(socialType);

            verify(loadUserPort).findByEmailAndSocialType(email, socialType);

            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(saveUserPort).save(captor.capture());

            User capturedUser = captor.getValue();
            assertThat(capturedUser.getEmail()).isEqualTo(email);
            assertThat(capturedUser.getSocialType()).isEqualTo(socialType);
        }

        @Test
        @DisplayName("NAVER 소셜 타입으로 사용자를 등록한다")
        void findOrRegister_네이버_신규등록() {
            // given
            String email = "naver@example.com";
            SocialType socialType = SocialType.NAVER;

            User newUser = User.of(email, socialType);

            when(loadUserPort.findByEmailAndSocialType(email, socialType))
                    .thenReturn(Optional.empty());
            when(saveUserPort.save(any(User.class))).thenReturn(newUser);

            // when
            User result = userFacade.findOrRegister(email, socialType);

            // then
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getSocialType()).isEqualTo(SocialType.NAVER);

            verify(loadUserPort).findByEmailAndSocialType(email, socialType);
            verify(saveUserPort).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("사용자 프로필 조회 테스트")
    class GetUserProfileTests {

        @Test
        @DisplayName("사용자 ID로 프로필을 정상적으로 조회한다")
        void getUserProfile_성공() {
            // given
            Long userId = 1L;
            String email = "user@example.com";
            SocialType socialType = SocialType.GOOGLE;

            UserProfileDto profileDto = new UserProfileDto(userId, email, socialType);

            when(loadUserProfileViewPort.findUserProfileViewById(userId))
                    .thenReturn(Optional.of(profileDto));

            // when
            UserProfileDto result = userFacade.getUserProfile(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(userId);
            assertThat(result.email()).isEqualTo(email);
            assertThat(result.socialType()).isEqualTo(socialType);

            verify(loadUserProfileViewPort).findUserProfileViewById(userId);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 ID로 조회하면 UserNotFoundException이 발생한다")
        void getUserProfile_사용자없음_예외() {
            // given
            Long userId = 999L;

            when(loadUserProfileViewPort.findUserProfileViewById(userId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userFacade.getUserProfile(userId))
                    .isInstanceOf(UserNotFoundException.class);

            verify(loadUserProfileViewPort).findUserProfileViewById(userId);
        }

        @Test
        @DisplayName("네이버 소셜 타입의 프로필을 조회한다")
        void getUserProfile_네이버소셜타입() {
            // given
            Long userId = 1L;
            String email = "naver@example.com";
            SocialType socialType = SocialType.NAVER;

            UserProfileDto profileDto = new UserProfileDto(userId, email, socialType);

            when(loadUserProfileViewPort.findUserProfileViewById(userId))
                    .thenReturn(Optional.of(profileDto));

            // when
            UserProfileDto result = userFacade.getUserProfile(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.socialType()).isEqualTo(SocialType.NAVER);

            verify(loadUserProfileViewPort).findUserProfileViewById(userId);
        }

        @Test
        @DisplayName("카카오 소셜 타입의 프로필을 조회한다")
        void getUserProfile_다양한소셜타입() {
            // given
            Long userId = 1L;
            String email = "kakao@example.com";
            SocialType socialType = SocialType.KAKAO;

            UserProfileDto profileDto = new UserProfileDto(userId, email, socialType);

            when(loadUserProfileViewPort.findUserProfileViewById(userId))
                    .thenReturn(Optional.of(profileDto));

            // when
            UserProfileDto result = userFacade.getUserProfile(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.socialType()).isEqualTo(SocialType.KAKAO);

            verify(loadUserProfileViewPort).findUserProfileViewById(userId);
        }
    }
}

