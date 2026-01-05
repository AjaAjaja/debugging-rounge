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
        User author = createUser("test1@test.com", SocialType.GOOGLE);
        User user2 = createUser("test2@test.com", SocialType.GOOGLE);
        User user3 = createUser("test3@test.com", SocialType.GOOGLE);

        String testTitle = "testTitle";
        String testContent = "abcdefghijklmnopqrstuvwxyz";
        Question question = createQuestion(testTitle, testContent, author.getId());
        Long questionId = question.getId();
        createQuestionRecommend(RecommendType.DOWN, questionId, user2.getId());
        createQuestionRecommend(RecommendType.DOWN, questionId, user3.getId());

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
        User author1 = createUser("test1@test.com", SocialType.GOOGLE);

        createQuestion("testTitle1", "a".repeat(30), author1.getId());
        createQuestion("testTitle2", "b".repeat(80), author1.getId());
        createQuestion("testTitle3", "c".repeat(150), author1.getId());

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
        User author = createUser("test1@test.com", SocialType.GOOGLE);
        User user2 = createUser("test2@test.com", SocialType.GOOGLE);
        User user3 = createUser("test3@test.com", SocialType.GOOGLE);
        User user4 = createUser("test4@test.com", SocialType.GOOGLE);

        Question question1 = createQuestion("second", "a".repeat(30), author.getId());
        Question question2 = createQuestion("first", "b".repeat(80), author.getId());
        Question question3 = createQuestion("third", "c".repeat(150), author.getId());

        // question1 - 2점
        createQuestionRecommend(RecommendType.UP, question1.getId(), user2.getId());
        createQuestionRecommend(RecommendType.UP, question1.getId(), user3.getId());

        // question2 - 3점
        createQuestionRecommend(RecommendType.UP, question2.getId(), user2.getId());
        createQuestionRecommend(RecommendType.UP, question2.getId(), user3.getId());
        createQuestionRecommend(RecommendType.UP, question2.getId(), user4.getId());

        // question3 - -1점
        createQuestionRecommend(RecommendType.DOWN, question3.getId(), user2.getId());

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
