package ajaajaja.debuging_rounge.domain.question.exception;

import ajaajaja.debuging_rounge.global.exception.BusinessException;
import ajaajaja.debuging_rounge.global.exception.ErrorCode;

public class QuestionNotFoundForDeleteException  extends BusinessException {
    public QuestionNotFoundForDeleteException() {
        super(ErrorCode.QUESTION_NOT_FOUND_FOR_DELETE);
    }
}
