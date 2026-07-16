package com.mvrtechnology.plcdata.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@ControllerAdvice
public class SseExceptionHandler
{
    @ExceptionHandler({IOException.class, AsyncRequestNotUsableException.class})
    public void handleClientDisconnect(IOException ex, HttpServletRequest request)
    {
        log.debug("SSE client disconnected during write: {}", request.getRequestURI());
    }
}
