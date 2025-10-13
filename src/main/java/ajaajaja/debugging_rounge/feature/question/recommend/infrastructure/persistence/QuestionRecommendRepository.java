package ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionRecommendRepository extends JpaRepository<QuestionRecommend, Long> {
    Optional<QuestionRecommend> findByQuestionIdAndUserId(@Param("questionId") Long questionId,
                                                          @Param("userId") Long userId);

    @Query("""
    SELECT SUM(
            CASE
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.UP THEN 1
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.DOWN THEN -1
            ELSE 0  END) AS recommendScore
    FROM QuestionRecommend qr
    WHERE qr.questionId =:questionId
    """)
    Integer getQuestionRecommendScoreByQuestionId(@Param("questionId") Long questionId);

    void deleteByQuestionIdAndUserId(@Param("questionId") Long questionId,
                                     @Param("userId") Long userId);
}
