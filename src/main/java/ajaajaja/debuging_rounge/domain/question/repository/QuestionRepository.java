package ajaajaja.debuging_rounge.domain.question.repository;

import ajaajaja.debuging_rounge.domain.question.dto.QuestionListResponseDto;
import ajaajaja.debuging_rounge.domain.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT new ajaajaja.debuging_rounge.domain.question.dto.QuestionListResponseDto(q.title, SUBSTRING(q.content, 1, 100)) FROM Question q")
    Page<QuestionListResponseDto> findQuestionsWithPreview(Pageable pageable);
}
