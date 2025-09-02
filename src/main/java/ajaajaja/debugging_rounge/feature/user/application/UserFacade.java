package ajaajaja.debugging_rounge.feature.user.application;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import ajaajaja.debugging_rounge.feature.user.application.port.in.FindOrRegisterUserUseCase;
import ajaajaja.debugging_rounge.feature.user.application.port.in.GetUserProfileQuery;
import ajaajaja.debugging_rounge.feature.user.application.port.out.LoadUserPort;
import ajaajaja.debugging_rounge.feature.user.application.port.out.LoadUserProfileViewPort;
import ajaajaja.debugging_rounge.feature.user.application.port.out.SaveUserPort;
import ajaajaja.debugging_rounge.feature.user.domain.exception.UserNotFoundException;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacade implements GetUserProfileQuery, FindOrRegisterUserUseCase {

    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final LoadUserProfileViewPort loadUserProfileViewPort;

    @Override
    @Transactional
    public User findOrRegister(String email, SocialType socialType) {
        return loadUserPort.findByEmailAndSocialType(email, socialType)
                .orElseGet(() -> saveUserPort.save(User.of(email, socialType)));
    }

    @Override
    public UserProfileDto getUserProfile(Long userId) {
        return loadUserProfileViewPort.findUserProfileViewById(userId).orElseThrow(UserNotFoundException::new);
    }

}
