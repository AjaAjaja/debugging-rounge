package ajaajaja.debugging_rounge.feature.question.application.mapper;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailWithRecommendDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface QuestionWithAnswersMapper {

    @Mapping(target = "questionId",  source = "questionDetailDto.questionId")
    @Mapping(target = "title",       source = "questionDetailDto.title")
    @Mapping(target = "content",     source = "questionDetailDto.content")
    @Mapping(target = "authorId",    source = "questionDetailDto.authorId")
    @Mapping(target = "authorEmail", source = "questionDetailDto.authorEmail")
    @Mapping(target = "answers",     source = "answerDetailWithRecommendDtoPage")
    @Mapping(target = "recommendScore", source = "questionDetailDto.recommendScore")
    @Mapping(target = "myRecommendType", source = "myRecommendType")
    QuestionWithAnswersDto toQuestionWithAnswerDto(
            QuestionDetailDto questionDetailDto,
            Page<AnswerDetailWithRecommendDto> answerDetailWithRecommendDtoPage,
            RecommendType myRecommendType
    );


}
