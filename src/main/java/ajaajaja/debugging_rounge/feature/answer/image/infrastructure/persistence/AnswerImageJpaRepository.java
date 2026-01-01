package ajaajaja.debugging_rounge.feature.answer.image.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.answer.image.domain.AnswerImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerImageJpaRepository extends JpaRepository<AnswerImage, Long> {
    List<AnswerImage> findByAnswerIdOrderByDisplayOrderAsc(Long answerId);
}


