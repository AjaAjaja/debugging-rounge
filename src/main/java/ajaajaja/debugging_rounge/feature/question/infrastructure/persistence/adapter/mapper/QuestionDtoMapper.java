package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter.mapper;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionDetailView;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionListView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionDtoMapper {

    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "authorEmail", source = "email")
    @Mapping(target = "imageUrls", ignore = true) // imageUrls는 별도로 조회하여 설정됨
    QuestionDetailDto toDto(QuestionDetailView questionDetailView);

    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "authorEmail", source = "email")
    QuestionListDto toDto(QuestionListView questionListView);
}
