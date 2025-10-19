package com.example.restaurApp.util;

import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.excepciones.EmpleadoInactivoException;
import com.example.restaurApp.excepciones.Validacion;

import jakarta.persistence.EntityNotFoundException;

public class EmpleadoUtil {

    /**
     * Valida que un empleado esté activo y pueda realizar operaciones
     */
    public static void validarEmpleadoActivo(Empleado empleado) {
        if (empleado == null) {
            throw new EntityNotFoundException("Empleado no encontrado.");
        }
        if (!empleado.isActivo()) {
            throw new EmpleadoInactivoException("El empleado está inactivo y no puede realizar operaciones.");
        }
    }

    /**
     * Valida que un empleado tenga un rol específico
     */
    public static void validarRolEmpleado(Empleado empleado, String rolRequerido) {
        validarEmpleadoActivo(empleado);

        if (empleado.getRol() == null || empleado.getRol().getDescripcion() == null) {
            throw new Validacion("El empleado no tiene un rol asignado.");
        }

        String rolActual = empleado.getRol().getDescripcion();
        if (!rolActual.equalsIgnoreCase(rolRequerido)) {
            throw new Validacion("Acceso denegado. Se requiere rol: " + rolRequerido +
                    ". Rol actual: " + rolActual);
        }
    }

    /**
     * Valida que un empleado tenga uno de los roles permitidos
     */
    public static void validarRolesEmpleado(Empleado empleado, String... rolesPermitidos) {
        validarEmpleadoActivo(empleado);

        if (empleado.getRol() == null || empleado.getRol().getDescripcion() == null) {
            throw new Validacion("El empleado no tiene un rol asignado.");
        }

        String rolActual = empleado.getRol().getDescripcion();
        boolean tieneRolPermitido = false;

        for (String rolPermitido : rolesPermitidos) {
            if (rolActual.equalsIgnoreCase(rolPermitido)) {
                tieneRolPermitido = true;
                break;
            }
        }

        if (!tieneRolPermitido) {
            String rolesStr = String.join(", ", rolesPermitidos);
            throw new Validacion("Acceso denegado. Se requiere uno de los roles: " + rolesStr +
                    ". Rol actual: " + rolActual);
        }
    }

    /**
     * Valida que un empleado sea administrador o tenga el rol específico
     */
    public static void validarAdminOrol(Empleado empleado, String rolEspecifico) {
        validarEmpleadoActivo(empleado);

        if (empleado.getRol() == null || empleado.getRol().getDescripcion() == null) {
            throw new Validacion("El empleado no tiene un rol asignado.");
        }

        String rolActual = empleado.getRol().getDescripcion();
        boolean esAdmin = rolActual.equalsIgnoreCase("ADMIN");
        boolean tieneRolEspecifico = rolActual.equalsIgnoreCase(rolEspecifico);

        if (!esAdmin && !tieneRolEspecifico) {
            throw new Validacion("Acceso denegado. Se requiere rol ADMINISTRADOR o " + rolEspecifico +
                    ". Rol actual: " + rolActual);
        }
    }
}
