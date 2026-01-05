package ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.support.MysqlJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(UserRepositoryAdapter.class)
class UserRepositoryAdapterTest extends MysqlJpaTestSupport {

    @Autowired
    UserRepositoryAdapter adapter;

    @Test
    @DisplayName("새로운 사용자를 저장한다")
    void save_새로운사용자_저장() {
        // given
        String email = "test@example.com";
        SocialType socialType = SocialType.GOOGLE;
        User user = User.of(email, socialType);

        // when
        User savedUser = adapter.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(email);
        assertThat(savedUser.getSocialType()).isEqualTo(socialType);
    }

    @Test
    @DisplayName("이메일과 소셜타입으로 사용자를 찾는다")
    void findByEmailAndSocialType_사용자존재_조회성공() {
        // given
        String email = "test@example.com";
        SocialType socialType = SocialType.KAKAO;
        User user = createUser(email, socialType);

        // when
        Optional<User> result = adapter.findByEmailAndSocialType(email, socialType);

        // then
        assertThat(result).isPresent();
        User foundUser = result.get();
        assertThat(foundUser.getId()).isEqualTo(user.getId());
        assertThat(foundUser.getEmail()).isEqualTo(email);
        assertThat(foundUser.getSocialType()).isEqualTo(socialType);
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 찾으면 빈 Optional을 반환한다")
    void findByEmailAndSocialType_사용자없음_빈Optional() {
        // given
        String email = "notexist@example.com";
        SocialType socialType = SocialType.GOOGLE;

        // when
        Optional<User> result = adapter.findByEmailAndSocialType(email, socialType);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("같은 이메일이지만 다른 소셜타입이면 별도 사용자로 조회된다")
    void findByEmailAndSocialType_같은이메일_다른소셜타입() {
        // given
        String email = "test@example.com";
        User googleUser = createUser(email, SocialType.GOOGLE);
        User kakaoUser = createUser(email, SocialType.KAKAO);

        // when
        Optional<User> foundGoogle = adapter.findByEmailAndSocialType(email, SocialType.GOOGLE);
        Optional<User> foundKakao = adapter.findByEmailAndSocialType(email, SocialType.KAKAO);

        // then
        assertThat(foundGoogle).isPresent();
        assertThat(foundKakao).isPresent();
        assertThat(foundGoogle.get().getId()).isEqualTo(googleUser.getId());
        assertThat(foundKakao.get().getId()).isEqualTo(kakaoUser.getId());
        assertThat(foundGoogle.get().getId()).isNotEqualTo(foundKakao.get().getId());
    }

    @Test
    @DisplayName("사용자 ID로 프로필을 조회한다")
    void findUserProfileViewById_사용자존재_프로필조회() {
        // given
        String email = "profile@example.com";
        SocialType socialType = SocialType.NAVER;
        User savedUser = createUser(email, socialType);

        // when
        Optional<UserProfileDto> result = adapter.findUserProfileViewById(savedUser.getId());

        // then
        assertThat(result).isPresent();
        UserProfileDto profile = result.get();
        assertThat(profile.userId()).isEqualTo(savedUser.getId());
        assertThat(profile.email()).isEqualTo(email);
        assertThat(profile.socialType()).isEqualTo(socialType);
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 프로필 조회 시 빈 Optional을 반환한다")
    void findUserProfileViewById_사용자없음_빈Optional() {
        // given
        Long nonExistentUserId = 99999L;

        // when
        Optional<UserProfileDto> result = adapter.findUserProfileViewById(nonExistentUserId);

        // then
        assertThat(result).isEmpty();
    }
}

