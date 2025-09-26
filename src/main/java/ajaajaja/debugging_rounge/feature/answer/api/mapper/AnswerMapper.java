package ajaajaja.debugging_rounge.feature.answer.api.mapper;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "loginUserId", expression = "java(loginUserId)")
    AnswerDetailResponse toResponse(AnswerDetailDto dto, @Context Long loginUserId);
}
