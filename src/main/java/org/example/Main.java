package org.example;

import org.example.Excepciones.Libro.LibroNoEncontradoException;
import org.example.Excepciones.Socio.SocioNoEncontradoException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        //try(Dao dao = new PostgresDao("jdbc:postgresql://localhost:5432/biblioteca" , "system","manager");
        try(Dao dao = new PostgresDao("jdbc:postgresql://localhost:5432/upsa" , "system","manager");
        ){

            System.out.println("==============================================");
            System.out.println("------------- Insercion de libro -------------");
            //A -> Insertamos un Libro
            //dao.insertarLibro("00000000000E","Cinco Horas Con Mario","LIBRE");
            System.out.println("==============================================");

            System.out.println(" ");

            System.out.println("==============================================");
            System.out.println("----------- Mostrar Libro por ISBN -----------");
            //Mostramos libro por su ISBN
            Libro libro1 = dao.getLibroByIsbn("00000000000D");
            System.out.println("Libro por ISBN -> "+libro1);
            System.out.println("==============================================");

            System.out.println(" ");

            System.out.println("==============================================");
            System.out.println("---------- Mostrar TODOS los libros ----------");
            //Mostramos todos los libros que tenemos
            List<Libro> libros = dao.getLibros();
            libros.forEach(System.out::println);
            System.out.println("==============================================");
            System.out.println(" ");
            System.out.println(" ");
            //--------------------------------------------

            System.out.println("==============================================");
            System.out.println("------------- Insercion de socio -------------");
            //A2 -> Insertamos un Socio
            //dao.insertarSocio("Lorena Galvez","lg@gmail.com","C/El Naranjo, P1, 2ºA","12345678E",2);
            //dao.insertarSocio("Javier Garcia","jg@gmail.com","C/El Recreo, P3, 3ºD","12345678F",3);
            System.out.println("-------------- Socio Insertado  --------------");
            System.out.println("==============================================");

            System.out.println(" ");

            System.out.println("==============================================");
            System.out.println("----------- Mostrar SOCIO por DNI  -----------");
            //Mostramos Socio por su DNI
            Socio socio1 = dao.getSocioByDni("12345678A");
            System.out.println("Socio por DNI -> "+socio1);
            System.out.println("==============================================");

            System.out.println(" ");

            System.out.println("==============================================");
            System.out.println("---------- Mostrar TODOS los Socios ----------");
            //Mostramos todos los socios que tenemos
            List<Socio> socios = dao.getSocios();
            socios.forEach(System.out::println);
            System.out.println("==============================================");

            System.out.println(" ");
            System.out.println("=            =           =          =                 =");
            // -------> COMPROBAR DE EXCEPCIONES !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! <-------

            LocalDateTime fechaHoraSistema = LocalDateTime.now();

            System.out.println("========================================================================");
            System.out.println("---------- Prestar Libro isbn 00000000000D al socio 12345678C ----------");
            //dao.prestarLibro("00000000000D", "12345678C", fechaHoraSistema );
            System.out.println("========================================================================");

            System.out.println("========================================================================");
            System.out.println("---------- Prestar Libro isbn 00000000000B al socio 12345678C ----------");
            //dao.prestarLibro("00000000000B", "12345678C", fechaHoraSistema );
            System.out.println("========================================================================");

            //<<<<<ESTO CREO Q NOS DA UN NULL EN EL TITULO Q DEVUELVE , MIRARLO , PORQUE ESO NO PUEDE PASAR>>>>>>
            System.out.println("========================================================================");
            System.out.println("---------- Devolver Libro isbn 00000000000B al socio 12345678C ---------");
            //dao.devolverLibro("00000000000B", fechaHoraSistema );
            System.out.println("========================================================================");

            System.out.println(" ");



            //<<<<<<APARTE DE LO Q NOS PIDE, PARA PODER PASARLE EL TITULO Y EL NOMBRE RESPECTIVAMENTE HE TENIDO Q CREAR DICHOS
            //ATRIBS EN PRESTAMO,SI QUIERES DEJARLO BONITO EN VEZ DE USAR SOUTC(LAMDA) HAY Q ESCRIBIR el fore A MANO :D....>>>>>>
            System.out.println("===================HISTORICO===========================");
            System.out.println("---------- Mostrar TODOS los prestamos ISBN concreto ----------");
            //Mostramos todos los prestamos que tenemos con ISBN concreto:
            List<Prestamo> prestamosISBN = dao.historicoLibro("00000000000D");
            prestamosISBN.forEach(System.out::println);
            System.out.println("---------- Mostrar TODOS los prestamos DNI concreto ----------");
            //Mostramos todos los prestamos que tenemos con ISBN concreto:
            List<Prestamo> prestamosDNI = dao.historicoSocio("12345678C");
            prestamosDNI.forEach(System.out::println);







            System.out.println("==============================================");

        }
        catch (LibroNoEncontradoException exception)
        {
            System.out.println("Libro no existe");
        }
        catch (SocioNoEncontradoException exception)
        {
            System.out.println("Socio no existe");
        }









    }
}