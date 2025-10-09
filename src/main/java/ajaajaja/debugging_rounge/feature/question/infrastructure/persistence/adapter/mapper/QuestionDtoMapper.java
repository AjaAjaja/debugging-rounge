package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter.mapper;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionDetailView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuestionDtoMapper {

    @Mapping(target = "questionId", source = "id")
    @Mapping(target = "authorEmail", source = "email")
    QuestionDetailDto toDto(QuestionDetailView questionDetailView);
}
