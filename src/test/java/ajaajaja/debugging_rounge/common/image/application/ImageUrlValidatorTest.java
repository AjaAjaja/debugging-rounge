package ajaajaja.debugging_rounge.common.image.application;

import ajaajaja.debugging_rounge.common.image.domain.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("ImageUrlValidator 테스트")
class ImageUrlValidatorTest {

    private ImageUrlValidator imageUrlValidator;

    @BeforeEach
    void setUp() {
        imageUrlValidator = new ImageUrlValidator();
        // 테스트를 위해 허용 도메인 설정
        ReflectionTestUtils.setField(imageUrlValidator, "allowedDomainsString", "");
    }

    @Test
    @DisplayName("null 또는 빈 리스트는 검증을 통과한다")
    void validateImageUrls_WithNullOrEmpty_ShouldPass() {
        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(null));
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(List.of()));
    }

    @Test
    @DisplayName("유효한 HTTP URL은 검증을 통과한다")
    void validateImageUrls_WithValidHttpUrl_ShouldPass() {
        // given
        List<String> validUrls = List.of("http://example.com/image.jpg");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(validUrls));
    }

    @Test
    @DisplayName("유효한 HTTPS URL은 검증을 통과한다")
    void validateImageUrls_WithValidHttpsUrl_ShouldPass() {
        // given
        List<String> validUrls = List.of("https://example.com/image.jpg");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(validUrls));
    }

    @Test
    @DisplayName("여러 개의 유효한 URL은 검증을 통과한다")
    void validateImageUrls_WithMultipleValidUrls_ShouldPass() {
        // given
        List<String> validUrls = List.of(
                "https://example.com/image1.jpg",
                "https://example.com/image2.png",
                "http://example.com/image3.gif"
        );

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(validUrls));
    }

    @Test
    @DisplayName("이미지 URL 개수가 10개를 초과하면 예외가 발생한다")
    void validateImageUrls_WithExceededCount_ShouldThrowException() {
        // given
        List<String> tooManyUrls = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            tooManyUrls.add("https://example.com/image" + i + ".jpg");
        }

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(tooManyUrls))
                .isInstanceOf(ImageUrlCountExceededException.class);
    }

    @Test
    @DisplayName("빈 문자열 URL은 예외가 발생한다")
    void validateImageUrls_WithEmptyString_ShouldThrowException() {
        // given
        List<String> emptyUrls = List.of("");

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(emptyUrls))
                .isInstanceOf(ImageUrlEmptyException.class);
    }

    @Test
    @DisplayName("null URL은 예외가 발생한다")
    void validateImageUrls_WithNullUrl_ShouldThrowException() {
        // given
        List<String> nullUrls = new ArrayList<>();
        nullUrls.add(null);

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(nullUrls))
                .isInstanceOf(ImageUrlEmptyException.class);
    }

    @Test
    @DisplayName("공백만 있는 URL은 예외가 발생한다")
    void validateImageUrls_WithBlankUrl_ShouldThrowException() {
        // given
        List<String> blankUrls = List.of("   ");

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(blankUrls))
                .isInstanceOf(ImageUrlEmptyException.class);
    }

    @Test
    @DisplayName("URL 길이가 500자를 초과하면 예외가 발생한다")
    void validateImageUrls_WithTooLongUrl_ShouldThrowException() {
        // given
        String longUrl = "https://example.com/" + "a".repeat(500);
        List<String> longUrls = List.of(longUrl);

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(longUrls))
                .isInstanceOf(ImageUrlTooLongException.class);
    }

    @Test
    @DisplayName("잘못된 URL 형식은 예외가 발생한다")
    void validateImageUrls_WithInvalidFormat_ShouldThrowException() {
        // given
        List<String> invalidUrls = List.of("not-a-url");

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(invalidUrls))
                .isInstanceOf(ImageUrlInvalidFormatException.class);
    }

    @Test
    @DisplayName("프로토콜이 없는 URL은 예외가 발생한다")
    void validateImageUrls_WithoutProtocol_ShouldThrowException() {
        // given
        List<String> invalidUrls = List.of("example.com/image.jpg");

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(invalidUrls))
                .isInstanceOf(ImageUrlInvalidFormatException.class);
    }

    @Test
    @DisplayName("지원하지 않는 프로토콜은 예외가 발생한다")
    void validateImageUrls_WithUnsupportedProtocol_ShouldThrowException() {
        // given
        // ftp는 정규표현식 검증에서 먼저 실패하므로 ImageUrlInvalidFormatException이 발생
        List<String> invalidUrls = List.of("ftp://example.com/image.jpg");

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(invalidUrls))
                .isInstanceOf(ImageUrlInvalidFormatException.class);
    }

    @Test
    @DisplayName("허용되지 않은 도메인은 예외가 발생한다")
    void validateImageUrls_WithNotAllowedDomain_ShouldThrowException() {
        // given
        ReflectionTestUtils.setField(imageUrlValidator, "allowedDomainsString", "allowed-domain.com");
        List<String> notAllowedUrls = List.of("https://not-allowed-domain.com/image.jpg");

        // when & then
        assertThatThrownBy(() -> imageUrlValidator.validateImageUrls(notAllowedUrls))
                .isInstanceOf(ImageUrlDomainNotAllowedException.class);
    }

    @Test
    @DisplayName("허용된 도메인은 검증을 통과한다")
    void validateImageUrls_WithAllowedDomain_ShouldPass() {
        // given
        ReflectionTestUtils.setField(imageUrlValidator, "allowedDomainsString", "allowed-domain.com");
        List<String> allowedUrls = List.of("https://allowed-domain.com/image.jpg");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(allowedUrls));
    }

    @Test
    @DisplayName("허용된 도메인의 서브도메인은 검증을 통과한다")
    void validateImageUrls_WithAllowedSubDomain_ShouldPass() {
        // given
        ReflectionTestUtils.setField(imageUrlValidator, "allowedDomainsString", "allowed-domain.com");
        List<String> allowedUrls = List.of("https://sub.allowed-domain.com/image.jpg");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(allowedUrls));
    }

    @Test
    @DisplayName("여러 허용 도메인 중 하나와 일치하면 검증을 통과한다")
    void validateImageUrls_WithMultipleAllowedDomains_ShouldPass() {
        // given
        ReflectionTestUtils.setField(imageUrlValidator, "allowedDomainsString", "domain1.com,domain2.com,domain3.com");
        List<String> allowedUrls = List.of("https://domain2.com/image.jpg");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(allowedUrls));
    }

    @Test
    @DisplayName("복잡한 경로를 가진 URL은 검증을 통과한다")
    void validateImageUrls_WithComplexPath_ShouldPass() {
        // given
        List<String> complexUrls = List.of(
                "https://example.com/path/to/image.jpg",
                "https://example.com/path/to/image.jpg?width=100&height=200",
                "https://example.com/path/to/image.jpg#fragment"
        );

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(complexUrls));
    }

    @Test
    @DisplayName("쿼리 파라미터가 있는 URL은 검증을 통과한다")
    void validateImageUrls_WithQueryParameters_ShouldPass() {
        // given
        List<String> urlsWithQuery = List.of("https://example.com/image.jpg?size=large&format=png");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(urlsWithQuery));
    }

    @Test
    @DisplayName("포트 번호가 있는 URL은 검증을 통과한다")
    void validateImageUrls_WithPort_ShouldPass() {
        // given
        List<String> urlsWithPort = List.of("https://example.com:8080/image.jpg");

        // when & then
        assertDoesNotThrow(() -> imageUrlValidator.validateImageUrls(urlsWithPort));
    }
}

