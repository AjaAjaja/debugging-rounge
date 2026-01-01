package ajaajaja.debugging_rounge.feature.question.image.application;

import ajaajaja.debugging_rounge.common.image.application.port.in.ValidateImageUrlsUseCase;
import ajaajaja.debugging_rounge.feature.question.image.application.port.out.SaveQuestionImagePort;
import ajaajaja.debugging_rounge.feature.question.image.domain.QuestionImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionImageService {

    private final SaveQuestionImagePort saveQuestionImagePort;
    private final ValidateImageUrlsUseCase validateImageUrlsUseCase;

    @Transactional
    public void saveQuestionImages(Long questionId, List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        // 이미지 URL 검증
        try {
            validateImageUrlsUseCase.validateImageUrls(imageUrls);
        } catch (Exception e) {
            // 검증 실패 시 로그 출력
            throw new RuntimeException("이미지 URL 검증 실패: " + imageUrls + ", 오류: " + e.getMessage(), e);
        }

        // QuestionImage 엔티티 생성
        List<QuestionImage> questionImages = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            questionImages.add(QuestionImage.of(questionId, imageUrls.get(i), i));
        }

        saveQuestionImagePort.saveAll(questionImages);
    }
}


