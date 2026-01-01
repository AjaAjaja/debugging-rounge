package ajaajaja.debugging_rounge.feature.common.image.application.port.in.dto;

import java.util.List;

public record GeneratePresignedUrlsResult(
        List<PresignedUrlInfo> results
) {
    public record PresignedUrlInfo(
            String filename,
            String uploadUrl,
            String imageUrl
    ) {}
}

