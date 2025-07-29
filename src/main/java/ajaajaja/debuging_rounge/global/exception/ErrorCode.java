package ajaajaja.debuging_rounge.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST("error.invalid_request",HttpStatus.BAD_REQUEST),
    NOT_FOUND("error.not_found", HttpStatus.NOT_FOUND),
    INTERNAL_SERVER_ERROR("error.internal_server", HttpStatus.INTERNAL_SERVER_ERROR),
    QUESTION_NOT_FOUND("error.question.not_found", HttpStatus.NOT_FOUND);

    private final String messageKey;

    private final HttpStatus httpStatus;
}
