package ajaajaja.debugging_rounge.feature.user.domain.model;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = @UniqueConstraint(name = "uk_user_email_social",
                columnNames = {"email","social_type"})
)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 191)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder(access = AccessLevel.PRIVATE)
    private User(String email, SocialType socialType, Role role) {
        this.email = java.util.Objects.requireNonNull(email);
        this.socialType = java.util.Objects.requireNonNull(socialType);
        this.role= role;
    }

    public static User of(String email, SocialType socialType) {
        return User.builder()
                .email(email)
                .socialType(socialType)
                .role(Role.ROLE_USER)
                .build();
    }

}
