/*-----------DROPS-------------*/
DROP TABLE libros;
DROP TABLE socios;
DROP TABLE prestamos;


/*==================LIBROS================================*/
DROP TABLE libros;
/*--------------------------------------------------------*/
CREATE TABLE libros
(
    isbn VARCHAR(12),
    titulo VARCHAR(100),
    estado VARCHAR(8) NOT NULL CHECK (estado IN ('LIBRE', 'OCUPADO')) DEFAULT 'LIBRE',

    CONSTRAINT "PK_LIBROS"          PRIMARY KEY (isbn),

    CONSTRAINT "UK_LIBROS_ISBN"     UNIQUE (isbn),

    CONSTRAINT "NT_LIBROS_ISBN"     CHECK (isbn     IS NOT NULL),
    CONSTRAINT "NT_LIBROS_TITULO"   CHECK (titulo   IS NOT NULL)
);
/*--------------------------------------------------------*/
INSERT INTO libros(isbn, titulo, estado)  values ('00000000000A','El Cantar de Mio Cid','OCUPADO');
INSERT INTO libros(isbn, titulo, estado)  values ('00000000000B','El Bestiario de Axlin','LIBRE');
INSERT INTO libros(isbn, titulo, estado)  values ('00000000000C','Un Café con Sal','OCUPADO');
INSERT INTO libros(isbn, titulo, estado)  values ('00000000000D','Lazarillo de Tormes','LIBRE');

/*========================================================*/

/*==================SOCIOS================================*/
DROP TABLE socios;
/*--------------------------------------------------------*/
CREATE TABLE socios
(
    nombre      VARCHAR(100),
    email       VARCHAR(100),
    direccion   VARCHAR(100),
    dni         VARCHAR(12),
    nprestamo   INTEGER,

    CONSTRAINT "PK_SOCIOS"                 PRIMARY KEY (dni),

    CONSTRAINT "NT_SOCIOS_DNI"          CHECK (dni          IS NOT NULL),
    CONSTRAINT "NT_SOCIOS_NOMBRE"       CHECK (nombre       IS NOT NULL),
    CONSTRAINT "NT_SOCIOS_EMAIL"        CHECK (email        IS NOT NULL),
    CONSTRAINT "NT_SOCIOS_DIRECCION"    CHECK (direccion    IS NOT NULL),

    CONSTRAINT "UK_SOCIOS_NOMBRE"       UNIQUE (nombre),
    CONSTRAINT "UK_SOCIOS_EMAIL"        UNIQUE (email),
    CONSTRAINT "UK_SOCIOS_DNI"          UNIQUE (dni),

    CONSTRAINT "CH_SOCIOS_NPRESTAMO"    CHECK (nprestamo BETWEEN 0 AND 5)

);
/*--------------------------------------------------------*/
INSERT INTO socios(nombre, email, direccion , dni, nprestamo)  values ('Juan Ramon','jr@gmail.com','Av/Los Cedros, P14, 2ºA' ,'12345678A', 2);
INSERT INTO socios(nombre, email, direccion , dni, nprestamo)  values ('Jorge Pascual','jp@gmail.com','C/El Pino, P13, 5ºS','12345678B',5);
INSERT INTO socios(nombre, email, direccion , dni, nprestamo)  values ('Angel Camara','ac@gmail.com','C/El Abedul, P1, 4ºC','12345678C',0);
INSERT INTO socios(nombre, email, direccion , dni, nprestamo)  values ('Manuel Muñoz','mm@gmail.com','C/El Cerezo, P10, 3ºN','12345678D',3);

/*=====================PESTAMOS===================================*/
DROP TABLE prestamos;
/*--------------------------------------------------------*/
CREATE TABLE prestamos
(
    isbn                VARCHAR(12),
    dni                 VARCHAR(12),
    fecha_prestamo      TIMESTAMP,
    fecha_devolucion    TIMESTAMP,

    CONSTRAINT "PK_PRESTAMOS" PRIMARY KEY (isbn,dni),

    CONSTRAINT "FK_PRESTAMOS_LIBROS" FOREIGN KEY (isbn) REFERENCES libros(isbn) ON UPDATE SET NULL /*si borro expt  cambia a null - cascade -> lo q halla debajo....*/,
    CONSTRAINT "FK_PRESTAMOS_SOCIOS" FOREIGN KEY (dni) REFERENCES socios(dni) ON UPDATE SET NULL,

    CONSTRAINT "NT_LIBROS_ISBN" CHECK (isbn IS NOT NULL),
    CONSTRAINT "NT_SOCIOS_DNI"  CHECK (dni  IS NOT NULL)

);
/*--------------------------------------------------------*/

CREATE SEQUENCE seq_prestamos MINVALUE 1 MAXVALUE 999999999 INCREMENT BY 1 START WITH 1 CYCLE;


/*--------------------------------------------------------*/

INSERT INTO prestamos(isbn, dni, fecha_prestamo , fecha_devolucion)  values ('00000000000A','12345678A','2022-11-11 20:00:00' , '2022-12-11 20:00:00');
INSERT INTO prestamos(isbn, dni, fecha_prestamo , fecha_devolucion)  values ('00000000000B','12345678B','2022-11-12 20:00:00' , '2022-12-12 20:00:00');
INSERT INTO prestamos(isbn, dni, fecha_prestamo , fecha_devolucion)  values ('00000000000C','12345678B','2022-11-13 20:00:00' , '2022-12-13 20:00:00');
INSERT INTO prestamos(isbn, dni, fecha_prestamo , fecha_devolucion)  values ('00000000000D','12345678C','2022-11-16 20:00:00' , '2022-12-14 20:00:00');
