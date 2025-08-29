package ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.application.port.out.LoadUserPort;
import ajaajaja.debugging_rounge.feature.user.application.port.out.LoadUserProfileViewPort;
import ajaajaja.debugging_rounge.feature.user.application.port.out.SaveUserPort;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.UserJpaRepository;
import ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.projection.UserProfileView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements LoadUserPort, SaveUserPort, LoadUserProfileViewPort {

    private final UserJpaRepository jpa;

    @Override
    public Optional<User> findByEmailAndSocialType(String email, SocialType socialType) {
        return jpa.findByEmailAndSocialType(email, socialType);
    }

    @Override
    public User save(User user) {
        return jpa.save(user);
    }

    @Override
    public Optional<UserProfileDto> findUserProfileViewById(Long id) {
        return jpa.findUserProfileViewById(id).map(this::toDto);
    }

    private UserProfileDto toDto(UserProfileView userProfileView) {
        return new UserProfileDto(
                userProfileView.getId(),
                userProfileView.getEmail(),
                userProfileView.getSocialType());
    }
}
