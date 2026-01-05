package ajaajaja.debugging_rounge.common.image.infrastructure.event;

import ajaajaja.debugging_rounge.common.image.application.port.out.DeleteImageFromS3Port;
import ajaajaja.debugging_rounge.common.image.domain.event.ImageDeleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ImageDeleteEventListener {

    private final DeleteImageFromS3Port deleteImageFromS3Port;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleImageDeleteEvent(ImageDeleteEvent event) {
        deleteImageFromS3Port.deleteImagesAsync(event.imageUrls());
    }
}

