package ajaajaja.debugging_rounge.common.image.application.port.out;

public interface GeneratePresignedUrlPort {
    PresignedUrlResult generatePresignedUrl(String filename, String contentType);
    
    record PresignedUrlResult(String uploadUrl, String imageUrl) {}
}



