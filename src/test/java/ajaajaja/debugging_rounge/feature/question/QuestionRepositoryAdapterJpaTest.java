package ajaajaja.debugging_rounge.feature.question;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionDetailDto;
import ajaajaja.debugging_rounge.feature.question.application.dto.QuestionListDto;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter.QuestionRepositoryAdapter;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.adapter.mapper.QuestionDtoMapperImpl;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.support.MysqlJpaTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({ QuestionRepositoryAdapter.class, QuestionDtoMapperImpl.class })
public class QuestionRepositoryAdapterJpaTest extends MysqlJpaTestSupport {

    @Autowired
    QuestionRepositoryAdapter adapter;

    @Test
    void findQuestionDetailById_질문이_존재하면_DTO를_반환한다() {

        // given
        User author = saveUser("test1@test.com", SocialType.GOOGLE);
        User user2 = saveUser("test2@test.com", SocialType.GOOGLE);
        User user3 = saveUser("test3@test.com", SocialType.GOOGLE);

        String testTitle = "testTitle";
        String testContent = "abcdefghijklmnopqrstuvwxyz";
        Question question = saveQuestion(testTitle, testContent, author.getId());
        Long questionId = question.getId();
        saveQuestionRecommend(RecommendType.DOWN, questionId, user2.getId());
        saveQuestionRecommend(RecommendType.DOWN, questionId, user3.getId());

        // when
        Optional<QuestionDetailDto> result = adapter.findQuestionDetailById(questionId);

        // then
        assertThat(result).isPresent();
        QuestionDetailDto dto = result.get();
        assertThat(dto.questionId()).isEqualTo(questionId);
        assertThat(dto.title()).isEqualTo(testTitle);
        assertThat(dto.content()).isEqualTo(testContent);
        assertThat(dto.authorId()).isEqualTo(author.getId());
        assertThat(dto.authorEmail()).isEqualTo(author.getEmail());
        assertThat(dto.recommendScore()).isEqualTo(-2);
    }

    @Test
    void findQuestionsWithPreviewForLatest_질문_최신순_정렬() {

        // given
        User author1 = saveUser("test1@test.com", SocialType.GOOGLE);

        saveQuestion("testTitle1", "a".repeat(30), author1.getId());
        saveQuestion("testTitle2", "b".repeat(80), author1.getId());
        saveQuestion("testTitle3", "c".repeat(150), author1.getId());

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(
                Sort.Order.desc("createdDate"),
                Sort.Order.desc("id")));

        //when
        Page<QuestionListDto> result = adapter.findQuestionsWithPreviewForLatest(pageable);

        // then
        List<QuestionListDto> dtoList = result.getContent();

        assertThat(dtoList).hasSize(3);

        assertThat(dtoList.get(0).title()).isEqualTo("testTitle3");
        assertThat(dtoList.get(1).title()).isEqualTo("testTitle2");
        assertThat(dtoList.get(2).title()).isEqualTo("testTitle1");

        assertThat(dtoList.get(0).previewContent().length()).isEqualTo(100);
        assertThat(dtoList.get(1).previewContent().length()).isEqualTo(80);
        assertThat(dtoList.get(2).previewContent().length()).isEqualTo(30);

    }

    @Test
    void findQuestionsWithPreviewForRecommend_질문_추천순_정렬() {

        // given
        User author = saveUser("test1@test.com", SocialType.GOOGLE);
        User user2 = saveUser("test2@test.com", SocialType.GOOGLE);
        User user3 = saveUser("test3@test.com", SocialType.GOOGLE);
        User user4 = saveUser("test4@test.com", SocialType.GOOGLE);

        Question question1 = saveQuestion("second", "a".repeat(30), author.getId());
        Question question2 = saveQuestion("first", "b".repeat(80), author.getId());
        Question question3 = saveQuestion("third", "c".repeat(150), author.getId());

        // question1 - 2점
        saveQuestionRecommend(RecommendType.UP, question1.getId(), user2.getId());
        saveQuestionRecommend(RecommendType.UP, question1.getId(), user3.getId());

        // question2 - 3점
        saveQuestionRecommend(RecommendType.UP, question2.getId(), user2.getId());
        saveQuestionRecommend(RecommendType.UP, question2.getId(), user3.getId());
        saveQuestionRecommend(RecommendType.UP, question2.getId(), user4.getId());

        // question3 - -1점
        saveQuestionRecommend(RecommendType.DOWN, question3.getId(), user2.getId());

        PageRequest pageable = PageRequest.of(0, 10);

        //when
        Page<QuestionListDto> result = adapter.findQuestionsWithPreviewForRecommend(pageable);

        // then
        List<QuestionListDto> dtoList = result.getContent();

        assertThat(dtoList).hasSize(3);

        assertThat(dtoList.get(0).title()).isEqualTo("first");
        assertThat(dtoList.get(1).title()).isEqualTo("second");
        assertThat(dtoList.get(2).title()).isEqualTo("third");

        assertThat(dtoList.get(0).previewContent().length()).isEqualTo(80);
        assertThat(dtoList.get(1).previewContent().length()).isEqualTo(30);
        assertThat(dtoList.get(2).previewContent().length()).isEqualTo(100);
    }

}
