package ajaajaja.debuging_rounge.domain.question.repository;

import ajaajaja.debuging_rounge.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

}
