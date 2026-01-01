package ajaajaja.debugging_rounge.feature.question.image.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.question.image.domain.QuestionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionImageJpaRepository extends JpaRepository<QuestionImage, Long> {
    List<QuestionImage> findByQuestionIdOrderByDisplayOrderAsc(Long questionId);
}


