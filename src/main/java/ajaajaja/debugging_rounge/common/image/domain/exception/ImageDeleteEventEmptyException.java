package ajaajaja.debugging_rounge.common.image.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class ImageDeleteEventEmptyException extends BusinessException {
    public ImageDeleteEventEmptyException() {
        super(ErrorCode.IMAGE_DELETE_EVENT_EMPTY);
    }
}

