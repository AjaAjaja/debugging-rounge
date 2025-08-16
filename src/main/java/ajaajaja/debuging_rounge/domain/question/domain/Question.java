package ajaajaja.debuging_rounge.domain.question.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    public Question(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Question of(String title, String content) {
        return new Question(title, content);
    }
}
