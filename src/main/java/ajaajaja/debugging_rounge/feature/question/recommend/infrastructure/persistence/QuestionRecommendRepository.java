package ajaajaja.debugging_rounge.feature.question.recommend.infrastructure.persistence;

import ajaajaja.debugging_rounge.feature.question.recommend.domain.QuestionRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRecommendRepository extends JpaRepository<QuestionRecommend, Long> {
}
