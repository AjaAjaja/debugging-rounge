package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.common.security.validator.OwnershipValidator;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailWithRecommendDto;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.DeleteAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.dto.AnswerRecommendScoreAndMyRecommendTypeDto;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.DeleteAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.LoadAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.question.api.sort.QuestionOrder;
import ajaajaja.debugging_rounge.feature.question.application.dto.*;
import ajaajaja.debugging_rounge.feature.question.application.mapper.QuestionWithAnswersMapper;
import ajaajaja.debugging_rounge.feature.question.application.port.in.*;
import ajaajaja.debugging_rounge.feature.question.application.port.out.DeleteQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.SaveQuestionPort;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionDeleteForbiddenException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionNotFoundForDeleteException;
import ajaajaja.debugging_rounge.feature.question.domain.exception.QuestionUpdateForbiddenException;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.DeleteQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionFacade implements
        CreateQuestionUseCase,
        GetQuestionWithAnswersQuery,
        GetQuestionListWithPreviewQuery,
        UpdateQuestionUseCase,
        DeleteQuestionUseCase {


    private final SaveQuestionPort saveQuestionPort;
    private final LoadQuestionPort loadQuestionPort;
    private final LoadQuestionRecommendPort loadQuestionRecommendPort;
    private final DeleteQuestionPort deleteQuestionPort;
    private final DeleteQuestionRecommendPort deleteQuestionRecommendPort;
    private final OwnershipValidator ownershipValidator;
    private final QuestionWithAnswersMapper mapper;

    private final LoadAnswerPort loadAnswerPort;
    private final LoadAnswerRecommendPort loadAnswerRecommendPort;
    private final DeleteAnswerPort deleteAnswerPort;
    private final DeleteAnswerRecommendPort deleteAnswerRecommendPort;

    @Override
    @Transactional
    public Long createQuestion(QuestionCreateDto questionCreateDto) {
        Question question = questionCreateDto.toEntity();

        Question savedQuestion = saveQuestionPort.save(question);

        return savedQuestion.getId();
    }


    @Override
    public Page<QuestionListDto> getQuestionsWithPreview(Pageable pageable, QuestionOrder order) {

        if (order == QuestionOrder.RECOMMEND) {
            return loadQuestionPort.findQuestionsWithPreviewForRecommend(pageable);
        }
        return loadQuestionPort.findQuestionsWithPreviewForLatest(pageable);
    }

    @Override
    public QuestionWithAnswersDto getQuestionWithAnswers(Long questionId, Long loginUserId, Pageable answerPageable) {

        QuestionDetailDto questionDetailDto =
                loadQuestionPort.findQuestionDetailById(questionId).orElseThrow(QuestionNotFoundException::new);
        RecommendType myRecommendTypeForQuestion =
                loadQuestionRecommendPort.findByQuestionIdAndUserId(questionId, loginUserId)
                        .map(QuestionRecommend::getType)
                        .orElse(RecommendType.NONE);

        Page<AnswerDetailWithRecommendDto> answerDetailWithRecommend = getAnswerDetailWithRecommend(questionId, loginUserId, answerPageable);


        return mapper.toQuestionWithAnswerDto(questionDetailDto, answerDetailWithRecommend, myRecommendTypeForQuestion);
    }

    @Override
    @Transactional
    public void updateQuestion(QuestionUpdateDto questionUpdateDto) {
        Question question = loadQuestionPort.findById(questionUpdateDto.id())
                .orElseThrow(QuestionNotFoundException::new);

        ownershipValidator.validateAuthor(question.getAuthorId(), questionUpdateDto.loginUserId(), QuestionUpdateForbiddenException::new);

        if (hasChanges(question, questionUpdateDto)) {
            question.update(questionUpdateDto.title(), questionUpdateDto.content());
        }
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId, Long loginUserId) {
        Question question = loadQuestionPort.findById(questionId)
                .orElseThrow(QuestionNotFoundForDeleteException::new);

        ownershipValidator.validateAuthor(question.getAuthorId(), loginUserId, QuestionDeleteForbiddenException::new);

        deleteQuestionPort.deleteById(question.getId());
    }

    private boolean hasChanges(Question question, QuestionUpdateDto questionUpdateDto) {
        return !Objects.equals(question.getTitle(), questionUpdateDto.title())
                || !Objects.equals(question.getContent(), questionUpdateDto.content());
    }

    private Page<AnswerDetailWithRecommendDto> getAnswerDetailWithRecommend(Long questionId, Long loginUserId, Pageable answerPageable) {

        Page<AnswerDetailDto> answerDetailDtoPage = loadAnswerPort.findAllByQuestionId(questionId, answerPageable);
        if (answerDetailDtoPage.isEmpty()) {
            return new PageImpl<>(List.of(), answerPageable, 0);
        }

        List<Long> answerIds = answerDetailDtoPage.getContent().stream().map(AnswerDetailDto::id).toList();

        List<AnswerRecommendScoreAndMyRecommendTypeDto> answerRecommendScoreAndMyType =
                loadAnswerRecommendPort.findRecommendScoreAndMyType(answerIds, loginUserId);

        Map<Long, AnswerRecommendScoreAndMyRecommendTypeDto> dtoMap =
                answerRecommendScoreAndMyType.stream()
                        .collect(HashMap::new, (m, d) -> m.put(d.answerId(), d), Map::putAll);

        List<AnswerDetailWithRecommendDto> dtoList =
                answerDetailDtoPage.getContent().stream()
                        .map(a -> AnswerDetailWithRecommendDto.of(a, dtoMap.get(a.id())))
                        .toList();

        return new PageImpl<>(dtoList, answerDetailDtoPage.getPageable(), answerDetailDtoPage.getTotalElements());
    }

}