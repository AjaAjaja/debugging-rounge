package ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.application.port.out.SaveAnswerPort;
import ajaajaja.debugging_rounge.feature.answer.domain.Answer;
import ajaajaja.debugging_rounge.feature.answer.infrastructure.persistence.AnswerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerRepositoryAdapter implements SaveAnswerPort {

    private final AnswerJpaRepository answerJpaRepository;
    @Override
    public Answer save(Answer answer) {
        return answerJpaRepository.save(answer);
    }
}
