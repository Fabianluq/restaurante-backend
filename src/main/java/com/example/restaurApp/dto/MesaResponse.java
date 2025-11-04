package com.example.restaurApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MesaResponse {
    private Long id;
    private int capacidad;
    private int numero;
    private String estado;
}
