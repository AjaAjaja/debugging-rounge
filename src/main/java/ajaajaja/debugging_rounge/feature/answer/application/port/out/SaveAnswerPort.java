package ajaajaja.debugging_rounge.feature.answer.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.domain.Answer;

public interface SaveAnswerPort {
    Answer save(Answer answer);
}
