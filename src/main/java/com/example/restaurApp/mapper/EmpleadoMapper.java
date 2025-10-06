package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.EmpleadoRequest;
import com.example.restaurApp.dto.EmpleadoResponse;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.Rol;

public class EmpleadoMapper {

    public static EmpleadoResponse toResponse(Empleado empleado) {
        return new EmpleadoResponse(
                empleado.getId(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getCorreo(),
                empleado.getTelefono(),
                empleado.getRol().getNombre());
    }

    public static Empleado toEntity(EmpleadoRequest request, Rol rol) {
        Empleado empleado = new Empleado();
        empleado.setNombre(request.getNombre());
        empleado.setApellido(request.getApellido());
        empleado.setCorreo(request.getCorreo());
        empleado.setTelefono(request.getTelefono());
        empleado.setContrasenia(request.getContrasenia());
        empleado.setRol(rol);
        return empleado;
    }
}
