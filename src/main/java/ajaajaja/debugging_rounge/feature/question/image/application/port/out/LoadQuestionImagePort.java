package ajaajaja.debugging_rounge.feature.question.image.application.port.out;

import java.util.List;

public interface LoadQuestionImagePort {
    List<String> findImageUrlsByQuestionId(Long questionId);
}


