package ajaajaja.debugging_rounge.common.image.application;

import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsRequestDto;
import ajaajaja.debugging_rounge.common.image.application.dto.GeneratePresignedUrlsResponseDto;
import ajaajaja.debugging_rounge.common.image.application.port.in.GeneratePresignedUrlUseCase;
import ajaajaja.debugging_rounge.common.image.application.port.out.GeneratePresignedUrlPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageFacade implements GeneratePresignedUrlUseCase {

    private final GeneratePresignedUrlPort generatePresignedUrlPort;

    @Override
    public GeneratePresignedUrlsResponseDto generatePresignedUrls(GeneratePresignedUrlsRequestDto requestDto) {
        List<GeneratePresignedUrlsResponseDto.PresignedUrlInfo> results = requestDto.files().stream()
                .map(fileInfo -> {
                    GeneratePresignedUrlPort.PresignedUrlResult result =
                            generatePresignedUrlPort.generatePresignedUrl(
                                    fileInfo.filename(),
                                    fileInfo.contentType()
                            );
                    
                    return new GeneratePresignedUrlsResponseDto.PresignedUrlInfo(
                            fileInfo.filename(),
                            result.uploadUrl(),
                            result.imageUrl()
                    );
                })
                .toList();
        
        return new GeneratePresignedUrlsResponseDto(results);
    }
}


