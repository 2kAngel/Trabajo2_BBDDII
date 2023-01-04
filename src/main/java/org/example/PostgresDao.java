package org.example;

import org.example.Excepciones.Libro.*;
import org.example.Excepciones.Prestamo.PrestamoNoEncontradoException;
import org.example.Excepciones.Socio.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PostgresDao implements Dao
{
    //------------------------------------------------------------------
    //host + port + user+ pswd
    private Connection connection;
    //------------------------------------------------------------------
    public PostgresDao(String url, String username, String password ) throws SQLException {
        Driver driver = new org.postgresql.Driver();
        DriverManager.registerDriver(driver);
        this.connection = DriverManager.getConnection(url , username , password);
    }
    //------------------------------------------------------------------
    List <Libro> libros = new ArrayList<>();
    //------------------------------------------------------------------
    @Override
    public List<Libro> getLibros() throws SQLException
    {
        String SQL =    "SELECT l.isbn , l.titulo, l.estado "
                +       " FROM libros l                       ";

        try(Statement statement = connection.createStatement(); //statment -> solo sentencias sql sin paramentros
            ResultSet resultSet = statement.executeQuery(SQL) //execute.query -> sentencia simple + result ----- execute.update -> demas
        )
        {
            //recorremos el data set con next , cdo next == false , fin

            if (resultSet.next())//primer registro
            {
                do//tratamiento de registro
                {


                    Libro libro = Libro.builder()
                            .withISBN(resultSet.getString(1))//1 -> el 1º del select = expediente
                            .withTitulo(resultSet.getString(2))
                            .withEstado(resultSet.getString(3)) //
                            .build();

                    libros.add(libro);

                } while (resultSet.next());
            }
            else //la consulta no obtiene ningun registro
            {

            }
        }
        return libros;
    }
    //------------------------------------------------------------------
    @Override
    public Libro getLibroByIsbn(String ISBN) throws SQLException, LibroNoEncontradoException {
        //sentencia sql con paramentros -> cambiamos statment por prepareStatment
        final String SQL= "SELECT l.isbn , l.titulo , l.estado "
                +         " FROM libros l                      "
                +         " WHERE l.isbn = ?                   ";//? -> se sustituiran por le valor del param , solo valores   //no usar nunca ...'"+isbn"'"

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL)
        )
        {
            preparedStatement.setString(1,ISBN);//1 -> ? = primer parametro de la sentencia
            try(ResultSet resultSet = preparedStatement.executeQuery())//query -> pq es un select || si fuera otro seria update
            {
                if(resultSet.next())//1 alumno encontrado (solo 1 libro , no hace falta bucle)
                {
//el return Optional.of siempre distinto de null, si es null da NULLPOINTREXP
                    return  (Libro.builder()
                            .withISBN(resultSet.getString(1))//1 -> el 1º del select = expediente
                            .withTitulo(resultSet.getString(2))
                            .withEstado(resultSet.getString(3))
                            .build()) ;

                    // ó ->local variable + return libro;
                }
                else // no encontrado
                {
                    throw new LibroNoEncontradoException();
                }
            }

        }
    }


    //------------------------------------------------------------------
    @Override
    public Libro insertarLibro(String ISBN, String titulo, String estado) throws SQLException, IsbnObligatorioException, TituloObligatorioException, IsbnDuplicadoException {
        final String SQL = "INSERT INTO libros(isbn, titulo, estado) "
                +          "           VALUES ( ?  , ?     , ?     ) ";

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL))
        {
            preparedStatement.setString(1,ISBN);
            preparedStatement.setString(2,titulo);
            preparedStatement.setString(3,estado);//

            int count = preparedStatement.executeUpdate();// o hace falta pq no hay null, o se inserta o no

            //O se inserta o excepcion -- en el builder da igual el orden de las propiedades
            return Libro.builder()
                    .withISBN(ISBN)
                    .withTitulo(titulo)
                    .withEstado(estado)
                    .build();

        } catch (SQLException sqlException)
        {
            String message = sqlException.getMessage();
            if(message.contains("PK_LIBROS")) throw new IsbnDuplicadoException();//si contiene "..." esto
            if(message.contains("UK_LIBROS_ISBN")) throw new IsbnDuplicadoException();
            if(message.contains("NT_LIBROS_ISBN")) throw new IsbnObligatorioException();
            if(message.contains("NT_LIBROS_TITULO")) throw new TituloObligatorioException();

            throw sqlException; //si no , esto

            //puedes hacer un message para cada constraint de la tabla
        }
    }

    //------------------------------------------------------------------
    //Funciones para PrestarLibro

    public Libro selectLibroISBNLibre(String ISBN) throws SQLException, LibroNoEncontradoException
    {

        //sentencia sql con paramentros -> cambiamos statment por prepareStatment
        final String SQL= "SELECT l.isbn , l.titulo , l.estado      "
                +         " FROM libros l                           "
                +         " WHERE l.isbn = ? AND l.estado = 'LIBRE' ";//

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL)
        )
        {
            preparedStatement.setString(1,ISBN);//1 -> ? = primer parametro de la sentencia
            try(ResultSet resultSet = preparedStatement.executeQuery())//query -> pq es un select || si fuera otro seria update
            {
                if(resultSet.next())//1 alumno encontrado (solo 1 libro , no hace falta bucle)
                {

                    return  (Libro.builder()
                            .withISBN(resultSet.getString(1))//1 -> el 1º del select = expediente
                            .withTitulo(resultSet.getString(2))
                            .withEstado(resultSet.getString(3))
                            .build()) ;
                }
                else
                {
                    throw new LibroNoEncontradoException();
                }
            }

        }
    }

    //------------------------------------------------------------------

    public Socio selectSocioDNIPrestamos(String DNI) throws SQLException, SocioNoEncontradoException
    {
        //sentencia sql con paramentros -> cambiamos statment por prepareStatment
        final String SQL= "SELECT s.nombre, s.email, s.direccion, s.dni, s.nprestamo    "
                +         " FROM socios s                                                   "
                +         " WHERE s.dni = ? AND s.nprestamo <= 5                            ";//? -> se sustituiran por le valor del param , solo valores   //no usar nunca ...'"+dni"'"

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL);

        )
        {
            preparedStatement.setString(1 ,DNI);//1 -> ? = cuarto parametro de la sentencia
            try(ResultSet resultSet = preparedStatement.executeQuery())//query -> pq es un select || si fuera otro seria update
            {
                if(resultSet.next())//1 alumno encontrado (solo 1 libro , no hace falta bucle)
                {
//el return Optional.of siempre distinto de null, si es null da NULLPOINTREXP
                    return  (Socio.builder()
                            .withNombre(resultSet.getString(1))//1 -> el 1º del select = expediente
                            .withEmail(resultSet.getString(2))
                            .withDireccion(resultSet.getString(3)) //
                            .withDNI(resultSet.getString(4)) //
                            .withNPrestamo(resultSet.getInt(5)) //
                            .build());

                    // ó ->local variable + return libro;
                }
                else // no encontrado
                {
                    throw new SocioNoEncontradoException();
                }
            }

        }
    }

    //------------------------------------------------------------------

    public Prestamo insertPrestamo(String ISBN, String DNI, LocalDateTime fechaHoraPrestamo) throws SQLException
    {
        final String SQL = "INSERT INTO prestamos(isbn, dni, fecha_prestamo) "
                +          "              VALUES ( ?  , ?  ,  ?            ) ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        ) {
            preparedStatement.setString(1, ISBN);
            preparedStatement.setString(2, DNI);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(fechaHoraPrestamo));

            preparedStatement.executeUpdate();

            return Prestamo.builder()
                    .withISBN(ISBN)
                    .withDNI(DNI)
                    .withFechaPrestamo(fechaHoraPrestamo)
                    .build();
        } catch (SQLException sqlException)
        {
            String message = sqlException.getMessage();

            throw sqlException;
        }
    }

    //------------------------------------------------------------------

    public void updateEstadoLibro( String isbn ) throws SQLException, LibroNoEncontradoException
    {
        final String SQL = "UPDATE libros              "
                         + "   SET  estado = 'OCUPADO' "
                         + " WHERE    isbn = ?         ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        )
        {
            preparedStatement.setString(1, isbn);

            int nRegistroAfectados = preparedStatement.executeUpdate();

            if ( nRegistroAfectados == 0 ) throw new LibroNoEncontradoException();
        }
    }

    //------------------------------------------------------------------

    public void updatePrestamosSocio( String dni ) throws  SQLException, SocioNoEncontradoException
    {
        final String SQL =   "UPDATE socios                     "
                           + "   SET  nprestamo = nprestamo + 1 "
                           + " WHERE  dni = ?                   ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        )
        {
            preparedStatement.setString(1, dni );

            int nRegistroAfectados = preparedStatement.executeUpdate();

            if ( nRegistroAfectados == 0 ) throw new SocioNoEncontradoException();
        }
    }

    //------------------------------------------------------------------

    @Override
    public void prestarLibro(String isbn, String dni, LocalDateTime fechaHoraPrestamo) throws SQLException, IsbnObligatorioException, LibroNoEncontradoException, DniObligatorioException, SocioNoEncontradoException
    {

        //Llamada a las funciones de Prestar Libro

        selectLibroISBNLibre(isbn);

        selectSocioDNIPrestamos(dni);

        insertPrestamo(isbn, dni, fechaHoraPrestamo);

        updateEstadoLibro(isbn);

        updatePrestamosSocio(dni);

        /*

        //----------------------------------------------
        final String SQL_Libro = "SELECT l.isbn , l.titulo , l.estado       "
                       +         "  FROM libros l                           "
                       +         "  WHERE l.isbn = ? AND l.estado = 'LIBRE' ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL_Libro);
                )
        {
            preparedStatement.setString(1, ISBN);

            try(
                    ResultSet resultSet = preparedStatement.executeQuery();
                    )
            {
                if ( resultSet.next() )
                {
                    //----------------------------------------------
                    final String SQL_SOCIO = "SELECT s.nombre, s.email, s.direccion, s.dni, s.nprestamo "
                                   +         " FROM  socios s                                           "
                                   +         " WHERE s.dni = ? AND s.nprestamo <= 5                     ";

                    try(
                            PreparedStatement preparedStatementSocio = connection.prepareStatement(SQL_SOCIO);
                    )
                    {
                        preparedStatementSocio.setString(1, DNI);

                        try(
                                ResultSet resultSetSocio = preparedStatementSocio.executeQuery();
                        )
                        {
                            if ( resultSetSocio.next() )
                            {
                                //----------------------------------------------
                                final String SQL_Insert_Prestamo = "INSERT INTO prestamos(isbn, dni, fecha_prestamo, fecha_devolucion) "
                                                        +          "              VALUES ( ?  , ?  ,  ?             , ?              ) ";

                                try(
                                        PreparedStatement preparedStatementInsert = connection.prepareStatement(SQL_Insert_Prestamo);
                                ) {
                                    preparedStatementInsert.setString(1, ISBN);
                                    preparedStatementInsert.setString(2, DNI);
                                    preparedStatementInsert.setTimestamp(3, Timestamp.valueOf(fechaHoraPrestamo));

                                    preparedStatementInsert.executeUpdate();

                                    return Prestamo.builder()
                                            .withISBN(ISBN)
                                            .withDNI(DNI)
                                            .withFechaPrestamo(fechaHoraPrestamo)
                                            .build();
                                }

                                //----------------------------------------------

                                final String SQL_Update_Libro = "UPDATE libros              "
                                                              + "   SET  estado = 'OCUPADO' "
                                                              + " WHERE    isbn = ?         ";

                                try(
                                        PreparedStatement preparedStatementUpdateLibro = connection.prepareStatement(SQL_Update_Libro);
                                        )
                                {
                                    preparedStatementUpdateLibro.setString(1, libro.getISBN() );

                                    preparedStatementUpdateLibro.executeUpdate();

                                }

                                //----------------------------------------------

                                final String SQL_Update_Socio =   "UPDATE socios                     "
                                                                + "   SET  nprestamo = nprestamo + 1 "
                                                                + " WHERE    dni = ?                 ";

                                try(
                                        PreparedStatement preparedStatementUpdateSocio = connection.prepareStatement(SQL_Update_Socio);
                                )
                                {
                                    preparedStatementUpdateSocio.setString(1, libro.getISBN() );

                                    preparedStatementUpdateSocio.executeUpdate();

                                }

                                //----------------------------------------------
                            }
                        }
                    }
                    //----------------------------------------------
                }
            }
        }
        //----------------------------------------------
        */
    }

    //------------------------------------------------------------------
    //Funciones para DevolverLibro

    public Libro selectLibroISBNOcupado(String ISBN) throws SQLException, LibroNoEncontradoException
    {

        //sentencia sql con paramentros -> cambiamos statment por prepareStatment
        final String SQL= "SELECT l.isbn , l.titulo , l.estado      "
                +         " FROM libros l                           "
                +         " WHERE l.isbn = ? AND l.estado = 'OCUPADO' ";//

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL)
        )
        {
            preparedStatement.setString(1,ISBN);//1 -> ? = primer parametro de la sentencia
            try(ResultSet resultSet = preparedStatement.executeQuery())//query -> pq es un select || si fuera otro seria update
            {
                if(resultSet.next())//1 alumno encontrado (solo 1 libro , no hace falta bucle)
                {

                    return  (Libro.builder()
                            .withISBN(resultSet.getString(1))//1 -> el 1º del select = expediente
                            .withTitulo(resultSet.getString(2))
                            .withEstado(resultSet.getString(3))
                            .build()) ;
                }
                else
                {
                    throw new LibroNoEncontradoException();
                }
            }

        }
    }

    //------------------------------------------------------------------

    public Prestamo selectPrestamoISBN(String ISBN) throws SQLException, PrestamoNoEncontradoException
    {
        //sentencia sql con paramentros -> cambiamos statment por prepareStatment
        final String SQL= "SELECT p.isbn, p.dni, p.fecha_prestamo, p.fecha_devolucion "
                +         " FROM prestamos p                                          "
                +         " WHERE p.isbn = ? AND p.fecha_devolucion IS NULL           ";

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL);

        )
        {
            preparedStatement.setString(1 ,ISBN);
            try(ResultSet resultSet = preparedStatement.executeQuery())//query -> pq es un select || si fuera otro seria update
            {
                if(resultSet.next())
                {
//el return Optional.of siempre distinto de null, si es null da NULLPOINTREXP
                    return  (Prestamo.builder()
                            .withISBN(resultSet.getString(1))//
                            .withDNI(resultSet.getString(2))
                            .withFechaPrestamo(resultSet.getTimestamp(3).toLocalDateTime()) //
                            //.withFechaDevolucion(resultSet.getTimestamp(4).toLocalDateTime()) //
                            .build());

                }
                else // no encontrado
                {
                    throw new PrestamoNoEncontradoException();
                }
            }

        }
    }

    //------------------------------------------------------------------

    public void updateFechaDevolucionPrestamo( String ISBN, LocalDateTime fechaHoraDevolucion ) throws  SQLException, PrestamoNoEncontradoException
    {
        final String SQL =   "UPDATE prestamos                     "
                           + "   SET  fecha_devolucion =  ?        "
                           + " WHERE  isbn = ?                     ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        )
        {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(fechaHoraDevolucion) );
            preparedStatement.setString(2, ISBN );

            int nRegistroAfectados = preparedStatement.executeUpdate();

            if ( nRegistroAfectados == 0 ) throw new PrestamoNoEncontradoException();
        }
    }

    //------------------------------------------------------------------

    public void updateEstadoLibroDevolucion( String ISBN ) throws SQLException, LibroNoEncontradoException
    {
        final String SQL = "UPDATE libros              "
                         + "   SET  estado = 'LIBRE' "
                         + " WHERE    isbn = ?         ";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        )
        {
            preparedStatement.setString(1, ISBN);

            int nRegistroAfectados = preparedStatement.executeUpdate();

            if ( nRegistroAfectados == 0 ) throw new LibroNoEncontradoException();
        }
    }

    //------------------------------------------------------------------

    public void updatePrestamosSocioDevolucion( String ISBN ) throws  SQLException, PrestamoNoEncontradoException
    {
        final String SQL =    "UPDATE socios                                                 "
                            + "   SET  nprestamo = nprestamo - 1                             "
                            + " WHERE  dni = (SELECT p.dni FROM prestamos p WHERE p.isbn = ?)";

        try(
                PreparedStatement preparedStatement = connection.prepareStatement(SQL);
        )
        {
            preparedStatement.setString(1, ISBN );

            int nRegistroAfectados = preparedStatement.executeUpdate();

            if ( nRegistroAfectados == 0 ) throw new PrestamoNoEncontradoException();
        }
    }

    @Override
    public void devolverLibro(String isbn, LocalDateTime fechaHoraDevolucion) throws SQLException, IsbnObligatorioException, LibroNoEncontradoException, PrestamoNoEncontradoException {

        selectLibroISBNOcupado(isbn);

        selectPrestamoISBN(isbn);

        updateFechaDevolucionPrestamo(isbn, fechaHoraDevolucion);

        updateEstadoLibroDevolucion(isbn);

        updatePrestamosSocioDevolucion(isbn);
    }

    //=====================================================================
    List <Socio> socios = new ArrayList<>();

    @Override
    public List<Socio> getSocios() throws SQLException {
        String SQL =    "SELECT s.nombre , s.email , s.direccion , s.dni , s.nprestamo "
                +       " FROM socios s                                                ";

        try(Statement statement = connection.createStatement(); //statment -> solo sentencias sql sin paramentros
            ResultSet resultSet = statement.executeQuery(SQL) //execute.query -> sentencia simple + result ----- execute.update -> demas
        )
        {
            //recorremos el data set con next , cdo next == false , fin

            if (resultSet.next())//primer registro
            {
                do//tratamiento de registro
                {


                    Socio socio = Socio.builder()
                            .withNombre(resultSet.getString(1))//1 -> el 1º del select = expediente
                            .withEmail(resultSet.getString(2))
                            .withDireccion(resultSet.getString(3)) //
                            .withDNI(resultSet.getString(4)) //
                            .withNPrestamo(resultSet.getInt(5)) //
                            .build();

                    socios.add(socio);

                } while (resultSet.next());
            }
            else //la consulta no obtiene ningun registro
            {

            }
        }
        return socios;
    }
    //------------------------------------------------------------------
    @Override
    public Socio getSocioByDni(String DNI) throws SQLException, SocioNoEncontradoException {
        //sentencia sql con paramentros -> cambiamos statment por prepareStatment
        final String SQL= "SELECT s.nombre, s.email, s.direccion, s.dni, s.nprestamo    "
                +         " FROM socios s                                                   "
                +         " WHERE s.dni = ?                                                 ";//? -> se sustituiran por le valor del param , solo valores   //no usar nunca ...'"+dni"'"

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL);

        )
        {
            preparedStatement.setString(1 ,DNI);//1 -> ? = cuarto parametro de la sentencia
            try(ResultSet resultSet = preparedStatement.executeQuery())//query -> pq es un select || si fuera otro seria update
            {
                if(resultSet.next())//1 alumno encontrado (solo 1 libro , no hace falta bucle)
                {
//el return Optional.of siempre distinto de null, si es null da NULLPOINTREXP
                    return  (Socio.builder()
                        .withNombre(resultSet.getString(1))//1 -> el 1º del select = expediente
                        .withEmail(resultSet.getString(2))
                        .withDireccion(resultSet.getString(3)) //
                        .withDNI(resultSet.getString(4)) //
                        .withNPrestamo(resultSet.getInt(5)) //
                        .build());

                    // ó ->local variable + return libro;
                }
                else // no encontrado
                {
                    throw new SocioNoEncontradoException();
                }
            }

        }
    }
    //------------------------------------------------------------------
    @Override
    public Socio insertarSocio(String nombre, String email, String direccion, String DNI, int nPrestamo) throws DireccionObligatorioException, DniDuplicadoException, DniObligatorioException, EmailDuplicadoException, EmailObligatorioException, NombreObligatorioException, NombreDuplicadoException, NumPrestamosRangoIncorrectoException, SQLException {
        final String SQL = "INSERT INTO socios(nombre, email, direccion, dni, nprestamo) "
                +          "           VALUES ( ?    , ?    , ?        , ?  ,    ?     ) ";

        try(PreparedStatement preparedStatement = connection.prepareStatement(SQL))
        {
            preparedStatement.setString(1,nombre);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,direccion);//
            preparedStatement.setString(4,DNI);//
            preparedStatement.setInt   (5,nPrestamo);//

            int count = preparedStatement.executeUpdate();// o hace falta pq no hay null, o se inserta o no

            //O se inserta o excepcion -- en el builder da igual el orden de las propiedades
                return Socio.builder()
                    .withNombre(nombre)
                    .withEmail(email)
                    .withDireccion(direccion)
                    .withDNI(DNI)
                    .withNPrestamo( nPrestamo )
                    .build();

        } catch (SQLException sqlException)
        {
            String message = sqlException.getMessage();
            if(message.contains("PK_SOCIOS")) throw new DniDuplicadoException();//si contiene "..." esto
            if(message.contains("NT_SOCIOS_DNI")) throw new DniObligatorioException();
            if(message.contains("NT_SOCIOS_NOMBRE")) throw new NombreObligatorioException();
            if(message.contains("NT_SOCIOS_EMAIL")) throw new EmailObligatorioException();
            if(message.contains("NT_SOCIOS_DIRECCION")) throw new DireccionObligatorioException();
            if(message.contains("UK_SOCIOS_NOMBRE")) throw new NombreDuplicadoException();
            if(message.contains("UK_SOCIOS_EMAIL")) throw new EmailDuplicadoException();
            if(message.contains("UK_SOCIOS_DNI")) throw new DniDuplicadoException();
            if(message.contains("CH_SOCIOS_NPRESTAMO")) throw new NumPrestamosRangoIncorrectoException();

            throw sqlException; //si no , esto

            //puedes hacer un message para cada constraint de la tabla
        }
    }


    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}
