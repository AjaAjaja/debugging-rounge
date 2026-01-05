package ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.application.dto.AnswerDetailDto;
import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.support.MysqlJpaTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(AnswerRepositoryAdapter.class)
class AnswerRepositoryAdapterTest extends MysqlJpaTestSupport {

    @Autowired
    AnswerRepositoryAdapter adapter;

    @Test
    @DisplayName("질문 ID로 답변 목록을 조회한다")
    void findAllByQuestionId_답변존재_목록조회() {
        // given
        User author = createUser("author@example.com", SocialType.GOOGLE);
        Question question = createQuestion("질문 제목", "질문 내용", author.getId());

        String content1 = "첫 번째 답변입니다.";
        String content2 = "두 번째 답변입니다.";
        createAnswer(content1, question.getId(), author.getId());
        createAnswer(content2, question.getId(), author.getId());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AnswerDetailDto> result = adapter.findAllByQuestionId(question.getId(), pageable);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).hasSize(2);

        List<AnswerDetailDto> answers = result.getContent();
        assertThat(answers.get(0).content()).isEqualTo(content1);
        assertThat(answers.get(0).authorId()).isEqualTo(author.getId());
        assertThat(answers.get(0).authorEmail()).isEqualTo(author.getEmail());

        assertThat(answers.get(1).content()).isEqualTo(content2);
        assertThat(answers.get(1).authorId()).isEqualTo(author.getId());
        assertThat(answers.get(1).authorEmail()).isEqualTo(author.getEmail());
    }

    @Test
    @DisplayName("답변이 없는 질문은 빈 페이지를 반환한다")
    void findAllByQuestionId_답변없음_빈페이지() {
        // given
        User author = createUser("author@example.com", SocialType.GOOGLE);
        Question question = createQuestion("답변 없는 질문", "내용", author.getId());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AnswerDetailDto> result = adapter.findAllByQuestionId(question.getId(), pageable);

        // then
        assertThat(result).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    @Test
    @DisplayName("다른 질문의 답변은 조회되지 않는다")
    void findAllByQuestionId_다른질문_답변제외() {
        // given
        User author = createUser("author@example.com", SocialType.GOOGLE);
        Question question1 = createQuestion("질문1", "내용1", author.getId());
        Question question2 = createQuestion("질문2", "내용2", author.getId());

        createAnswer("질문1의 답변", question1.getId(), author.getId());
        createAnswer("질문2의 답변", question2.getId(), author.getId());

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AnswerDetailDto> result = adapter.findAllByQuestionId(question1.getId(), pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).content()).isEqualTo("질문1의 답변");
    }

    @Test
    @DisplayName("존재하지 않는 질문 ID로 조회하면 빈 페이지를 반환한다")
    void findAllByQuestionId_존재하지않는질문_빈페이지() {
        // given
        Long nonExistentQuestionId = 99999L;
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<AnswerDetailDto> result = adapter.findAllByQuestionId(nonExistentQuestionId, pageable);

        // then
        assertThat(result).isEmpty();
    }
}

