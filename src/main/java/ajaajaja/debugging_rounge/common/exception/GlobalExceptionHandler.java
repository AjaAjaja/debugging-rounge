package ajaajaja.debugging_rounge.common.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> handleBindException(BindException e, Locale locale) {

        List<String> errorMessages = e.getBindingResult().getFieldErrors().stream().map(error -> {
            String messageKey = error.getDefaultMessage();
            String message = messageSource.getMessage(messageKey, null, messageKey, locale);

            return error.getField() + ": " + message;
        }).collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REQUEST_INVALID, errorMessages);

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e, Locale locale) {

        ErrorCode errorCode = e.getErrorCode();
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, locale);
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, List.of(message));

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            HttpRequestMethodNotSupportedException.class,
            NoResourceFoundException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    protected ResponseEntity<ErrorResponse> handleRequestInvalidRequest(Exception e, Locale locale) {

        ErrorCode errorCode = ErrorCode.REQUEST_INVALID;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, locale);
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, List.of(message));

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e, Locale locale) {

        ErrorCode errorCode = ErrorCode.SERVER_INTERNAL_ERROR;
        String message = messageSource.getMessage(errorCode.getMessageKey(), null, locale);
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, List.of(message));

        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
