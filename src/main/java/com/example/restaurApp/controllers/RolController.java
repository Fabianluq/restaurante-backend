package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.RolRequest;
import com.example.restaurApp.dto.RolResponse;
import com.example.restaurApp.entity.Rol;
import com.example.restaurApp.mapper.RolMapper;
import com.example.restaurApp.service.RolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolController {
    private RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @PostMapping
    public ResponseEntity<RolResponse> crearRol(@RequestBody RolRequest rolRequest) {
        Rol rol = RolMapper.toEntity(rolRequest);
        Rol nuevoRol = rolService.crearRol(rol);
        return ResponseEntity.ok().body(RolMapper.toResponse(nuevoRol));
    }

    @GetMapping
    public ResponseEntity<List<RolResponse>> ListarRol() {
        List<RolResponse> roles = rolService.listarRol()
                .stream()
                .map(RolMapper::toResponse)
                .toList();
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/rol/{nombre}")
    public ResponseEntity<List<RolResponse>> ListarPorNombre(@RequestParam String nombre) {
        List<RolResponse> roles = rolService.listarPorNombre(nombre)
                .stream()
                .map(RolMapper::toResponse)
                .toList();
        return ResponseEntity.ok(roles);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolResponse> actualizarRol(@RequestBody Long id, @RequestBody RolRequest rolRequest) {
        Rol rol = RolMapper.toEntity(rolRequest);
        try {
            Rol actualizado = rolService.actualizarRol(id, rol);
            return ResponseEntity.ok(RolMapper.toResponse(actualizado));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> eliminarRol(@PathVariable Long id){
        try{
            rolService.eliminarRol(id);
            return ResponseEntity.noContent().build();
        }catch (RuntimeException e){
            return ResponseEntity.notFound().build();
        }

    }
}

