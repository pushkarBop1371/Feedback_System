package com.campus.feedbacktool.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

/**
 * Central place that turns exceptions into consistent JSON error bodies and
 * the right HTTP status, so callers can reliably tell:
 *   - 400 Bad Request  -> the caller sent something invalid
 *   - 404 Not Found    -> the entity the caller referenced doesn't exist
 *   - 500 Server Error -> something unexpected broke on our side
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- 404: entity not found -------------------------------------------------
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ApiError body = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // --- 400: bean validation failures (@Valid on request bodies) --------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Validation failed for one or more fields", request.getRequestURI(), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- 400: malformed JSON body -----------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request",
                "Request body is missing or malformed JSON", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- 400: wrong type in a path variable / query param (e.g. /surveys/abc) --
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Parameter '%s' should be of type %s", ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "a different type");
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", message, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- 400: illegal arguments raised deliberately by the service layer -------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ApiError body = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // --- 401: login attempted with a wrong username/password -------------------
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex, HttpServletRequest request) {
        ApiError body = new ApiError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // --- 500: anything else we didn't anticipate --------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        ApiError body = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
                "Something went wrong while processing your request", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
