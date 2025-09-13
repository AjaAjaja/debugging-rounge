package ajaajaja.debugging_rounge.feature.answer.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Long userId;

    @Builder(access = AccessLevel.PRIVATE)
    public Answer(String content, Long questionId, Long userId) {
        this.content = content;
        this.questionId = questionId;
        this.userId = userId;
    }

    public static Answer of(String content, Long questionId, Long userId) {
        return Answer.builder()
                .content(content)
                .questionId(questionId)
                .userId(userId)
                .build();
    }

}
