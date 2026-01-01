package ajaajaja.debugging_rounge.feature.answer.image.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.answer.image.application.port.out.LoadAnswerImagePort;
import ajaajaja.debugging_rounge.feature.answer.image.application.port.out.SaveAnswerImagePort;
import ajaajaja.debugging_rounge.feature.answer.image.domain.AnswerImage;
import ajaajaja.debugging_rounge.feature.answer.image.infrastructure.persistence.AnswerImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerImageRepositoryAdapter implements SaveAnswerImagePort, LoadAnswerImagePort {

    private final AnswerImageJpaRepository answerImageJpaRepository;

    @Override
    public void saveAll(List<AnswerImage> answerImages) {
        answerImageJpaRepository.saveAll(answerImages);
    }

    @Override
    public List<String> findImageUrlsByAnswerId(Long answerId) {
        return answerImageJpaRepository.findByAnswerIdOrderByDisplayOrderAsc(answerId)
                .stream()
                .map(AnswerImage::getImageUrl)
                .toList();
    }
}


