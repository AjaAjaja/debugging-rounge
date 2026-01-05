package ajaajaja.debugging_rounge.common.image.domain.event;

import ajaajaja.debugging_rounge.common.image.domain.exception.ImageDeleteEventEmptyException;

import java.util.List;

public record ImageDeleteEvent(List<String> imageUrls) {
    
    public ImageDeleteEvent {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new ImageDeleteEventEmptyException();
        }
    }
    
    public static ImageDeleteEvent of(List<String> imageUrls) {
        return new ImageDeleteEvent(imageUrls);
    }
}

