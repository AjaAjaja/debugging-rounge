package ajaajaja.debuging_rounge.domain.question.repository;

import ajaajaja.debuging_rounge.domain.question.dto.QuestionListResponseDto;
import ajaajaja.debuging_rounge.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT new ajaajaja.debuging_rounge.domain.question.dto.QuestionListResponseDto(q.title, SUBSTRING(q.content, 1, 200)) FROM Question q")
    List<QuestionListResponseDto> findAllWithPreview();
}
