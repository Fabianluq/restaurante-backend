package com.example.restaurApp.mapper;

import com.example.restaurApp.dto.RolRequest;
import com.example.restaurApp.dto.RolResponse;
import com.example.restaurApp.entity.Rol;

public class RolMapper {
    public static RolResponse toResponse(Rol rol){
        return new RolResponse(
                rol.getId(),
                rol.getNombre(),
                rol.getDescripcion()
        );
    }

    public static Rol toEntity(RolRequest request){
        Rol rol = new Rol();
        rol.setNombre(request.getNombre());
        rol.setDescripcion(request.getDescripcion());
        return rol;
    }
}
