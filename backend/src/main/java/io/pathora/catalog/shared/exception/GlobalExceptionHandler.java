package io.pathora.catalog.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(ResourceNotFoundException.class)
  ResponseEntity<ApiError> notFound(ResourceNotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
  }

  @ExceptionHandler(ConflictException.class)
  ResponseEntity<ApiError> conflict(ConflictException ex, HttpServletRequest req) {
    return build(HttpStatus.CONFLICT, ex.getMessage(), req, null);
  }

  @ExceptionHandler(ForbiddenException.class)
  ResponseEntity<ApiError> forbidden(ForbiddenException ex, HttpServletRequest req) {
    return build(HttpStatus.FORBIDDEN, ex.getMessage(), req, null);
  }

  @ExceptionHandler(UnauthorizedException.class)
  ResponseEntity<ApiError> unauthorized(UnauthorizedException ex, HttpServletRequest req) {
    return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), req, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
    return build(
        HttpStatus.BAD_REQUEST, "The submitted data is incomplete or invalid.", req, errors);
  }

  @ExceptionHandler(DataAccessException.class)
  ResponseEntity<ApiError> database(DataAccessException ex, HttpServletRequest req) {
    return build(
        HttpStatus.SERVICE_UNAVAILABLE, "The database is currently unavailable.", req, null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.", req, null);
  }

  private ResponseEntity<ApiError> build(
      HttpStatus status, String message, HttpServletRequest req, Map<String, String> errors) {
    return ResponseEntity.status(status)
        .body(
            new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                errors,
                Instant.now()));
  }
}
