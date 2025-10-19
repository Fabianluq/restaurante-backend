package com.example.restaurApp.controllers;

import com.example.restaurApp.dto.ClienteRequest;
import com.example.restaurApp.dto.ClienteResponse;
import com.example.restaurApp.entity.Cliente;
import com.example.restaurApp.mapper.ClienteMapper;
import com.example.restaurApp.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@RequestBody ClienteRequest request) {
        Cliente cliente = ClienteMapper.toEntity(request);
        Cliente nuevoCliente = clienteService.crearCliente(cliente);
        return ResponseEntity.status(201).body(ClienteMapper.toResponse(nuevoCliente));
    }


    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        List<ClienteResponse> cliente = clienteService.listarClientes()
                .stream()
                .map(ClienteMapper::toResponse)
                .toList();
        return ResponseEntity.ok(cliente);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> buscarPorId(@PathVariable Long id) {
        return  clienteService.buscarPorId(id).map(ClienteMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<List<ClienteResponse>> buscarPorNombre(@RequestParam String nombre) {
        List<ClienteResponse> clientes = clienteService.buscarPorNombre(nombre).stream()
                .map(ClienteMapper::toResponse).toList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/buscar/correo")
    public ResponseEntity<ClienteResponse> buscarPorCorreo(@RequestParam String correo) {
        return clienteService.buscarPorCorreo(correo).map(ClienteMapper::toResponse)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(@PathVariable Long id,
                                                              @RequestBody ClienteRequest request) {

        Cliente cliente = ClienteMapper.toEntity(request);
        try {
            Cliente actualizado = clienteService.actualizarCliente(id, cliente);
            return ResponseEntity.ok(ClienteMapper.toResponse(actualizado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        try {
            clienteService.eliminarCliente(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
