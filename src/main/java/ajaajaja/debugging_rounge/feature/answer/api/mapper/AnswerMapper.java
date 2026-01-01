package ajaajaja.debugging_rounge.feature.answer.api.mapper;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerUpdateRequest;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailWithRecommendDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
    @Mapping(target = "mine",
            expression = "java( loginUserId != null && java.util.Objects.equals(answerDetailDto.authorId(), loginUserId))")
    @Mapping(target = "myRecommendType", ignore = true)
    @Mapping(target = "recommendScore", ignore = true)
    AnswerDetailResponse toResponse(AnswerDetailDto answerDetailDto, Long loginUserId);
    
    @Mapping(target = "mine",
            expression = "java( loginUserId != null && java.util.Objects.equals(answerDetailWithRecommendDto.authorId(), loginUserId))")
    AnswerDetailResponse toResponse(AnswerDetailWithRecommendDto answerDetailWithRecommendDto, Long loginUserId);

    @Mapping(target = "authorId", source = "loginUserId")
    AnswerUpdateDto toDto(AnswerUpdateRequest answerUpdateRequest, Long id, Long loginUserId);
}
