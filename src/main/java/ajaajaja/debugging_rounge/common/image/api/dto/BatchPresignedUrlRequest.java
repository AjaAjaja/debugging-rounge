package ajaajaja.debugging_rounge.common.image.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Presigned URL 생성 요청 (배치)")
public record BatchPresignedUrlRequest(
        @Schema(description = "업로드할 파일 정보 목록")
        @NotEmpty(message = "최소 1개의 파일 정보가 필요합니다.")
        @Size(max = 10, message = "한 번에 최대 10개까지 업로드할 수 있습니다.")
        @Valid
        List<FileInfo> files
) {
    @Schema(description = "파일 정보")
    public record FileInfo(
            @Schema(description = "파일명", example = "image.jpg")
            @NotBlank(message = "파일명은 필수입니다.")
            String filename,
            
            @Schema(description = "파일 콘텐츠 타입", example = "image/jpeg")
            @NotBlank(message = "콘텐츠 타입은 필수입니다.")
            @Pattern(regexp = "^image/.*", message = "이미지 타입만 허용됩니다.")
            String contentType
    ) {}
}


