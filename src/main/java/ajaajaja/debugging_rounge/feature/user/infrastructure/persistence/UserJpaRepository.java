package ajaajaja.debugging_rounge.feature.user.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.projection.UserProfileView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);
    @Query("""
              SELECT u.id AS id, u.email AS email, u.socialType AS socialType
              FROM User u
              WHERE u.id = :id
            """)
    Optional<UserProfileView> findUserProfileViewById(@Param("id") Long id);
}
