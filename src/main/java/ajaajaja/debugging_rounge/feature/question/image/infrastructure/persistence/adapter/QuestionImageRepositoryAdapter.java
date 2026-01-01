package ajaajaja.debugging_rounge.feature.question.image.infrastructure.persistence.adapter;

import ajaajaja.debugging_rounge.feature.question.image.application.port.out.LoadQuestionImagePort;
import ajaajaja.debugging_rounge.feature.question.image.application.port.out.SaveQuestionImagePort;
import ajaajaja.debugging_rounge.feature.question.image.domain.QuestionImage;
import ajaajaja.debugging_rounge.feature.question.image.infrastructure.persistence.QuestionImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QuestionImageRepositoryAdapter implements SaveQuestionImagePort, LoadQuestionImagePort {

    private final QuestionImageJpaRepository questionImageJpaRepository;

    @Override
    public void saveAll(List<QuestionImage> questionImages) {
        questionImageJpaRepository.saveAll(questionImages);
    }

    @Override
    public List<String> findImageUrlsByQuestionId(Long questionId) {
        return questionImageJpaRepository.findByQuestionIdOrderByDisplayOrderAsc(questionId)
                .stream()
                .map(QuestionImage::getImageUrl)
                .toList();
    }
}


