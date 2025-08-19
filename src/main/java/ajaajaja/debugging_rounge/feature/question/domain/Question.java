package ajaajaja.debugging_rounge.feature.question.domain;

import ajaajaja.debugging_rounge.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Long userId;

    public Question(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public static Question of(String title, String content, Long userId) {
        return new Question(title, content, userId);
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}