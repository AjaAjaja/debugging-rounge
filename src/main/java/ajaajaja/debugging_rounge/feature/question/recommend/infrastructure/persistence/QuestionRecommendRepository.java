package ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    Integer findQuestionRecommendScoreByQuestionId(@Param("questionId") Long questionId);

    void deleteByQuestionIdAndUserId(@Param("questionId") Long questionId,
                                     @Param("userId") Long userId);

    void deleteAllByQuestionId(@Param("questionId") Long questionId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
    INSERT INTO question_recommend(question_id, user_id, type, created_date, last_modified_date)
    VALUES (:questionId, :userId, :type, NOW(), NOW())
    ON DUPLICATE KEY UPDATE
    type = VALUES(type), last_modified_date = NOW()
    """, nativeQuery = true)
    void insertOrUpdate(
            @Param("questionId") Long questionId,
            @Param("userId") Long userId,
            @Param("type") String type
    );
}
