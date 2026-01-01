package ajaajaja.debugging_rounge.feature.common.image.infrastructure.s3;

import ajaajaja.debugging_rounge.feature.common.image.application.port.out.GeneratePresignedUrlPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Component
public class S3PresignedUrlAdapter implements GeneratePresignedUrlPort {

    private final S3Presigner s3Presigner;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.base-url}")
    private String baseUrl;

    public S3PresignedUrlAdapter(S3Presigner s3Presigner) {
        this.s3Presigner = s3Presigner;
    }

    @Override
    public PresignedUrlResult generatePresignedUrl(String filename, String contentType) {
        // 고유한 파일 키 생성 (UUID + 확장자)
        String fileKey = generateFileKey(filename, contentType);
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .contentType(contentType)
                .build();

        // Presigned URL 생성 (15분 유효)
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String uploadUrl = presignedRequest.url().toString();
        
        // 이미지 접근 URL 생성
        String imageUrl = baseUrl + "/" + fileKey;
        
        return new PresignedUrlResult(uploadUrl, imageUrl);
    }

    // 고유한 파일 키 생성 (UUID + 확장자)
    private String generateFileKey(String originalFilename, String contentType) {
        String extension = getFileExtension(originalFilename, contentType);
        String uuid = UUID.randomUUID().toString();
        return "images/" + uuid + "." + extension;
    }

    // 파일 확장자 추출: 1) 파일명에서 추출 시도, 2) contentType에서 추출, 3) 기본값
    private String getFileExtension(String filename, String contentType) {
        // 파일명에 확장자가 있으면 우선 사용
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < filename.length() - 1) {
            String extension = filename.substring(lastDotIndex + 1).toLowerCase();
            if (isValidImageExtension(extension)) {
                return extension;
            }
        }
        
        // 2. 파일명에 확장자가 없거나 유효하지 않으면 contentType에서 추출
        return getExtensionFromContentType(contentType);
    }
    
    // 허용된 이미지 확장자 검증
    private boolean isValidImageExtension(String extension) {
        return switch (extension) {
            case "jpg", "jpeg", "png", "gif", "webp", "svg", "bmp" -> true;
            default -> false;
        };
    }
    
    // contentType에서 확장자 매핑 (예: "image/png" -> "png")
    private String getExtensionFromContentType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg" -> "jpeg";
            case "image/jpg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "image/webp" -> "webp";
            case "image/svg+xml" -> "svg";
            case "image/bmp" -> "bmp";
            default -> "jpg";
        };
    }
}


