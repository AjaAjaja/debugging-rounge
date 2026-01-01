package ajaajaja.debugging_rounge.feature.answer.image.application;

import ajaajaja.debugging_rounge.common.image.application.port.in.ValidateImageUrlsUseCase;
import ajaajaja.debugging_rounge.common.image.domain.exception.ImageUrlEmptyException;
import ajaajaja.debugging_rounge.common.image.domain.exception.ImageUrlInvalidFormatException;
import ajaajaja.debugging_rounge.feature.answer.image.application.port.out.SaveAnswerImagePort;
import ajaajaja.debugging_rounge.feature.answer.image.domain.AnswerImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerImageService 테스트")
class AnswerImageServiceTest {

    @Mock
    SaveAnswerImagePort saveAnswerImagePort;

    @Mock
    ValidateImageUrlsUseCase validateImageUrlsUseCase;

    @InjectMocks
    AnswerImageService answerImageService;

    @Test
    @DisplayName("null 이미지 URL 리스트는 저장하지 않는다")
    void saveAnswerImages_WithNull_ShouldNotSave() {
        // given
        Long answerId = 1L;

        // when
        answerImageService.saveAnswerImages(answerId, null);

        // then
        verify(validateImageUrlsUseCase, never()).validateImageUrls(any());
        verify(saveAnswerImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("빈 이미지 URL 리스트는 저장하지 않는다")
    void saveAnswerImages_WithEmptyList_ShouldNotSave() {
        // given
        Long answerId = 1L;
        List<String> emptyUrls = List.of();

        // when
        answerImageService.saveAnswerImages(answerId, emptyUrls);

        // then
        verify(validateImageUrlsUseCase, never()).validateImageUrls(any());
        verify(saveAnswerImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("유효한 이미지 URL들을 저장한다")
    void saveAnswerImages_WithValidUrls_ShouldSave() {
        // given
        Long answerId = 1L;
        List<String> imageUrls = List.of(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg"
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerImage>> captor = ArgumentCaptor.forClass(List.class);

        // when
        answerImageService.saveAnswerImages(answerId, imageUrls);

        // then
        verify(validateImageUrlsUseCase).validateImageUrls(imageUrls);
        verify(saveAnswerImagePort).saveAll(captor.capture());

        List<AnswerImage> savedImages = captor.getValue();
        assertThat(savedImages).hasSize(3);
        assertThat(savedImages.get(0).getAnswerId()).isEqualTo(answerId);
        assertThat(savedImages.get(0).getImageUrl()).isEqualTo("https://example.com/image1.jpg");
        assertThat(savedImages.get(0).getDisplayOrder()).isEqualTo(0);
        assertThat(savedImages.get(1).getImageUrl()).isEqualTo("https://example.com/image2.jpg");
        assertThat(savedImages.get(1).getDisplayOrder()).isEqualTo(1);
        assertThat(savedImages.get(2).getImageUrl()).isEqualTo("https://example.com/image3.jpg");
        assertThat(savedImages.get(2).getDisplayOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미지 URL 검증이 실패하면 예외가 발생한다")
    void saveAnswerImages_WithInvalidUrls_ShouldThrowException() {
        // given
        Long answerId = 1L;
        List<String> invalidUrls = List.of("invalid-url");

        doThrow(new ImageUrlInvalidFormatException())
                .when(validateImageUrlsUseCase)
                .validateImageUrls(invalidUrls);

        // when & then
        assertThatThrownBy(() -> answerImageService.saveAnswerImages(answerId, invalidUrls))
                .isInstanceOf(ImageUrlInvalidFormatException.class);

        verify(saveAnswerImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("이미지 URL 검증 중 예외가 발생하면 저장하지 않는다")
    void saveAnswerImages_WhenValidationFails_ShouldNotSave() {
        // given
        Long answerId = 1L;
        List<String> imageUrls = List.of("");

        doThrow(new ImageUrlEmptyException())
                .when(validateImageUrlsUseCase)
                .validateImageUrls(imageUrls);

        // when & then
        assertThatThrownBy(() -> answerImageService.saveAnswerImages(answerId, imageUrls))
                .isInstanceOf(ImageUrlEmptyException.class);

        verify(saveAnswerImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("이미지 URL의 displayOrder는 0부터 시작하여 순차적으로 증가한다")
    void saveAnswerImages_ShouldSetDisplayOrderSequentially() {
        // given
        Long answerId = 1L;
        List<String> imageUrls = List.of(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg",
                "https://example.com/image4.jpg",
                "https://example.com/image5.jpg"
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AnswerImage>> captor = ArgumentCaptor.forClass(List.class);

        // when
        answerImageService.saveAnswerImages(answerId, imageUrls);

        // then
        verify(saveAnswerImagePort).saveAll(captor.capture());

        List<AnswerImage> savedImages = captor.getValue();
        assertThat(savedImages).hasSize(5);
        for (int i = 0; i < savedImages.size(); i++) {
            assertThat(savedImages.get(i).getDisplayOrder()).isEqualTo(i);
        }
    }
}

