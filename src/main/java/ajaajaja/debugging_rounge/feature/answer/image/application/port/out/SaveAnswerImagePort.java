package ajaajaja.debugging_rounge.feature.answer.image.application.port.out;

import ajaajaja.debugging_rounge.feature.answer.image.domain.AnswerImage;

import java.util.List;

public interface SaveAnswerImagePort {
    void saveAll(List<AnswerImage> answerImages);
}


