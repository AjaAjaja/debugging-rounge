package ajaajaja.debugging_rounge.common.security.handler;

import ajaajaja.debugging_rounge.common.exception.ErrorCode;
import ajaajaja.debugging_rounge.common.exception.ErrorResponse;
import ajaajaja.debugging_rounge.common.jwt.exception.CustomAuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED; // 기본값

        if (authException instanceof CustomAuthenticationException custom) {
            errorCode = custom.getErrorCode();
        }

        sendError(response, request.getLocale(), errorCode);
    }

    private void sendError(
            HttpServletResponse response,
            Locale locale,
            ErrorCode errorCode) throws IOException {

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        String message = messageSource.getMessage(errorCode.getMessageKey(), null, locale);
        ErrorResponse error = ErrorResponse.of(errorCode, List.of(message));
        objectMapper.writeValue(response.getWriter(), error);
    }
}
