package ajaajaja.debugging_rounge.feature.user.application.dto;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;

public record UserProfileDto(
        Long userId,
        String email,
        SocialType socialType
) {}
