package ajaajaja.debuging_rounge.global.auth.oauth;

import ajaajaja.debuging_rounge.global.exception.ErrorCode;
import ajaajaja.debuging_rounge.global.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final MessageSource messageSource;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        sendError(response, request.getLocale());
    }

    private void sendError(
            HttpServletResponse response,
            Locale locale) throws IOException {
        response.setStatus(ErrorCode.AUTHORIZATION_FAILED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String message = messageSource.getMessage(ErrorCode.AUTHORIZATION_FAILED.getMessageKey(), null, locale);
        ErrorResponse error = ErrorResponse.of(ErrorCode.AUTHORIZATION_FAILED, List.of(message));
        objectMapper.writeValue(response.getWriter(), error);
    }
}
