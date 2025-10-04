package com.example.restaurApp.service;

import com.example.restaurApp.entity.Rol;
import com.example.restaurApp.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {
    private RolRepository rolRepository;
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Rol crearRol(Rol rol){
        return rolRepository.save(rol);
    }

    public List<Rol> listarRol(){
        return rolRepository.findAll();
    }

    public List<Rol> listarPorNombre(String nombre){
        return rolRepository.findByNombre(nombre);
    }

    public Rol actualizarRol(Long id, Rol rol){
        return rolRepository.findById(id)
                .map ( r -> {
                    r.setNombre(rol.getNombre());
                    r.setDescripcion(rol.getDescripcion());
                    return rolRepository.save(r);
                }).orElseThrow(() -> new RuntimeException("Rol no encontrao"));
    }

    public void eliminarRol(Long id) {

        if (!rolRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con id: " + id);
        }
        rolRepository.deleteById(id);
    }


}

