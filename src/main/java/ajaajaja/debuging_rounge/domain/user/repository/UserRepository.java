package ajaajaja.debuging_rounge.domain.user.repository;

import ajaajaja.debuging_rounge.domain.user.entity.User;
import ajaajaja.debuging_rounge.global.auth.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);
}
