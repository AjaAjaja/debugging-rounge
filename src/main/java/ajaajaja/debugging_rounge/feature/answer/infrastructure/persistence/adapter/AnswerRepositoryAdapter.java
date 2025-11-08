package ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.DeleteAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.AnswerJpaRepository;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.projection.AnswerDetailView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerRepositoryAdapter implements SaveAnswerPort, LoadAnswerPort, DeleteAnswerPort {

    private final AnswerJpaRepository answerJpaRepository;
    @Override
    public Answer save(Answer answer) {
        return answerJpaRepository.save(answer);
    }

    @Override
    public Page<AnswerDetailDto> findAllByQuestionId(Long questionId, Pageable pageable) {
        return answerJpaRepository.findAllByQuestionId(questionId, pageable).map(this::toDto);
    }

    @Override
    public Optional<Answer> findById(Long id) {
        return answerJpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        answerJpaRepository.deleteById(id);
    }

    @Override
    public void deleteAllByQuestionId(Long questionId) {
        answerJpaRepository.deleteAllByQuestionId(questionId);
    }

    private AnswerDetailDto toDto(AnswerDetailView answerDetailView) {
        return new AnswerDetailDto(
                answerDetailView.getId(),
                answerDetailView.getContent(),
                answerDetailView.getAuthorId(),
                answerDetailView.getAuthorEmail()
        );
    }

}
