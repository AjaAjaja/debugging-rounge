package ajaajaja.debugging_rounge.common.image.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Presigned URL 생성 응답 (배치)")
public record BatchPresignedUrlResponse(
        @Schema(description = "생성된 Presigned URL 목록")
        List<PresignedUrlResult> results
) {
    @Schema(description = "Presigned URL 결과")
    public record PresignedUrlResult(
            @Schema(description = "원본 파일명", example = "image.jpg")
            String filename,
            
            @Schema(description = "이미지 업로드용 Presigned URL", example = "https://s3.amazonaws.com/...")
            String uploadUrl,
            
            @Schema(description = "업로드된 이미지 접근 URL", example = "https://debugging-rounge-bucket.s3.ap-northeast-2.amazonaws.com/images/uuid.jpg")
            String imageUrl
    ) {}
}


