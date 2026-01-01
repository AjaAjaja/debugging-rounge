package ajaajaja.debugging_rounge.feature.common.image.application.port.in.dto;

import java.util.List;

public record GeneratePresignedUrlsCommand(
        List<FileInfo> files
) {
    public record FileInfo(
            String filename,
            String contentType
    ) {}
}

