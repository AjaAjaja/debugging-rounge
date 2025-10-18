package ajaajaja.debugging_rounge.feature.question.api.mapper;

import ajaajaja.debugging_rounge.feature.answer.api.dto.AnswerDetailResponse;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionWithAnswerResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponse;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionWithAnswersDto;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface QuestionResponseMapper {

    @Mapping(target = "previewContent", source = "previewContent", qualifiedByName = "clean")
    QuestionListResponse toQuestionListResponse(QuestionListDto dto);
    @Mapping(target = "mine",
            expression = "java( loginUserId != null && java.util.Objects.equals(questionWithAnswersDto.authorId(), loginUserId))")
    QuestionWithAnswerResponse toQuestionWithAnswersResponse(QuestionWithAnswersDto questionWithAnswersDto, @Context Long loginUserId);

    @Mapping(target = "mine",
            expression = "java( loginUserId != null && java.util.Objects.equals(answerDetailDto.authorId(), loginUserId))")
    AnswerDetailResponse toAnswerDetailResponse(AnswerDetailDto answerDetailDto, @Context Long loginUserId);

    @Named("clean")
    default String clean(String rawContent) {
        if (rawContent == null) return "";
        String cleaned = rawContent
                .replaceAll("[\\r\\n]+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
        return cleaned.length() > 50 ? cleaned.substring(0, 50) + "..." : cleaned;
    }

    default Page<AnswerDetailResponse> toAnswerDetailPage(Page<AnswerDetailDto> answerDtoPage, @Context Long loginUserId) {
        if (answerDtoPage == null) {
            return Page.empty();
        }
        return answerDtoPage.map(dto -> toAnswerDetailResponse(dto, loginUserId));
    }

}
