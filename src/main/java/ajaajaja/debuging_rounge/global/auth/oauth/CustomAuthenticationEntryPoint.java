package ajaajaja.debuging_rounge.global.auth.oauth;

import ajaajaja.debuging_rounge.global.exception.ErrorCode;
import ajaajaja.debuging_rounge.global.exception.ErrorResponse;
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
        sendError(response, request.getLocale());
    }

    private void sendError(
            HttpServletResponse response,
            Locale locale) throws IOException {
        response.setStatus(ErrorCode.AUTHENTICATION_FAILED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = messageSource.getMessage(ErrorCode.AUTHENTICATION_FAILED.getMessageKey(), null, locale);
        ErrorResponse error = ErrorResponse.of(ErrorCode.AUTHENTICATION_FAILED, List.of(message));
        objectMapper.writeValue(response.getWriter(), error);
    }
}
