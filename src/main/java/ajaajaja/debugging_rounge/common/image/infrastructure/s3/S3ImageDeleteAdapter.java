package ajaajaja.debugging_rounge.common.image.infrastructure.s3;

import ajaajaja.debugging_rounge.common.image.application.port.out.DeleteImageFromS3Port;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageDeleteAdapter implements DeleteImageFromS3Port {

    private final S3Client s3Client;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.base-url}")
    private String baseUrl;

    @Override
    @Async
    public void deleteImagesAsync(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        try {
            List<ObjectIdentifier> objectsToDelete = imageUrls.stream()
                    .filter(url -> url != null && !url.isBlank())
                    .map(this::extractFileKeyFromUrl)
                    .map(key -> ObjectIdentifier.builder().key(key).build())
                    .toList();
            
            if (objectsToDelete.isEmpty()) {
                return;
            }

            DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(builder -> builder.objects(objectsToDelete))
                    .build();
            
            s3Client.deleteObjects(deleteRequest);
            
        } catch (S3Exception e) {
        } catch (Exception e) {
        }
    }

    //이미지 URL에서 S3 파일 키를 추출하는 메서드
    private String extractFileKeyFromUrl(String imageUrl) {
        // baseUrl이 포함된 경우 제거
        if (imageUrl.startsWith(baseUrl)) {
            return imageUrl.substring(baseUrl.length() + 1); // +1은 '/' 제거
        }
        
        // 만약 전체 URL 형식이면 마지막 경로 부분만 추출
        if (imageUrl.contains("/images/")) {
            int index = imageUrl.indexOf("/images/");
            return imageUrl.substring(index + 1);
        }
        
        // 이미 파일 키 형식이면 그대로 반환
        return imageUrl;
    }
}
