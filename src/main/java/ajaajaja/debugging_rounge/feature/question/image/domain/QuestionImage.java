package ajaajaja.debugging_rounge.feature.question.image.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder;

    @Builder(access = AccessLevel.PRIVATE)
    public QuestionImage(Long questionId, String imageUrl, Integer displayOrder) {
        this.questionId = questionId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    public static QuestionImage of(Long questionId, String imageUrl, Integer displayOrder) {
        return QuestionImage.builder()
                .questionId(questionId)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .build();
    }
}


