package ajaajaja.debugging_rounge.feature.answer.recommend.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "answer_recommend",
        uniqueConstraints = @UniqueConstraint(name = "uk_answer_user", columnNames = {"answer_id", "user_id"})
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerRecommend extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendType type;

    @Column(nullable = false)
    private Long answerId;

    @Column(nullable = false)
    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    public AnswerRecommend(RecommendType recommendType, Long answerId, Long userId) {
        this.type = recommendType;
        this.answerId = answerId;
        this.userId = userId;
    }

    public static AnswerRecommend of(RecommendType recommendType, Long answerId, Long userId) {
        return new AnswerRecommend(recommendType, answerId, userId);
    }

}
