package ajaajaja.debugging_rounge.feature.question.domain.exception;

import ajaajaja.debugging_rounge.common.exception.BusinessException;
import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class QuestionNotFoundForDeleteException  extends BusinessException {
    public QuestionNotFoundForDeleteException() {
        super(ErrorCode.QUESTION_NOT_FOUND_FOR_DELETE);
    }
}
