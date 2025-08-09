package ajaajaja.debuging_rounge.domain.question.entity;

import ajaajaja.debuging_rounge.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String title;

    private String content;

    public Question(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Question of(String title, String content) {
        return new Question(title, content);
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}