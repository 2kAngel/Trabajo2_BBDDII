package org.example;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Data

public class Libro
{
//  MAYUS -> Primary Key
//  ACCESO  TIPOJAVA            ATRIBUTO           TIPOSQL

    private String              ISBN;            //VARHCAR
    private String              titulo;          //VARCHAR
    private String              estado;          //VARCHAR  LIBRE/OCUPADO

}
