package ajaajaja.debugging_rounge.common.jwt.exception;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class QuestionUpdateForbiddenExceptionCustom extends CustomAuthorizationException {
    public QuestionUpdateForbiddenExceptionCustom() {
        super(ErrorCode.QUESTION_UPDATE_FORBIDDEN);
    }
}
