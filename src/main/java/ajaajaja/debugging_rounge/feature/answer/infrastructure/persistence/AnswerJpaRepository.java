package ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.projection.AnswerDetailView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
    @Query(
            value = """
            SELECT a.id AS id,
                   a.content AS content,
                   a.authorId AS authorId,
                   u.email AS authorEmail
            FROM Answer a
            JOIN User u
            ON a.authorId = u.id
            WHERE a.questionId = :questionId
            """,
            countQuery = """
            SELECT COUNT(a.id)
            FROM Answer a
            WHERE a.questionId = :questionId
            """
    )
    Page<AnswerDetailView> findAllByQuestionId(@Param("questionId") Long questionId, Pageable pageable);

    void deleteAllByQuestionId(Long questionId);

}
