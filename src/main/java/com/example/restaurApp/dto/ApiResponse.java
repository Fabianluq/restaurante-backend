package com.example.restaurApp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private String mensaje;
    private String estado;
    private T datos;
    private int codigo;

    public ApiResponse() {}

    public ApiResponse(String mensaje, String estado, T datos, int codigo) {
        this.mensaje = mensaje;
        this.estado = estado;
        this.datos = datos;
        this.codigo = codigo;
    }

    // Métodos estáticos para crear respuestas comunes
    public static <T> ApiResponse<T> success(T datos) {
        return new ApiResponse<>("Operación exitosa", "SUCCESS", datos, 200);
    }

    public static <T> ApiResponse<T> success(String mensaje, T datos) {
        return new ApiResponse<>(mensaje, "SUCCESS", datos, 200);
    }

    public static <T> ApiResponse<T> created(String mensaje, T datos) {
        return new ApiResponse<>(mensaje, "CREATED", datos, 201);
    }

    public static <T> ApiResponse<T> error(String mensaje, int codigo) {
        return new ApiResponse<>(mensaje, "ERROR", null, codigo);
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(mensaje, "ERROR", null, 400);
    }

    public static <T> ApiResponse<T> notFound(String mensaje) {
        return new ApiResponse<>(mensaje, "NOT_FOUND", null, 404);
    }

    public static <T> ApiResponse<T> unauthorized(String mensaje) {
        return new ApiResponse<>(mensaje, "UNAUTHORIZED", null, 401);
    }

    public static <T> ApiResponse<T> forbidden(String mensaje) {
        return new ApiResponse<>(mensaje, "FORBIDDEN", null, 403);
    }

    public static <T> ApiResponse<T> internalError(String mensaje) {
        return new ApiResponse<>(mensaje, "INTERNAL_ERROR", null, 500);
    }
}
