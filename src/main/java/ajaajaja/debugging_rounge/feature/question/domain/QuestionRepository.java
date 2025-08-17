package ajaajaja.debugging_rounge.feature.question.domain;


import ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT new ajaajaja.debugging_rounge.feature.question.api.dto.QuestionListResponseDto(q.id, q.title, SUBSTRING(q.content, 1, 100)) FROM Question q")
    Page<QuestionListResponseDto> findQuestionsWithPreview(Pageable pageable);
}
