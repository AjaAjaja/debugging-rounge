package ajaajaja.debugging_rounge.feature.question.image.application;

import ajaajaja.debugging_rounge.common.image.application.port.in.ValidateImageUrlsUseCase;
import ajaajaja.debugging_rounge.common.image.domain.exception.ImageUrlEmptyException;
import ajaajaja.debugging_rounge.common.image.domain.exception.ImageUrlInvalidFormatException;
import ajaajaja.debugging_rounge.feature.question.image.application.port.out.SaveQuestionImagePort;
import ajaajaja.debugging_rounge.feature.question.image.domain.QuestionImage;
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
@DisplayName("QuestionImageService 테스트")
class QuestionImageServiceTest {

    @Mock
    SaveQuestionImagePort saveQuestionImagePort;

    @Mock
    ValidateImageUrlsUseCase validateImageUrlsUseCase;

    @InjectMocks
    QuestionImageService questionImageService;

    @Test
    @DisplayName("null 이미지 URL 리스트는 저장하지 않는다")
    void saveQuestionImages_WithNull_ShouldNotSave() {
        // given
        Long questionId = 1L;

        // when
        questionImageService.saveQuestionImages(questionId, null);

        // then
        verify(validateImageUrlsUseCase, never()).validateImageUrls(any());
        verify(saveQuestionImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("빈 이미지 URL 리스트는 저장하지 않는다")
    void saveQuestionImages_WithEmptyList_ShouldNotSave() {
        // given
        Long questionId = 1L;
        List<String> emptyUrls = List.of();

        // when
        questionImageService.saveQuestionImages(questionId, emptyUrls);

        // then
        verify(validateImageUrlsUseCase, never()).validateImageUrls(any());
        verify(saveQuestionImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("유효한 이미지 URL들을 저장한다")
    void saveQuestionImages_WithValidUrls_ShouldSave() {
        // given
        Long questionId = 1L;
        List<String> imageUrls = List.of(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg"
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QuestionImage>> captor = ArgumentCaptor.forClass(List.class);

        // when
        questionImageService.saveQuestionImages(questionId, imageUrls);

        // then
        verify(validateImageUrlsUseCase).validateImageUrls(imageUrls);
        verify(saveQuestionImagePort).saveAll(captor.capture());

        List<QuestionImage> savedImages = captor.getValue();
        assertThat(savedImages).hasSize(3);
        assertThat(savedImages.get(0).getQuestionId()).isEqualTo(questionId);
        assertThat(savedImages.get(0).getImageUrl()).isEqualTo("https://example.com/image1.jpg");
        assertThat(savedImages.get(0).getDisplayOrder()).isEqualTo(0);
        assertThat(savedImages.get(1).getImageUrl()).isEqualTo("https://example.com/image2.jpg");
        assertThat(savedImages.get(1).getDisplayOrder()).isEqualTo(1);
        assertThat(savedImages.get(2).getImageUrl()).isEqualTo("https://example.com/image3.jpg");
        assertThat(savedImages.get(2).getDisplayOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("이미지 URL 검증이 실패하면 RuntimeException이 발생한다")
    void saveQuestionImages_WithInvalidUrls_ShouldThrowException() {
        // given
        Long questionId = 1L;
        List<String> invalidUrls = List.of("invalid-url");

        doThrow(new ImageUrlInvalidFormatException())
                .when(validateImageUrlsUseCase)
                .validateImageUrls(invalidUrls);

        // when & then
        assertThatThrownBy(() -> questionImageService.saveQuestionImages(questionId, invalidUrls))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미지 URL 검증 실패");

        verify(saveQuestionImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("이미지 URL 검증 중 예외가 발생하면 저장하지 않는다")
    void saveQuestionImages_WhenValidationFails_ShouldNotSave() {
        // given
        Long questionId = 1L;
        List<String> imageUrls = List.of("");

        doThrow(new ImageUrlEmptyException())
                .when(validateImageUrlsUseCase)
                .validateImageUrls(imageUrls);

        // when & then
        assertThatThrownBy(() -> questionImageService.saveQuestionImages(questionId, imageUrls))
                .isInstanceOf(RuntimeException.class);

        verify(saveQuestionImagePort, never()).saveAll(any());
    }

    @Test
    @DisplayName("이미지 URL의 displayOrder는 0부터 시작하여 순차적으로 증가한다")
    void saveQuestionImages_ShouldSetDisplayOrderSequentially() {
        // given
        Long questionId = 1L;
        List<String> imageUrls = List.of(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg",
                "https://example.com/image4.jpg",
                "https://example.com/image5.jpg"
        );

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QuestionImage>> captor = ArgumentCaptor.forClass(List.class);

        // when
        questionImageService.saveQuestionImages(questionId, imageUrls);

        // then
        verify(saveQuestionImagePort).saveAll(captor.capture());

        List<QuestionImage> savedImages = captor.getValue();
        assertThat(savedImages).hasSize(5);
        for (int i = 0; i < savedImages.size(); i++) {
            assertThat(savedImages.get(i).getDisplayOrder()).isEqualTo(i);
        }
    }
}

