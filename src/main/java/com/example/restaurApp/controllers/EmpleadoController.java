package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.EmpleadoRequest;
import com.example.restaurApp.dto.EmpleadoResponse;
import com.example.restaurApp.entity.Empleado;
import com.example.restaurApp.entity.Rol;
import com.example.restaurApp.mapper.EmpleadoMapper;
import com.example.restaurApp.repository.RolRepository;
import com.example.restaurApp.service.EmpleadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private EmpleadoService empleadoService;
    private RolRepository rolRepository;

    public EmpleadoController(EmpleadoService empleadoService, RolRepository rolRepository) {
        this.empleadoService = empleadoService;
        this.rolRepository = rolRepository;
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@RequestBody EmpleadoRequest request) {
        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Empleado empleado = EmpleadoMapper.toEntity(request, rol);
        Empleado nuevoEmpleado = empleadoService.crearEmpleado(empleado);
        return ResponseEntity.status(201).body(EmpleadoMapper.toResponse(nuevoEmpleado));
    }


    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados() {
        List<EmpleadoResponse> empleados = empleadoService.listarEmpleados()
                .stream()
                .map(EmpleadoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> buscarPorId(@PathVariable Long id) {
        return  empleadoService.buscarPorId(id).map(EmpleadoMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<EmpleadoResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<EmpleadoResponse> empleados = empleadoService.buscarPorNombre(nombre).stream()
                .map(EmpleadoMapper::toResponse).toList();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/buscar/correo")
    public ResponseEntity<EmpleadoResponse> buscarPorCorreo(@RequestParam String correo) {
        return empleadoService.buscarPorCorreo(correo).map(EmpleadoMapper::toResponse)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<EmpleadoResponse>> ListarEmpleadoPorRol(@PathVariable Long rolId ) {
        List<EmpleadoResponse> empleados = empleadoService.ListarEmpleadoPorRol(rolId)
                .stream()
                .map(EmpleadoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(empleados);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoResponse> actualizarEmpleado(@PathVariable Long id,
                                                               @RequestBody EmpleadoRequest request) {
        Rol rol = rolRepository.findById(request.getRolId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        Empleado empleado = EmpleadoMapper.toEntity(request, rol);
        try {
            Empleado actualizado = empleadoService.actualizarEmpleado(id, empleado);
            return ResponseEntity.ok(EmpleadoMapper.toResponse(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        try {
            empleadoService.eliminarEmpleado(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}

