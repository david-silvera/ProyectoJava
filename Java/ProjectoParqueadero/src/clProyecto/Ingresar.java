/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clProyecto;

import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import java.util.Date;
/**
 *
 * @author David
 */
public class Ingresar {
    
    int idVehiculo,idPersona=0;
    String placa;

    public Ingresar() {
        
        
    }
    
        public void ingresarPersona(String nombre ,String apellido,String telefono,String correo,String direccion ){
            String query = "select ifnull((SELECT id FROM persona WHERE nombre = ? AND apellido = ?  LIMIT 1), 0) as resultado ;";
             try  { 
                Connection db = dbConection.conectar();
                PreparedStatement ps = db.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

               ps.setString(1, nombre);
               ps.setString(2, apellido);

               ResultSet resultSet = ps.executeQuery();

               if (resultSet.next()) {
                
                 idPersona=resultSet.getInt("resultado");
               
                if (idPersona >0) {
                   
                    System.out.println("Persona existente");
                   
                } else {
                 
                     String query2="insert into Persona ( nombre , apellido, telefono, correo , direccion ) values(?,?,?,?,?)";
                     try{
                         PreparedStatement psI = db.prepareStatement(query2, PreparedStatement.RETURN_GENERATED_KEYS);
                        psI.setString(1, nombre);
                        psI.setString(2, apellido);
                        psI.setString(3, telefono);
                        psI.setString(4, correo);
                        psI.setString(5, direccion);

                        psI.executeUpdate();
                        ResultSet rs = psI.getGeneratedKeys();
                        if (rs.next()) {
                             idPersona = rs.getInt(1); 
                        }
                        
                          
                }catch(SQLException ex){
                    ex.printStackTrace();
                }    
              }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
     
    
    }
    
    
    public void igresarVehiculo(String placa ,String tipoVehiculo){
    
        try {
        Connection db = dbConection.conectar();
        CallableStatement cs = db.prepareCall("CALL GestionarVehiculo(?, ?, ?)");

        cs.setString(1, placa);
        cs.setString(2, tipoVehiculo);
        cs.registerOutParameter(3, Types.INTEGER); 

        cs.execute();

        idVehiculo = cs.getInt(3);  

        cs.close();
        db.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    }
    
public void transacciones(String placa, int tarifa, String metodoPago, double totalPago, String ingreso, String salida) {
    String verificarQuery = "CALL verificarTransaccionActiva(?)";
    String registrarQuery = "CALL registrarTransaccion(?, ?, ?, ?, ?, ?)";

    try (Connection db = dbConection.conectar();
         CallableStatement verificarStmt = db.prepareCall(verificarQuery)) {


        verificarStmt.setString(1, placa);
        ResultSet rs = verificarStmt.executeQuery();

        if (rs.next() && rs.getInt("transaccionActiva") == 1) {
            JOptionPane.showMessageDialog(null, "El vehículo se encuentra dentro del parqueadero");
            
        }else{


        try (CallableStatement registrarStmt = db.prepareCall(registrarQuery)) {
            registrarStmt.setInt(1, idVehiculo);
            registrarStmt.setInt(2, tarifa);
            registrarStmt.setString(3, metodoPago);
            registrarStmt.setDouble(4, totalPago);
            registrarStmt.setString(5, ingreso);
            registrarStmt.setString(6, salida);

            int affectedRows = registrarStmt.executeUpdate();

            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(null, "Vehiculo registrado correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar la transacción.");
            }
        }
    }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error en la transacción: " + e.getMessage());
        e.printStackTrace();
    }
}
public void igresarIncidente(String descripcion ,String tipoIncidente,String Usuario,int idTransaccion, double monto){
    
        try {
             Connection db = dbConection.conectar();
            CallableStatement cs = db.prepareCall("{CALL InsertarIncidente(?, ?, ?, ?, ?)}");

            cs.setInt(1, idTransaccion);
            cs.setString(2, Usuario);
            cs.setString(3, tipoIncidente);
            cs.setString(4, descripcion);
            cs.setDouble(5, monto);

            cs.execute();
            cs.close();
            db.close();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    }

}
