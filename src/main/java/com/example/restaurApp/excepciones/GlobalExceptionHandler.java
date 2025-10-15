package com.example.restaurApp.excepciones;

import com.example.restaurApp.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Validacion.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidacionException(
            Validacion ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.error(ex.getMessage(), 400);
        response.setDatos(errorDetails);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RecursoNoEncontrado.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleRecursoNoEncontradoException(
            RecursoNoEncontrado ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.notFound(ex.getMessage());
        response.setDatos(errorDetails);
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ReservaNoDisponible.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleReservaNoDisponibleException(
            ReservaNoDisponible ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.error(ex.getMessage(), 409);
        response.setDatos(errorDetails);
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.error(ex.getMessage(), 400);
        response.setDatos(errorDetails);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleSecurityException(
            SecurityException ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        
        ApiResponse<Map<String, Object>> response = ApiResponse.forbidden(ex.getMessage());
        response.setDatos(errorDetails);
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("message", "Error interno del servidor");
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        errorDetails.put("error", ex.getClass().getSimpleName());
        
        // Log the actual exception for debugging
        logger.error("Error no manejado: ", ex);
        
        ApiResponse<Map<String, Object>> response = ApiResponse.internalError("Error interno del servidor");
        response.setDatos(errorDetails);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}