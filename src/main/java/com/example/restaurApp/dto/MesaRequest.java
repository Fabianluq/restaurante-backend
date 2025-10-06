package com.example.restaurApp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MesaRequest {
    private Long id;
    private int numero;
    private int capacidad;
    private Long estadoId;

}
