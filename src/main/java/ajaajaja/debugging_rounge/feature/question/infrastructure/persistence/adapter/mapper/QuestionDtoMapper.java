package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter.mapper;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionDetailView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionDtoMapper {
    QuestionDetailDto toDto(QuestionDetailView questionDetailView);
}
