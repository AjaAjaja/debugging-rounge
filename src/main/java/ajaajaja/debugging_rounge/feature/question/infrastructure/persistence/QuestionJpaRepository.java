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
            SELECT q.id AS id, q.title AS title, q.content AS content, u.email AS email, u.id AS userId
            FROM Question q
            JOIN User u
            ON q.userId = u.id
            WHERE q.id = :questionId
            """)
    Optional<QuestionDetailView> findQuestionDetailById(@Param("questionId") Long questionId);

    @Query("""
            SELECT q.id AS id, q.title AS title,
            SUBSTRING(q.content, 1, 100) AS previewContent , u.email AS email
            FROM Question q
            JOIN User u
            ON q.userId = u.id
            """)
    Page<QuestionListView> findQuestionsWithPreview(Pageable pageable);

    Boolean existsQuestionById(Long id);
}
