package ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType;
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
    Integer getQuestionRecommendScoreByQuestionId(@Param("questionId") Long questionId);

    void deleteByQuestionIdAndUserId(@Param("questionId") Long questionId,
                                     @Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
    INSERT INTO QuestionRecommend qr(quetion_id, user_id, type, created_date, last_modified_date_date)
    VALUES (:qid, :uid, :type, NOW(), NOW())
    ON DUPLICATE KEY UPDATE
    type = VALUES(type), last_modified_date_date= NOW()
    """, nativeQuery = true)
    void insertOrUpdate(
            @Param("questionId") Long questionId,
            @Param("userId") Long userId,
            @Param("recommendType") RecommendType type
    );
}
