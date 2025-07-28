package ajaajaja.debuging_rounge.global.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<String> handleBusinessException(BusinessException e, Locale locale) {

        ErrorCode errorCode = e.getErrorCode();
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, locale);
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, message);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse.getErrorMessage());
    }

}
