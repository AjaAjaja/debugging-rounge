package ajaajaja.debugging_rounge.feature.answer.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = Length.LONG)
    private String content;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Long authorId;

    @Builder(access = AccessLevel.PRIVATE)
    public Answer(String content, Long questionId, Long authorId) {
        this.content = content;
        this.questionId = questionId;
        this.authorId = authorId;
    }

    public static Answer of(String content, Long questionId, Long authorId) {
        return Answer.builder()
                .content(content)
                .questionId(questionId)
                .authorId(authorId)
                .build();
    }

}
