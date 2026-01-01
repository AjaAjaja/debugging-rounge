package ajaajaja.debugging_rounge.feature.answer.image.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long answerId;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @Column(nullable = false)
    private Integer displayOrder;

    @Builder(access = AccessLevel.PRIVATE)
    public AnswerImage(Long answerId, String imageUrl, Integer displayOrder) {
        this.answerId = answerId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    public static AnswerImage of(Long answerId, String imageUrl, Integer displayOrder) {
        return AnswerImage.builder()
                .answerId(answerId)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .build();
    }
}


