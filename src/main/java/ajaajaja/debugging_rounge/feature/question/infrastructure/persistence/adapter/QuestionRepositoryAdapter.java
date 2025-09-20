package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.port.out.DeleteQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.SaveQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.QuestionJpaRepository;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionDetailView;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionListView;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionRepositoryAdapter implements SaveQuestionPort, LoadQuestionPort, DeleteQuestionPort {

    private final QuestionJpaRepository questionJpaRepository;

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
        return questionJpaRepository.findQuestionDetailById(id).map(this::toDto);
    }

    @Override
    public Page<QuestionListDto> findQuestionsWithPreview(Pageable pageable) {
        return questionJpaRepository.findQuestionsWithPreview(pageable).map(this::toDto);
    }

    @Override
    public Boolean existsQuestionById(Long id) {
        return questionJpaRepository.existsQuestionById(id);
    }

    @Override
    public void deleteById(Long id) {
        questionJpaRepository.deleteById(id);
    }

    private QuestionDetailDto toDto(QuestionDetailView questionDetailView) {
        return new QuestionDetailDto(
                questionDetailView.getId(),
                questionDetailView.getTitle(),
                questionDetailView.getContent(),
                questionDetailView.getUserId(),
                questionDetailView.getEmail());
    }

    private QuestionListDto toDto(QuestionListView questionListView) {
        return new QuestionListDto(
                questionListView.getId(),
                questionListView.getTitle(),
                questionListView.getPreviewContent(),
                questionListView.getEmail());
    }
}
