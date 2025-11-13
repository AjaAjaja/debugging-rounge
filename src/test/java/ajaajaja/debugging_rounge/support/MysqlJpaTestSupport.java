package ajaajaja.debugging_rounge.support;

import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.QuestionJpaRepository;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence.QuestionRecommendRepository;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class MysqlJpaTestSupport {

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.40")
            .withDatabaseName("debugging_rounge")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    UserJpaRepository userJpaRepository;

    @Autowired
    QuestionJpaRepository questionJpaRepository;

    @Autowired
    QuestionRecommendRepository questionRecommendRepository;

    protected User saveUser(String email, SocialType socialType) {
        User user = User.of(email, socialType);
        return userJpaRepository.save(user);
    }

    protected Question saveQuestion(String title, String content, Long authorId) {
        Question question = Question.of(title, content, authorId);
        return questionJpaRepository.save(question);
    }

    protected QuestionRecommend saveQuestionRecommend(RecommendType recommendType, Long questionId, Long userId) {
        QuestionRecommend questionRecommend = QuestionRecommend.of(recommendType, questionId, userId);
        return questionRecommendRepository.save(questionRecommend);
    }
}
