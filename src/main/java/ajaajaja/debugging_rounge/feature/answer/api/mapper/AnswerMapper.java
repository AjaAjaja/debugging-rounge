package ajaajaja.debugging_rounge.feature.answer.api.mapper;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "mine",
            expression = "java( loginUserId != null && java.util.Objects.equals(answerDetailDto.userId(), loginUserId))")
    AnswerDetailResponse toResponse(AnswerDetailDto answerDetailDto, Long loginUserId);
}
