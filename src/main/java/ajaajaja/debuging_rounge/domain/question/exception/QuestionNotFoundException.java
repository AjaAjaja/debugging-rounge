package ajaajaja.debuging_rounge.domain.question.exception;

import ajaajaja.debuging_rounge.global.exception.ErrorCode;
import ajaajaja.debuging_rounge.global.exception.BusinessException;

public class QuestionNotFoundException extends BusinessException {

    public QuestionNotFoundException() {
        super(ErrorCode.QUESTION_NOT_FOUND);
    }
}
