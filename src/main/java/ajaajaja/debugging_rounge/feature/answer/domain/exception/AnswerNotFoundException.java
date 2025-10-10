package ajaajaja.debugging_rounge.feature.answer.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class AnswerNotFoundException extends BusinessException {
    public AnswerNotFoundException() {
        super(ErrorCode.ANSWER_NOT_FOUND);
    }
}
