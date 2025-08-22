package ajaajaja.debugging_rounge.common.exception.auth;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;

public class QuestionUpdateForbiddenExceptionCustom extends CustomAuthorizationException {
    public QuestionUpdateForbiddenExceptionCustom() {
        super(ErrorCode.QUESTION_UPDATE_FORBIDDEN);
    }
}
