package ajaajaja.debugging_rounge.feature.question.application.port.out;

import ajaajaja.debugging_rounge.feature.question.domain.Question;

public interface SaveQuestionPort {
    Question save(Question question);
}
