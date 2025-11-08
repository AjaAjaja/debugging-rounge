package ajaajaja.debugging_rounge.feature.question.application;

import ajaajaja.debugging_rounge.common.security.validator.OwnershipValidator;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.DeleteAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.application.port.out.LoadAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.DeleteAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.answer.recommend.application.port.out.LoadAnswerRecommendPort;
import ajaajaja.debugging_rounge.feature.question.api.sort.QuestionOrder;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.application.mapper.QuestionWithAnswersMapper;
import ajaajaja.debugging_rounge.feature.question.application.port.out.DeleteQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.LoadQuestionPort;
import ajaajaja.debugging_rounge.feature.question.application.port.out.SaveQuestionPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.DeleteQuestionRecommendPort;
import ajaajaja.debugging_rounge.feature.question.recommend.application.port.out.LoadQuestionRecommendPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionFacadeTest {

    @Mock
    SaveQuestionPort saveQuestionPort;
    @Mock
    LoadQuestionPort loadQuestionPort;
    @Mock
    LoadQuestionRecommendPort loadQuestionRecommendPort;
    @Mock
    DeleteQuestionPort deleteQuestionPort;
    @Mock
    DeleteQuestionRecommendPort deleteQuestionRecommendPort;
    @Mock
    OwnershipValidator ownershipValidator;
    @Mock
    QuestionWithAnswersMapper mapper;
    @Mock
    LoadAnswerPort loadAnswerPort;
    @Mock
    LoadAnswerRecommendPort loadAnswerRecommendPort;
    @Mock
    DeleteAnswerPort deleteAnswerPort;
    @Mock
    DeleteAnswerRecommendPort deleteAnswerRecommendPort;

    @InjectMocks
    QuestionFacade questionFacade;

    @Test
    void order가_LATEST면_최신순포트를_이용한다() {

        // given
        PageRequest pageable = PageRequest.of(0, 20);
        PageImpl<QuestionListDto> expected = new PageImpl<>(List.of());

        when(loadQuestionPort.findQuestionsWithPreviewForLatest(pageable)).thenReturn(expected);

        // when
        Page<QuestionListDto> result = questionFacade.findQuestionsWithPreview(pageable, QuestionOrder.LATEST);

        // then
        verify(loadQuestionPort).findQuestionsWithPreviewForLatest(pageable);
        verify(loadQuestionPort, never()).findQuestionsWithPreviewForRecommend(any());
        assertThat(result).isSameAs(expected);
    }

    @Test
    void order가_RECOMMEND면_추천순포트를_이용한다() {

        // given
        PageRequest pageable = PageRequest.of(0, 20);
        PageImpl<QuestionListDto> expected = new PageImpl<>(List.of());

        when(loadQuestionPort.findQuestionsWithPreviewForRecommend(pageable)).thenReturn(expected);

        // when
        Page<QuestionListDto> result = questionFacade.findQuestionsWithPreview(pageable, QuestionOrder.RECOMMEND);

        // then
        verify(loadQuestionPort).findQuestionsWithPreviewForRecommend(pageable);
        verify(loadQuestionPort, never()).findQuestionsWithPreviewForLatest(any());
        assertThat(result).isSameAs(expected);
    }



}