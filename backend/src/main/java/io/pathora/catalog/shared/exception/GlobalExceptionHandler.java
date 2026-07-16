package io.pathora.catalog.shared.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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
        HttpStatus.BAD_REQUEST,
        "Los datos enviados están incompletos o no son válidos.",
        req,
        errors);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
    return build(
        HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no tiene un formato válido.", req, null);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<ApiError> typeMismatch(
      MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
    return build(
        HttpStatus.BAD_REQUEST,
        "El valor enviado para '" + ex.getName() + "' no es válido.",
        req,
        null);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  ResponseEntity<ApiError> missingParameter(
      MissingServletRequestParameterException ex, HttpServletRequest req) {
    return build(
        HttpStatus.BAD_REQUEST,
        "Falta el parámetro obligatorio '" + ex.getParameterName() + "'.",
        req,
        null);
  }

  @ExceptionHandler(DataAccessException.class)
  ResponseEntity<ApiError> database(DataAccessException ex, HttpServletRequest req) {
    return build(
        HttpStatus.SERVICE_UNAVAILABLE,
        "La base de datos no está disponible en este momento.",
        req,
        null);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<ApiError> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, null);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado.", req, null);
  }

  private ResponseEntity<ApiError> build(
      HttpStatus status, String message, HttpServletRequest req, Map<String, String> errors) {
    return ResponseEntity.status(status)
        .body(
            new ApiError(
                status.value(),
                errorName(status),
                message,
                req.getRequestURI(),
                errors,
                Instant.now()));
  }

  private String errorName(HttpStatus status) {
    return switch (status) {
      case BAD_REQUEST -> "Solicitud incorrecta";
      case UNAUTHORIZED -> "No autorizado";
      case FORBIDDEN -> "Acceso prohibido";
      case NOT_FOUND -> "No encontrado";
      case CONFLICT -> "Conflicto";
      case SERVICE_UNAVAILABLE -> "Servicio no disponible";
      case INTERNAL_SERVER_ERROR -> "Error interno del servidor";
      default -> "Error";
    };
  }
}
