package ajaajaja.debugging_rounge.common.image.application;

import ajaajaja.debugging_rounge.common.image.application.port.in.ValidateImageUrlsUseCase;
import ajaajaja.debugging_rounge.common.image.domain.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ImageUrlValidator implements ValidateImageUrlsUseCase {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]",
            Pattern.CASE_INSENSITIVE
    );

    private static final int MAX_IMAGE_URLS = 10;
    private static final long MAX_URL_LENGTH = 500;

    @Value("${aws.image.allowed-domains:}")
    private String allowedDomainsString;

    private List<String> getAllowedDomains() {
        if (allowedDomainsString.isBlank()) {
            return List.of();
        }
        return List.of(allowedDomainsString.split(","))
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @Override
    public void validateImageUrls(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        if (imageUrls.size() > MAX_IMAGE_URLS) {
            throw new ImageUrlCountExceededException();
        }

        for (String imageUrl : imageUrls) {
            validateSingleUrl(imageUrl);
        }
    }

    private void validateSingleUrl(String imageUrl) {
        // null 체크
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new ImageUrlEmptyException();
        }

        // 길이 체크
        if (imageUrl.length() > MAX_URL_LENGTH) {
            throw new ImageUrlTooLongException();
        }

        // URL 형식 체크 (정규표현식으로 http:// 또는 https:// 형식 검증)
        if (!URL_PATTERN.matcher(imageUrl).matches()) {
            throw new ImageUrlInvalidFormatException();
        }

        try {
            URL url = new URL(imageUrl);
            String host = url.getHost();

            // 도메인 검증 (설정된 경우)
            List<String> allowedDomains = getAllowedDomains();
            if (!allowedDomains.isEmpty()) {
                boolean isAllowed = allowedDomains.stream()
                        .anyMatch(domain -> host.equals(domain) || host.endsWith("." + domain));
                if (!isAllowed) {
                    throw new ImageUrlDomainNotAllowedException();
                }
            }

            // 프로토콜 체크 (HTTP/HTTPS만 허용)
            String protocol = url.getProtocol();
            if (!protocol.equals("http") && !protocol.equals("https")) {
                throw new ImageUrlUnsupportedProtocolException();
            }

        } catch (MalformedURLException e) {
            throw new ImageUrlInvalidFormatException();
        }
    }
}


