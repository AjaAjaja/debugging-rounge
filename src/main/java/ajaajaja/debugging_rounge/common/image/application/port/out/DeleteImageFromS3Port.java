package ajaajaja.debugging_rounge.common.image.application.port.out;

import java.util.List;

public interface DeleteImageFromS3Port {

    void deleteImagesAsync(List<String> imageUrls);
}

