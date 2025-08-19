package ajaajaja.debugging_rounge.feature.question.domain;


import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionDetailResponseDto;
import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT new ajaajaja.debugging_rounge.feature.question.api.dto." +
            "QuestionDetailResponseDto(q.id, q.title, q.content, u.email, u.id) " +
            "FROM Question q " +
            "JOIN User u " +
            "ON q.userId = u.id " +
            "WHERE q.id = :questionId")
    Optional<QuestionDetailResponseDto> findQuestionDetailById(@Param("questionId") Long questionId);

    @Query("SELECT new ajaajaja.debugging_rounge.feature.question.api.dto." +
            "QuestionListResponseDto(q.id, q.title, SUBSTRING(q.content, 1, 100), u.email) " +
            "FROM Question q JOIN User u ON q.userId = u.id")
    Page<QuestionListResponseDto> findQuestionsWithPreview(Pageable pageable);
}
