package ajaajaja.debuging_rounge.domain.user.service;

import ajaajaja.debuging_rounge.domain.user.entity.User;
import ajaajaja.debuging_rounge.domain.user.repository.UserRepository;
import ajaajaja.debuging_rounge.global.auth.SocialType;
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

}
