package ajaajaja.debugging_rounge.feature.user.api.mapper;

import ajaajaja.debugging_rounge.feature.user.api.dto.UserProfileResponse;
import ajaajaja.debugging_rounge.feature.user.application.dto.UserProfileDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    UserProfileResponse toResponse(UserProfileDto view);
}
