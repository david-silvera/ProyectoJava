delimiter //
drop database parqueadero;//
create database Parqueadero;//
use Parqueadero;//

SET GLOBAL log_bin_trust_function_creators = 1;//

create table Persona( id INT primary key NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(50),apellido varchar(50),telefono varchar(12),correo varchar(50),direccion varchar(50));//
    
    
  create table Usuario( id INT primary key NOT NULL AUTO_INCREMENT,
      fkPersona INT NOT NULL, CONSTRAINT FOREIGN KEY(fkPersona) REFERENCES parqueadero.Persona(id)
    , contraseña varchar(15),  esAdmin boolean);//
  
create table Vehiculo( id INT primary key NOT NULL AUTO_INCREMENT,
    placa VARCHAR(6),tipoVehiculo varchar(30) );//
    
    CREATE TABLE Tarifa (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    tipoVehiculo VARCHAR(30) NOT NULL,
    tarifaBase DOUBLE NOT NULL
);

    
CREATE TABLE Transaccion_Independiente (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    fkVehiculo INT NOT NULL, CONSTRAINT FOREIGN KEY(fkVehiculo) REFERENCES Parqueadero.Vehiculo(id),
    fkTarifa INT NOT NULL, CONSTRAINT FOREIGN KEY(fkTarifa) REFERENCES Parqueadero.Tarifa(id),
    ingreso varchar(100),salida varchar(100),
    fechaEntrada DATETIME,
    fechaSalida DATETIME,
    metodoPago VARCHAR(20),
    totalPago DOUBLE
);
CREATE TABLE Incidente (
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    descripcion TEXT NOT NULL,
    fechaIncidente DATETIME,
    tipoIncidente VARCHAR(50),
    Usuario VARCHAR(50),
     montoMulta DOUBLE ,
	fkTransaccion INT NOT NULL,
    CONSTRAINT fk_incidente_transaccion FOREIGN KEY (fkTransaccion) REFERENCES Parqueadero.Transaccion_Independiente(id)
);

    
 INSERT INTO persona (nombre, apellido, telefono, correo, direccion) 
VALUES ('FRANK','MALDONADO', '3005484038', 'frankmaldonado@gmail.com','la paz'),
('DAVID','SILVERA', '3106352203', 'davidsilvera@gmail.com','la paz');    //

INSERT INTO usuario ( fkPersona , contraseña, esAdmin) 
VALUES (1, 'frank2006', true),(2, '12345', false);//

INSERT INTO Tarifa (tipoVehiculo, tarifaBase) VALUES
('Carro', 5000),
('Moto', 3000),
('Camión', 8000);//


CREATE PROCEDURE ValidarUsuario(
IN p_correo VARCHAR(50), IN p_contraseña VARCHAR(15), OUT p_resultado INT, OUT p_esAdmin INT  )
BEGIN
	SELECT IFNULL(
        (SELECT u.id FROM Usuario u 
         INNER JOIN Persona p ON p.id = u.fkPersona 
         WHERE p.correo = p_correo AND u.contraseña = p_contraseña), 0
    ) INTO p_resultado;

    IF p_resultado <> 0 THEN
        SELECT u.esAdmin INTO p_esAdmin FROM Usuario u WHERE u.id = p_resultado;
    ELSE
        SET p_esAdmin = 0; 
    END IF;
END;//

CREATE PROCEDURE ObtenerNombreUsuario( IN p_idUsuario INT, OUT p_nombreCompleto VARCHAR(100)
)
BEGIN
    SELECT CONCAT(p.nombre, ' ', p.apellido) INTO p_nombreCompleto
    FROM Usuario u
    INNER JOIN Persona p ON p.id = u.fkPersona
    WHERE u.id = p_idUsuario
    LIMIT 1;
END;//


CREATE PROCEDURE ObtenerDatosUsuario(p_idUsuario INT)
BEGIN
    SELECT p.nombre, p.apellido, p.telefono, p.correo, p.direccion, u.contraseña, u.fkPersona
    FROM Usuario u
    INNER JOIN Persona p ON p.id = u.fkPersona
    WHERE u.id = p_idUsuario;
END; //

CREATE PROCEDURE ActualizarPersona(IN p_id INT, IN p_nombre VARCHAR(50), IN p_apellido VARCHAR(50), IN p_telefono VARCHAR(12), IN p_correo VARCHAR(50), IN p_direccion VARCHAR(50))
BEGIN
    UPDATE Persona
    SET nombre = p_nombre,
        apellido = p_apellido,
        telefono = p_telefono,
        correo = p_correo,
        direccion = p_direccion
    WHERE id = p_id;
END;//


CREATE PROCEDURE ActualizarContraseña(IN p_id INT,IN p_contraseña VARCHAR(15))
BEGIN
    UPDATE Usuario
    SET contraseña = p_contraseña
    WHERE id = p_id;
END; //

CREATE PROCEDURE ObtenerTiposVehiculo()
BEGIN
    SELECT DISTINCT tipoVehiculo FROM Tarifa;
END; //

CREATE PROCEDURE ObtenerTarifaPorTipo(IN p_tipoVehiculo VARCHAR(30), OUT p_tarifa int)
BEGIN
    SET p_tarifa = 0;
    SELECT id INTO p_tarifa
    FROM Tarifa
    WHERE tipoVehiculo = p_tipoVehiculo
    LIMIT 1;
END; //

CREATE PROCEDURE ObtenerVehiculosConTransaccionActiva()
BEGIN
    SELECT 
        t.id AS idTransaccion,
        v.placa,
        v.tipoVehiculo
    FROM Vehiculo v
    INNER JOIN Transaccion_Independiente t ON v.id = t.fkVehiculo
    WHERE t.fechaSalida IS NULL;
END; //


CREATE PROCEDURE GestionarVehiculo( IN p_placa VARCHAR(6), IN p_tipoVehiculo VARCHAR(30), OUT p_idVehiculo INT)
BEGIN
    SET p_idVehiculo = 0;

    SELECT id INTO p_idVehiculo
    FROM Vehiculo
    WHERE placa = p_placa
    LIMIT 1;

    IF p_idVehiculo IS NULL OR p_idVehiculo = 0 THEN
        INSERT INTO Vehiculo (placa, tipoVehiculo) VALUES (p_placa, p_tipoVehiculo);
        SET p_idVehiculo = LAST_INSERT_ID();  
    END IF;
END;//

CREATE PROCEDURE verificarTransaccionActiva(IN p_placa VARCHAR(6))
BEGIN
    SELECT CASE 
        WHEN EXISTS (
            SELECT 1 FROM transaccion_independiente ti
            INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
            WHERE vi.placa = p_placa AND ti.fechaSalida IS NULL
        ) THEN 1 
        ELSE 0 
    END AS transaccionActiva;
END;

CREATE PROCEDURE registrarTransaccion(
    IN p_idVehiculo INT,
    IN p_idTarifa INT,
    IN p_metodoPago VARCHAR(20),
    IN p_totalPago DOUBLE,
    IN p_ingreso VARCHAR(100),
    IN p_salida VARCHAR(100)
)
BEGIN
    DECLARE v_fechaEntrada DATETIME;

    SET v_fechaEntrada = NOW();

    INSERT INTO transaccion_independiente (fkVehiculo, fkTarifa, fechaEntrada, fechaSalida, metodoPago, totalPago, ingreso, salida) 
    VALUES (p_idVehiculo, p_idTarifa, v_fechaEntrada, NULL, p_metodoPago, p_totalPago, p_ingreso, NULL);
END;//


CREATE PROCEDURE sacarVehiculo(IN p_placa VARCHAR(20), IN p_salida VARCHAR(100), OUT p_resultado INT)
BEGIN
    DECLARE v_transaccion_id INT;

    SELECT IFNULL(
        (SELECT ti.id 
         FROM transaccion_independiente ti
         INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id  
         WHERE vi.placa = p_placa AND ti.fechaSalida IS NULL 
         ORDER BY ti.fechaEntrada DESC  
         LIMIT 1), 
        0) 
    INTO v_transaccion_id;

    IF v_transaccion_id = 0 THEN
        SET p_resultado = 0; 
    ELSE
        UPDATE transaccion_independiente 
        SET fechaSalida = NOW(), salida = p_salida
        WHERE id = v_transaccion_id;

        SET p_resultado = v_transaccion_id; 
    END IF;
END; //

CREATE FUNCTION calcularPago(
    v_tarifaBase DOUBLE,
    v_fechaEntrada DATETIME,
    v_fechaSalida DATETIME
) RETURNS DOUBLE DETERMINISTIC
BEGIN
    DECLARE v_diferenciaHoras INT;
    DECLARE v_totalPago DOUBLE;

    SET v_diferenciaHoras = CEIL(TIMESTAMPDIFF(SECOND, v_fechaEntrada, v_fechaSalida) / 3600.0);

    IF v_diferenciaHoras <= 1 THEN
        SET v_totalPago = v_tarifaBase;
    ELSEIF v_diferenciaHoras = 2 THEN
        SET v_totalPago = v_tarifaBase + (v_tarifaBase / 2);
    ELSE
        SET v_totalPago = v_tarifaBase + ((v_tarifaBase / 2) * (v_diferenciaHoras - 1));
    END IF;

    RETURN v_totalPago;
END; //

CREATE PROCEDURE calcularTotalPagar(IN p_idTransaccion INT, OUT p_totalPago DOUBLE)
BEGIN
    DECLARE v_fechaEntrada DATETIME;
    DECLARE v_fechaSalida DATETIME;
    DECLARE v_tarifaBase DOUBLE;
    DECLARE v_pagoBase DOUBLE;
    DECLARE v_montoMultas DOUBLE DEFAULT 0;

    SELECT ti.fechaEntrada, ti.fechaSalida, ta.tarifaBase
    INTO v_fechaEntrada, v_fechaSalida, v_tarifaBase
    FROM transaccion_independiente ti
    INNER JOIN tarifa ta ON ti.fkTarifa = ta.id
    WHERE ti.id = p_idTransaccion;

    IF v_fechaEntrada IS NULL OR v_fechaSalida IS NULL THEN
        SET p_totalPago = -1; 
    ELSE

        SET v_pagoBase = calcularPago(v_tarifaBase, v_fechaEntrada, v_fechaSalida);

        SELECT IFNULL(SUM(montoMulta), 0)
        INTO v_montoMultas
        FROM incidente
        WHERE fkTransaccion = p_idTransaccion;

        SET p_totalPago = v_pagoBase + v_montoMultas;

        UPDATE transaccion_independiente 
        SET totalPago = p_totalPago 
        WHERE id = p_idTransaccion;
    END IF;
END; //

CREATE PROCEDURE obtenerTransaccionesPorUsuario(usuarioIngreso VARCHAR(50))
BEGIN
    SELECT 
        vi.placa AS 'Placa',
        ti.fechaEntrada AS 'Fecha de Entrada',
        ti.metodoPago AS 'Método de Pago',
        vi.tipoVehiculo AS 'Tipo de Vehículo'
    FROM transaccion_independiente ti
    INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
    WHERE ti.ingreso = usuarioIngreso
    ORDER BY ti.fechaEntrada DESC;
END;

CREATE PROCEDURE obtenerTodasTransacciones()
BEGIN
    SELECT 
        vi.placa AS 'Placa',
        vi.tipoVehiculo AS 'Tipo de Vehículo',
        ti.ingreso AS 'Ingresado por',
        ti.fechaEntrada AS 'Fecha de Entrada',
        ti.fechaSalida AS 'Fecha de Salida',
        ti.salida AS 'Retirado por',
        ti.metodoPago AS 'Método de Pago',
        ta.tarifaBase AS 'Tarifa',
        ti.totalPago AS 'Total a Pagar'
    FROM transaccion_independiente ti
    INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
    INNER JOIN tarifa ta ON ti.fkTarifa = ta.id;
END;//

CREATE FUNCTION calcularTotalRecaudado()
RETURNS DECIMAL(10,2)
DETERMINISTIC
BEGIN
    DECLARE total DECIMAL(10,2);
    
    SELECT SUM(ti.totalPago) INTO total
    FROM transaccion_independiente ti;
    
    RETURN total;
END;//

CREATE PROCEDURE obtenerTransaccionesPorFecha(
    IN fechaInicio DATE,
    IN fechaFin DATE
)
BEGIN
    SELECT 
        vi.placa AS 'Placa',
        vi.tipoVehiculo AS 'Tipo de Vehículo',
        ti.ingreso AS 'Ingresado por',
        ti.fechaEntrada AS 'Fecha de Entrada',
        ti.fechaSalida AS 'Fecha de Salida',
        ti.salida AS 'Retirado por',
        ti.metodoPago AS 'Método de Pago',
        ta.tarifaBase AS 'Tarifa',
        ti.totalPago AS 'Total a Pagar'
    FROM transaccion_independiente ti
    INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
    INNER JOIN tarifa ta ON ti.fkTarifa = ta.id
    WHERE DATE(ti.fechaEntrada) BETWEEN fechaInicio AND fechaFin
    AND ti.fechaSalida IS NOT NULL;
    
     SELECT 
        IFNULL(SUM(ti.totalPago), 0) AS TotalGanancias
    FROM transaccion_independiente ti
    WHERE DATE(ti.fechaEntrada) BETWEEN fechaInicio AND fechaFin
    AND ti.fechaSalida IS NOT NULL;
    
END;//

CREATE PROCEDURE ObtenerTransaccionesPorHora(
    IN horaInicio TIME,
    IN horaFinal TIME
)
BEGIN
    SELECT 
        vi.placa AS 'Placa',
        vi.tipoVehiculo AS 'Tipo de Vehículo',
        ti.ingreso AS 'Ingresado por',
        ti.fechaEntrada AS 'Fecha de Entrada',
        ti.fechaSalida AS 'Fecha de Salida',
        ti.salida AS 'Retirado por',
        ti.metodoPago AS 'Método de Pago',
        ta.tarifaBase AS 'Tarifa',
        ti.totalPago AS 'Total a Pagar'
    FROM transaccion_independiente ti
    INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
    INNER JOIN tarifa ta ON ti.fkTarifa = ta.id
    WHERE TIME(ti.fechaEntrada) BETWEEN horaInicio AND horaFinal;
END;//

CREATE PROCEDURE obtenerTransaccionesPorTipoVehiculo(IN tipo VARCHAR(50))
BEGIN
    SELECT 
        vi.placa AS 'Placa',
        vi.tipoVehiculo AS 'Tipo de Vehículo',
        ti.ingreso AS 'Ingresado por',
        ti.fechaEntrada AS 'Fecha de Entrada',
        ti.fechaSalida AS 'Fecha de Salida',
        ti.salida AS 'Retirado por',
        ti.metodoPago AS 'Método de Pago',
        ta.tarifaBase AS 'Tarifa',
        ti.totalPago AS 'Total a Pagar'
    FROM transaccion_independiente ti
    INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
    INNER JOIN tarifa ta ON ti.fkTarifa = ta.id
    WHERE vi.tipoVehiculo = tipo;
END; //

DELIMITER //

CREATE FUNCTION CalcularTiempoEstancia(entrada DATETIME) RETURNS VARCHAR(50)
DETERMINISTIC
BEGIN
    DECLARE tiempo VARCHAR(50);
    
    SET tiempo = CONCAT(
        TIMESTAMPDIFF(DAY, entrada, NOW()),' ', 
        TIME_FORMAT(SEC_TO_TIME(TIMESTAMPDIFF(SECOND, entrada, NOW()) % 86400), '%H:%i:%s')
    );

    RETURN tiempo;
END; //


CREATE PROCEDURE ObtenerVehiculosTransaccion(
    IN estado VARCHAR(10) 
)
BEGIN
    IF estado = 'dentro' THEN

        SELECT 
            vi.placa AS 'Placa',
            vi.tipoVehiculo AS 'Tipo de Vehículo',
            ti.ingreso AS 'Ingresado por',
            ti.fechaEntrada AS 'Fecha de Entrada',
            ti.metodoPago AS 'Método de Pago',
            ta.tarifaBase AS 'Tarifa',
            CalcularTiempoEstancia(ti.fechaEntrada) AS 'Tiempo de Estancia'
        FROM transaccion_independiente ti
        INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
        INNER JOIN tarifa ta ON ti.fkTarifa = ta.id
        WHERE ti.fechaSalida IS NULL;
        
    ELSEIF estado = 'fuera' THEN

        SELECT 
            vi.placa AS 'Placa',
            vi.tipoVehiculo AS 'Tipo de Vehículo',
            ti.ingreso AS 'Ingresado por',
            ti.fechaEntrada AS 'Fecha de Entrada',
            ti.fechaSalida AS 'Fecha de Salida',
            ti.salida AS 'Retirado por',
            ti.metodoPago AS 'Método de Pago',
            ta.tarifaBase AS 'Tarifa',
            ti.totalPago AS 'Total a Pagar'
        FROM transaccion_independiente ti
        INNER JOIN vehiculo vi ON ti.fkVehiculo = vi.id
        INNER JOIN tarifa ta ON ti.fkTarifa = ta.id
        WHERE ti.fechaSalida IS NOT NULL;
    END IF;
END; //


CREATE PROCEDURE actualizarTarifas(in p_precio double , in p_id int) BEGIN

	UPDATE tarifa t
	SET t.tarifaBase = p_precio
    WHERE t.id = p_id;

END;//
CREATE PROCEDURE obtenerPrecioTarifas(in p_tipoVehiculo VARCHAR(20) , OUT p_precio DOUBLE) BEGIN

	SET p_precio = 0;
    SELECT tarifaBase INTO p_precio
    FROM Tarifa
    WHERE tipoVehiculo = p_tipoVehiculo
    LIMIT 1;

END;//
CREATE PROCEDURE InsertarIncidente (
    IN p_idTransaccion INT,
    IN p_nombre VARCHAR(50),
    IN p_tipoIncidente VARCHAR(50),
    IN p_descripcion TEXT,
    IN p_monto DOUBLE
)
BEGIN
    INSERT INTO Incidente (descripcion, fechaIncidente, tipoIncidente,Usuario, montoMulta, fkTransaccion) VALUES 
    (p_descripcion,now(),p_tipoIncidente,p_nombre,p_monto,p_idTransaccion);
END; //

select * from persona;//
  select * from vehiculo ;//
    select * from transaccion_independiente ;//
    select * from usuario ;//
    select * from tarifa;//