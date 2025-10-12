package ajaajaja.debugging_rounge.feature.question.recommend.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "question_recommend",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_question_user", columnNames = {"question_id", "user_id"})
        }
)
public class QuestionRecommend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendType type;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Long userId;

}

