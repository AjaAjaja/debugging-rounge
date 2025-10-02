package ajaajaja.debugging_rounge.feature.question.api.mapper;

import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponse;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponse;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
    @Mapping(target = "mine",
            expression = "java( loginUserId != null && java.util.Objects.equals(questionDetailDto.authorId(), loginUserId))")
    QuestionDetailResponse toResponse(QuestionDetailDto questionDetailDto, Long loginUserId);

    @Mapping(target = "previewContent", source = "previewContent", qualifiedByName = "clean")
    QuestionListResponse toResponse(QuestionListDto dto);

    @Named("clean")
    default String clean(String rawContent) {
        if (rawContent == null) return "";
        String cleaned = rawContent
                .replaceAll("[\\r\\n]+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
        return cleaned.length() > 50 ? cleaned.substring(0, 50) + "..." : cleaned;
    }

}
