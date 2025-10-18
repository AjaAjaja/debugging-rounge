package ajaajaja.debugging_rounge.feature.question.recommend.api.mapper;

import ajaajaja.debugging_rounge.feature.question.recommend.api.dto.QuestionRecommendScoreAndMyRecommendTypeResponse;
import ajaajaja.debugging_rounge.feature.question.recommend.api.dto.QuestionRecommendUpdateRequest;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.question.recommend.application.dto.QuestionRecommendUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionRecommendMapper {

    @Mapping(target = "userId", source = "loginUserId")
    QuestionRecommendUpdateDto toDto(
            QuestionRecommendUpdateRequest questionRecommendUpdateRequest,
            Long questionId,
            Long loginUserId);

    QuestionRecommendScoreAndMyRecommendTypeResponse toResponse(QuestionRecommendScoreAndMyRecommendTypeDto dto);
}
