package ajaajaja.debugging_rounge.feature.answer.image.application.port.out;

import java.util.List;

public interface LoadAnswerImagePort {
    List<String> findImageUrlsByAnswerId(Long answerId);
}


