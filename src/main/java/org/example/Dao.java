package org.example;
import org.example.Excepciones.Libro.*;
import org.example.Excepciones.Prestamo.PrestamoNoEncontradoException;
import org.example.Excepciones.Socio.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface Dao extends AutoCloseable
{
    //Declaramos la lista de los libros
    List <Libro> getLibros() throws SQLException;
    //-------------------------------------------------------
    // Voy a hacerlo con propagacion de excepciones en vez de optionals...
    // Optional <Libro> getLibroByIsbn(String ISBN) throws SQLException;
    //-------------------------------------------------------
    //Metodos ->  <<Obtener libro / Actualizar libro / Eliminar libro>>
    Libro getLibroByIsbn(String ISBN) throws SQLException, LibroNoEncontradoException;

    //void updateLibro (Libro libro) throws SQLException, LibroNoEncontradoException;
    //void deleteLibroByIsbn (String ISBN) throws SQLException, LibroNoEncontradoException;

    //-------------------------------------------------------
    Libro insertarLibro(String ISBN , String titulo, String estado) throws SQLException, IsbnObligatorioException, TituloObligatorioException, IsbnDuplicadoException;

    //-------------------------------------------------------

    void prestarLibro(String isbn, String dni, LocalDateTime fechaHoraPrestamo) throws SQLException, IsbnObligatorioException, LibroNoEncontradoException, DniObligatorioException, SocioNoEncontradoException;

    //-------------------------------------------------------

    void devolverLibro(String isbn, LocalDateTime fechaHoraDevolucion) throws SQLException, IsbnObligatorioException, LibroNoEncontradoException, PrestamoNoEncontradoException;

    //================================================================

    //Declaramos la lista de los socios
    List <Socio> getSocios() throws SQLException;

    Socio getSocioByDni(String DNI) throws SQLException , SocioNoEncontradoException;

    Socio insertarSocio(String nombre , String email, String direccion , String DNI, int nPrestamo) throws DireccionObligatorioException, DniDuplicadoException, DniObligatorioException, EmailDuplicadoException, EmailObligatorioException, NombreObligatorioException, NombreDuplicadoException, NumPrestamosRangoIncorrectoException, SQLException;

    //==========================HISTORICO======================================

    List <Prestamo> historicoLibro(String ISBN) throws SQLException, IsbnObligatorioException , DniObligatorioException;
    List <Prestamo> historicoSocio(String DNI) throws PrestamoNoEncontradoException,DniObligatorioException,SQLException;

}
