package ajaajaja.debugging_rounge.common.image.application.dto;

import java.util.List;

public record GeneratePresignedUrlsResponseDto(
        List<PresignedUrlInfo> presignedUrlInfos
) {
    public record PresignedUrlInfo(
            String filename,
            String uploadUrl,
            String imageUrl
    ) {}
}


