package ajaajaja.debugging_rounge.common.image.api;

import ajaajaja.debugging_rounge.common.image.api.dto.BatchPresignedUrlRequest;
import ajaajaja.debugging_rounge.common.image.api.dto.BatchPresignedUrlResponse;
import ajaajaja.debugging_rounge.common.image.api.mapper.ImageMapper;
import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsRequestDto;
import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsResponseDto;
import ajaajaja.debugging_rounge.common.image.application.port.in.GeneratePresignedUrlUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Image", description = "이미지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {

    private final GeneratePresignedUrlUseCase generatePresignedUrlUseCase;
    private final ImageMapper imageMapper;

    @Operation(
            summary = "Presigned URL 생성 (배치)",
            description = "S3에 이미지를 업로드하기 위한 Presigned URL을 생성합니다. 한 번에 최대 10개까지 처리 가능합니다.",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presigned URL 생성 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    @PostMapping("/presigned-urls")
    public ResponseEntity<BatchPresignedUrlResponse> generatePresignedUrls(
            @RequestBody @Valid BatchPresignedUrlRequest request
    ) {
        GeneratePresignedUrlsRequestDto requestDto = imageMapper.toRequestDto(request);
        GeneratePresignedUrlsResponseDto responseDto = generatePresignedUrlUseCase.generatePresignedUrls(requestDto);
        BatchPresignedUrlResponse response = imageMapper.toResponse(responseDto);
        
        return ResponseEntity.ok(response);
    }
}


