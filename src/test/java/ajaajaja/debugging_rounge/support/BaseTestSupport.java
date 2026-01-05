package ajaajaja.debugging_rounge.support;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.image.domain.AnswerImage;
import ajaajaja.debugging_rounge.feature.answer.image.infrastructure.persistence.AnswerImageJpaRepository;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.AnswerJpaRepository;
import ajaajaja.debugging_rounge.feature.auth.domain.SocialType;
import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.image.domain.QuestionImage;
import ajaajaja.debugging_rounge.feature.question.image.infrastructure.persistence.QuestionImageJpaRepository;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.QuestionJpaRepository;
import ajaajaja.debugging_rounge.feature.user.domain.model.User;
import ajaajaja.debugging_rounge.feature.user.infrastructure.persistence.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@ActiveProfiles("test")
public abstract class BaseTestSupport {

    @Container
    @ServiceConnection
    @SuppressWarnings("resource")
    protected static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.40")
            .withDatabaseName("debugging_rounge")
            .withUsername("test")
            .withPassword("test");

    // === ApplicationContext ===
    
    @Autowired
    protected ApplicationContext applicationContext;

    // === Repository 주입 ===

    @Autowired
    protected UserJpaRepository userJpaRepository;

    @Autowired
    protected QuestionJpaRepository questionJpaRepository;

    @Autowired
    protected QuestionImageJpaRepository questionImageJpaRepository;

    @Autowired
    protected AnswerJpaRepository answerJpaRepository;

    @Autowired
    protected AnswerImageJpaRepository answerImageJpaRepository;

    // === User Helper Methods ===

    protected User createUser(String email) {
        return createUser(email, SocialType.GOOGLE);
    }

    protected User createUser(String email, SocialType socialType) {
        User user = User.of(email, socialType);
        return userJpaRepository.save(user);
    }

    // === Question Helper Methods ===

    protected Question createQuestion(String title, String content, Long authorId) {
        Question question = Question.of(title, content, authorId);
        return questionJpaRepository.save(question);
    }

    protected void saveQuestionImages(Long questionId, List<String> imageUrls) {
        List<QuestionImage> images = imageUrls.stream()
                .map(url -> QuestionImage.of(questionId, url, imageUrls.indexOf(url)))
                .toList();
        questionImageJpaRepository.saveAll(images);
    }

    // === Answer Helper Methods ===

    protected Answer createAnswer(String content, Long questionId, Long authorId) {
        Answer answer = Answer.of(content, questionId, authorId);
        return answerJpaRepository.save(answer);
    }

    protected void saveAnswerImages(Long answerId, List<String> imageUrls) {
        List<AnswerImage> images = imageUrls.stream()
                .map(url -> AnswerImage.of(answerId, url, imageUrls.indexOf(url)))
                .toList();
        answerImageJpaRepository.saveAll(images);
    }
}

