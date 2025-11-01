package ajaajaja.debugging_rounge.feature.question.infrastructure.persistence;


import ajaajaja.debugging_rounge.feature.question.domain.Question;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionDetailView;
import ajaajaja.debugging_rounge.feature.question.infrastructure.persistence.projection.QuestionListView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionJpaRepository extends JpaRepository<Question, Long> {

    @Query("""
            SELECT q.id AS id, q.title AS title, q.content AS content, u.email AS email, u.id AS authorId,
            SUM(
            CASE
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.UP THEN 1
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.DOWN THEN -1
            ELSE 0  END) AS recommendScore
            FROM Question q
            JOIN User u
            ON q.authorId = u.id
            LEFT JOIN QuestionRecommend qr
            ON q.id = qr.questionId
            WHERE q.id = :questionId
            GROUP BY q.id, q.title, q.content, u.email, u.id
            """)
    Optional<QuestionDetailView> findQuestionDetailById(@Param("questionId") Long questionId);

    @Query(value = """
            SELECT q.id AS id, q.title AS title,
            SUBSTRING(q.content, 1, 100) AS previewContent, u.email AS email,
            SUM(
            CASE
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.UP THEN 1
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.DOWN THEN -1
            ELSE 0  END) AS recommendScore
            FROM Question q
            JOIN User u ON q.authorId = u.id
            LEFT JOIN QuestionRecommend qr ON q.id = qr.questionId
            GROUP BY q.id
            """,
            countQuery = "SELECT COUNT(q) FROM Question q")
    Page<QuestionListView> findQuestionsWithPreviewForLatest(Pageable pageable);

    @Query(value = """
            SELECT q.id AS id, q.title AS title,
            SUBSTRING(q.content, 1, 100) AS previewContent, u.email AS email,
            SUM(
            CASE
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.UP THEN 1
            WHEN qr.type = ajaajaja.debugging_rounge.feature.question.recommend.domain.RecommendType.DOWN THEN -1
            ELSE 0  END) AS recommendScore
            FROM Question q
            JOIN User u ON q.authorId = u.id
            LEFT JOIN QuestionRecommend qr ON q.id = qr.questionId
            GROUP BY q.id
            ORDER BY recommendScore DESC , q.createdDate DESC, q.id DESC
            """,
            countQuery = "SELECT COUNT(q) FROM Question q")
    Page<QuestionListView> findQuestionsWithPreviewForRecommend(Pageable pageable);

    Boolean existsQuestionById(Long id);
}
