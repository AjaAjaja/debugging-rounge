package ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence;

import ajaajaja.debugging_rounge.feature.answer.recommend.domain.AnswerRecommend;
import ajaajaja.debugging_rounge.feature.answer.recommend.infrasturture.persistence.projection.AnswerRecommendScoreAndMyTypeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRecommendRepository extends JpaRepository<AnswerRecommend, Long> {

    @Query("""
            SELECT ar.answerId as answerId,
            SUM(
                CASE
                WHEN ar.type = ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType.UP   THEN 1
                WHEN ar.type = ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType.DOWN THEN -1
                ELSE 0
                END
            ) AS answerRecommendScore,
            COALESCE(
                    MAX(
                    CASE
                    WHEN ar.userId = :userId THEN ar.type
                    END
                    ), ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType.NONE)
            AS myAnswerRecommendType
            FROM AnswerRecommend ar
            WHERE ar.answerId IN :answerIds
            GROUP BY ar.answerId
            """)
    List<AnswerRecommendScoreAndMyTypeView> getAnswerRecommendScoreAndMyType(@Param("answerIds") List<Long> answerIds,
                                                                             @Param("userId") Long userId);

    void deleteByAnswerIdAndUserId(@Param("answerId") Long answerId, @Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query(value = """
            INSERT INTO answer_recommend(answer_id, user_id, type, created_date, last_modified_date)
            VALUES (:answerId, :userId, :recommendType, NOW(), NOW())
            ON DUPLICATE KEY UPDATE
            type = VALUES(type), last_modified_date = NOW()
            """, nativeQuery = true)
    void insertOrUpdate(
            @Param("answerId") Long answerId,
            @Param("userId") Long userId,
            @Param("recommendType") String recommendType
    );

    @Query("""
            SELECT COALESCE( SUM(
                        CASE
                        WHEN ar.type = ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType.UP THEN 1
                        WHEN ar.type = ajaajaja.debugging_rounge.feature.answer.recommend.domain.RecommendType.DOWN THEN -1
                        ELSE 0 END), 0)
            FROM AnswerRecommend ar
            WHERE ar.answerId =:answerId
            """)
    Integer getAnswerRecommendScoreByAnswerId(@Param("answerId") Long answerId);

}
