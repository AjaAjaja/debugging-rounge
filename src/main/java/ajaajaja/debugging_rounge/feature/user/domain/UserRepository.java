package ajaajaja.debugging_rounge.feature.user.domain;

import ajaajaja.debugging_rounge.feature.user.domain.User;
import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);
}
