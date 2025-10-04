package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.ClienteRequest;
import com.example.restaurApp.dto.ClienteResponse;
import com.example.restaurApp.entity.Cliente;

public class ClienteMapper {
    public static ClienteResponse toResponse (Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getCorreo(),
                cliente.getTelefono()
        );
    }

    public static Cliente toEntity(ClienteRequest request) {
        Cliente cliente = new Cliente();
        cliente.setNombre(request.getNombre());
        cliente.setApellido(request.getApellido());
        cliente.setCorreo(request.getCorreo());
        cliente.setTelefono(request.getTelefono());
        return cliente;
    }
}
