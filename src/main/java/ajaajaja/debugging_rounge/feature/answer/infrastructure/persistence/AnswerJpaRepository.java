package ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerJpaRepository extends JpaRepository<Answer, Long> {
}
