package ajaajaja.debuging_rounge.domain.user.entity;

import ajaajaja.debuging_rounge.global.auth.SocialType;
import ajaajaja.debuging_rounge.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String refreshToken;

    public User(String email, SocialType socialType) {
        this.email = email;
        this.socialType = socialType;
        this.role = Role.ROLE_USER;
    }

    public static User of(String email, SocialType socialType) {
        return new User(email, socialType);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
