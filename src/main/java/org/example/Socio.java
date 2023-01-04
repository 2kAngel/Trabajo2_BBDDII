package org.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Socio
{
// ACCESO   TIPOJAVA        ATRIBUTO            TIPOSQL
    private String          nombre;             //VARHCAR
    private String          email;              //VARHCAR
    private String          direccion;          //VARHCAR
    private String          DNI; //PK           //VARHCAR
    private int             nPrestamo;          //INTEGER
}