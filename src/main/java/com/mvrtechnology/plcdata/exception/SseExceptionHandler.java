package com.mvrtechnology.plcdata.exception;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import java.io.IOException;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SseExceptionHandler {

    @ExceptionHandler({
            IOException.class,
            AsyncRequestNotUsableException.class
    })
    public void handleClientDisconnect(
            Exception ex,
            HttpServletRequest request
    ) {

        log.debug(
                "SSE client disconnected. uri={} reason={}",
                request.getRequestURI(),
                ex.getMessage()
        );
    }
}
