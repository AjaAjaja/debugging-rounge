package ajaajaja.debugging_rounge.feature.question.image.application.port.out;

import ajaajaja.debugging_rounge.feature.question.image.domain.QuestionImage;

import java.util.List;

public interface SaveQuestionImagePort {
    void saveAll(List<QuestionImage> questionImages);
}


