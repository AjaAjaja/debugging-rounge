package ajaajaja.debuging_rounge.domain.question.service;

import ajaajaja.debuging_rounge.domain.question.dto.QuestionDetailResponseDto;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionUpdateRequestDto;
import ajaajaja.debuging_rounge.domain.question.entity.Question;
import ajaajaja.debuging_rounge.domain.question.dto.QuestionCreateRequestDto;
import ajaajaja.debuging_rounge.domain.question.repository.QuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Long createQuestion(QuestionCreateRequestDto questionCreateRequestDto) {
        Question question = questionCreateRequestDto.toEntity(questionCreateRequestDto);

        Question savedQuestion = questionRepository.save(question);

        return savedQuestion.getId();
    }

    public QuestionDetailResponseDto findQuestionById(Long id) {
        Question question = getQuestionEntityById(id);

        return QuestionDetailResponseDto.fromEntity(question);
    }

    @Transactional
    public void updateQuestion(Long id, QuestionUpdateRequestDto questionUpdateRequestDto) {
        Question question = getQuestionEntityById(id);

        question.updateTitle(questionUpdateRequestDto.getTitle());
        question.updateContent(questionUpdateRequestDto.getContent());
    }

    private Question getQuestionEntityById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("질문을 찾을 수 없습니다."));
    }
}