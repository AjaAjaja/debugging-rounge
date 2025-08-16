package ajaajaja.debugging_rounge.feature.user.dto;

import ajaajaja.debugging_rounge.feature.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDto {

    private String email;

    public static UserProfileDto of(User user) {
        return new UserProfileDto(user.getEmail());
    }

}
