package com.example.restaurApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    @OneToMany(mappedBy = "rol")
    private List<Empleado> empleados;

    public Rol() {}

    public Rol(String nombre, String descripcion, List<Empleado> empleados) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.empleados = empleados;
    }
}
