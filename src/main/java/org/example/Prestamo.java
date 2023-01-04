package org.example;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Prestamo
{
// ACCESO  TIPOJAVA      ATRIBUTO            TIPOSQL
   private String        ISBN;               //VARCHAR
   private String        DNI;                //VARCHAR
   private LocalDateTime fechaPrestamo;      //TIMESTAMP
   private LocalDateTime fechaDevolucion;    //TIMESTAMP
}
