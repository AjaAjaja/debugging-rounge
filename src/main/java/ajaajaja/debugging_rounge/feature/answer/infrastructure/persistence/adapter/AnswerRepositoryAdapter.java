package ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.AnswerJpaRepository;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.projection.AnswerDetailView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerRepositoryAdapter implements SaveAnswerPort, LoadAnswerPort {

    private final AnswerJpaRepository answerJpaRepository;
    @Override
    public Answer save(Answer answer) {
        return answerJpaRepository.save(answer);
    }

    @Override
    public Page<AnswerDetailDto> findPageByQuestionId(Long questionId, Pageable pageable) {
        return answerJpaRepository.findPageByQuestionId(questionId, pageable).map(this::toDto);
    }

    private AnswerDetailDto toDto(AnswerDetailView answerDetailView) {
        return new AnswerDetailDto(
                answerDetailView.getId(),
                answerDetailView.getContent(),
                answerDetailView.getUserId()
        );
    }
}
