package ajaajaja.debugging_rounge.support;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.AnswerRecommend;
import ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.AnswerRecommendRepository;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
import ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence.QuestionRecommendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * JPA Repository 슬라이스 테스트를 위한 베이스 클래스
 * - @DataJpaTest: JPA 관련 빈만 로드
 * - BaseTestSupport 상속: 공통 설정 및 헬퍼 메서드 사용
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class MysqlJpaTestSupport extends BaseTestSupport {

    @Autowired
    protected QuestionRecommendRepository questionRecommendRepository;

    @Autowired
    protected AnswerRecommendRepository answerRecommendRepository;

    // === 추천 관련 헬퍼 메서드 (JPA 테스트 전용) ===

    protected QuestionRecommend createQuestionRecommend(RecommendType recommendType, Long questionId, Long userId) {
        QuestionRecommend questionRecommend = QuestionRecommend.of(recommendType, questionId, userId);
        return questionRecommendRepository.save(questionRecommend);
    }

    protected AnswerRecommend createAnswerRecommend(
            ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType recommendType,
            Long answerId, Long userId) {
        AnswerRecommend answerRecommend = AnswerRecommend.of(recommendType, answerId, userId);
        return answerRecommendRepository.save(answerRecommend);
    }
}
