package ajaajaja.debugging_rounge.common.image.application.dto;

import java.util.List;

public record GeneratePresignedUrlsRequestDto(
        List<FileInfo> files
) {
    public record FileInfo(
            String filename,
            String contentType
    ) {}
}


