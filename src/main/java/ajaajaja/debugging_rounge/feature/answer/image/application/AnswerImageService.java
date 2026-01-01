package ajaajaja.debugging_rounge.feature.answer.image.application;

import ajaajaja.debugging_rounge.feature.answer.image.application.port.out.SaveAnswerImagePort;
import ajaajaja.debugging_rounge.feature.answer.image.domain.AnswerImage;
import ajaajaja.debugging_rounge.common.image.application.port.in.ValidateImageUrlsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerImageService {

    private final SaveAnswerImagePort saveAnswerImagePort;
    private final ValidateImageUrlsUseCase validateImageUrlsUseCase;

    @Transactional
    public void saveAnswerImages(Long answerId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        // 이미지 URL 검증
        validateImageUrlsUseCase.validateImageUrls(imageUrls);

        // AnswerImage 엔티티 생성
        List<AnswerImage> answerImages = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            answerImages.add(AnswerImage.of(answerId, imageUrls.get(i), i));
        }

        saveAnswerImagePort.saveAll(answerImages);
    }
}


