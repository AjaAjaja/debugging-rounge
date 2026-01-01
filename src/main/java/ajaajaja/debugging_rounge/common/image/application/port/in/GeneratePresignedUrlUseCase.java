package ajaajaja.debugging_rounge.common.image.application.port.in;

import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsRequestDto;
import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsResponseDto;

public interface GeneratePresignedUrlUseCase {
    GeneratePresignedUrlsResponseDto generatePresignedUrls(GeneratePresignedUrlsRequestDto requestDto);
}



