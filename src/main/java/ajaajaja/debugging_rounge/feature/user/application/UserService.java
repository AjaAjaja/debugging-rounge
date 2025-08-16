package ajaajaja.debugging_rounge.feature.user.application;

import ajaajaja.debugging_rounge.feature.user.domain.User;
import ajaajaja.debugging_rounge.feature.user.domain.UserRepository;
import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.domain.exception.UserNotFoundException;
import ajaajaja.debugging_rounge.feature.user.dto.UserProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User findOrRegister(String email, SocialType socialType) {
        return userRepository.findByEmailAndSocialType(email, socialType)
                .orElseGet(() -> userRepository.save(User.of(email, socialType)));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return UserProfileDto.of(user);
    }

}
