package ajaajaja.debugging_rounge.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    REQUEST_INVALID("error.request.invalid", HttpStatus.BAD_REQUEST),
    RESOURCE_NOT_FOUND("error.resource.not_found", HttpStatus.NOT_FOUND),
    SERVER_INTERNAL_ERROR("error.server.internal", HttpStatus.INTERNAL_SERVER_ERROR),
    QUESTION_NOT_FOUND("error.question.not_found", HttpStatus.NOT_FOUND),
    QUESTION_NOT_FOUND_FOR_DELETE("error.question.not_found_for_delete", HttpStatus.NOT_FOUND),
    UNSUPPORTED_SOCIAL_TYPE("error.social_type.unsupported", HttpStatus.BAD_REQUEST),
    JWT_CREATION_FAILED("error.jwt.creation_failed", HttpStatus.INTERNAL_SERVER_ERROR),
    JWT_PARSING_FAILED("error.jwt.parsing_failed", HttpStatus.UNAUTHORIZED),
    JWT_INVALID("error.jwt.validation_failed", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("error.user.not_found", HttpStatus.NOT_FOUND),
    REFRESH_TOKEN_NOT_FOUND  ("error.refresh_token_not_found",  HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_INVALID("error.refresh_token.invalid", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED("error.authentication.failed", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_FAILED("error.authorization.failed", HttpStatus.FORBIDDEN),
    AUTHENTICATION_REQUIRED("error.authentication.required", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_PRINCIPAL_INVALID("error.authentication.principal_invalid", HttpStatus.UNAUTHORIZED),
    USER_IDENTIFIER_INVALID("error.user.identifier_invalid", HttpStatus.UNAUTHORIZED),
    QUESTION_UPDATE_FORBIDDEN("error.question.update_forbidden", HttpStatus.FORBIDDEN),
    QUESTION_DELETE_FORBIDDEN("error.question.delete_forbidden", HttpStatus.FORBIDDEN),
    ANSWER_NOT_FOUND("error.answer.not_found", HttpStatus.NOT_FOUND),
    ANSWER_UPDATE_FORBIDDEN("error.answer.update_forbidden", HttpStatus.FORBIDDEN);

    private final String messageKey;

    private final HttpStatus httpStatus;
}
