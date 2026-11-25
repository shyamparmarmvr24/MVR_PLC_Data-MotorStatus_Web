package com.mvrtechnology.plcdata.exception;

import com.mvrtechnology.plcdata.dtos.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.persistence.PersistenceException;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MissingServletRequestParameterException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int MAX_LOG_MESSAGE_LENGTH = 500;


    // ==========================================
    // BUSINESS EXCEPTION
    // ==========================================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {

        if (ex.getStatus() == HttpStatus.FORBIDDEN) {

            log.warn(
                    "Request denied. method={} uri={} status={} field={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    ex.getStatus().value(),
                    ex.getField()
            );
        }

        return ResponseEntity
                .status(ex.getStatus())
                .body(new ErrorResponse(
                        false,
                        ex.getField(),
                        ex.getMessage()
                ));
    }


    // MISSING REQUEST PARAMETER

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException ex
    ) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        false,
                        ex.getParameterName(),
                        ex.getParameterName() + " is required."
                ));
    }


    // INVALID PARAMETER TYPE / DATE FORMAT

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex
    ) {

        String message;

        if ("fromDateTime".equals(ex.getName())
                || "toDateTime".equals(ex.getName())) {

            message = "Invalid date-time format. Expected format: yyyy-MM-dd'T'HH:mm:ss";

        } else if ("plantId".equals(ex.getName())) {

            message = "Plant ID must be a valid number.";

        } else if ("intervalMinutes".equals(ex.getName())) {

            message = "Interval must be a valid number.";

        } else {

            message = "Invalid value for " + ex.getName() + ".";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        false,
                        ex.getName(),
                        message
                ));
    }


    // ==========================================
    // DATABASE EXCEPTION

    @ExceptionHandler({
            DataAccessException.class,
            SQLException.class,
            PersistenceException.class
    })
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            Exception ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (isSseResponse(request,response)) {

            log.debug(
                    "Ignoring exception response for SSE connection. uri={} reason={}",
                    request.getRequestURI(),
                    ex.getMessage()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Throwable root = getRootCause(ex);

        log.error(
                "Database operation failed. method={} uri={} exception={} rootCause={} reason={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                root.getClass().getSimpleName(),
                getSafeMessage(root)
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        false,
                        null,
                        "A database error occurred. Please try again later."
                ));
    }


    // ==========================================
    // GENERAL EXCEPTION
    // ==========================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (isSseResponse(request,response)) {

            log.debug(
                    "Ignoring exception response for SSE connection. uri={} reason={}",
                    request.getRequestURI(),
                    ex.getMessage()
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        Throwable root = getRootCause(ex);

        log.error(
                "Unhandled application exception. method={} uri={} exception={} rootCause={} reason={}",
                request.getMethod(),
                request.getRequestURI(),
                ex.getClass().getSimpleName(),
                root.getClass().getSimpleName(),
                getSafeMessage(root)
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        false,
                        null,
                        "An unexpected error occurred. Please try again later."
                ));
    }


    // ==========================================
    // ROOT CAUSE
    // ==========================================

    private Throwable getRootCause(Throwable ex) {

        Throwable current = ex;

        int depth = 0;

        while (current.getCause() != null
                && current.getCause() != current
                && depth++ < 20) {

            current = current.getCause();
        }

        return current;
    }


    // ==========================================
    // SAFE LOG MESSAGE
    // ==========================================

    private String getSafeMessage(Throwable ex) {

        String message = ex.getMessage();

        if (message == null || message.isBlank()) {
            return "No exception message available";
        }

        message = message
                .replace('\n', ' ')
                .replace('\r', ' ')
                .replace('\t', ' ');

        if (message.length() > MAX_LOG_MESSAGE_LENGTH) {

            return message.substring(
                    0,
                    MAX_LOG_MESSAGE_LENGTH
            ) + "...";
        }

        return message;
    }

    private boolean isSseResponse(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String accept = request.getHeader("Accept");
        String contentType = response.getContentType();

        return (accept != null && accept.contains("text/event-stream"))
                || (contentType != null && contentType.contains("text/event-stream"))
                || response.isCommitted();
    }
}