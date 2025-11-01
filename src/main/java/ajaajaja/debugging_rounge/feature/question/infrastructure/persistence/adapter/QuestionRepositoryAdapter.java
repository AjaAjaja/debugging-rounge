package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.port.out.DeleteQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.SaveQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.QuestionJpaRepository;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter.mapper.QuestionDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionRepositoryAdapter implements SaveQuestionPort, LoadQuestionPort, DeleteQuestionPort {

    private final QuestionJpaRepository questionJpaRepository;
    private final QuestionDtoMapper questionDtoMapper;

    @Override
    public Question save(Question question) {
        return questionJpaRepository.save(question);
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questionJpaRepository.findById(id);
    }

    @Override
    public Optional<QuestionDetailDto> findQuestionDetailById(Long id) {
        return questionJpaRepository.findQuestionDetailById(id).map(questionDtoMapper::toDto);
    }

    @Override
    public Page<QuestionListDto> findQuestionsWithPreviewForLatest(Pageable pageable) {
        return questionJpaRepository.findQuestionsWithPreviewForLatest(pageable).map(questionDtoMapper::toDto);
    }

    @Override
    public Page<QuestionListDto> findQuestionsWithPreviewForRecommend(Pageable pageable) {
        return questionJpaRepository.findQuestionsWithPreviewForRecommend(pageable).map(questionDtoMapper::toDto);
    }

    @Override
    public Boolean existsQuestionById(Long id) {
        return questionJpaRepository.existsQuestionById(id);
    }

    @Override
    public void deleteById(Long id) {
        questionJpaRepository.deleteById(id);
    }

}
