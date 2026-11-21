package com.fitpass.exception;

import com.fitpass.dto.ApiResponse;
import com.fitpass.dto.ErrorResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Error de validación en los campos enviados",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
    )
    public ResponseEntity<ApiResponse<Void>> manejarValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("Datos de entrada inválidos");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(mensaje));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Argumento inválido en la petición",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
    )
    public ResponseEntity<ApiResponse<Void>> manejarBadRequest(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Recurso no encontrado o condición de negocio no cumplida",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
    )
    public ResponseEntity<ApiResponse<Void>> manejarRuntimeException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500",
            description = "Error interno inesperado en el servidor",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))
    )
    public ResponseEntity<ApiResponse<Void>> manejarGeneral(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Ocurrió un error interno en el servidor."));
    }
}