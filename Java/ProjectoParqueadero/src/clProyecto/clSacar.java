/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clProyecto;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class clSacar {

    int idTransaccion;
    String fechaSalida;
    public clSacar() {
    }
    
    public void sacarVehiculo(String placa, String salida) {
    String query = "CALL sacarVehiculo(?, ?, ?)";

    try (Connection db = dbConection.conectar();
         CallableStatement cs = db.prepareCall(query)) {

        cs.setString(1, placa);
        cs.setString(2, salida);
        cs.registerOutParameter(3, java.sql.Types.INTEGER); 

        cs.executeUpdate();

        int resultado = cs.getInt(3);
        
        idTransaccion=resultado;

        if (resultado == 0) {
            JOptionPane.showMessageDialog(null, "No se encontró el vehículo.");
        } else {
            JOptionPane.showMessageDialog(null, "Vehículo sacado correctamente.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error en la transacción: " + e.getMessage());
        e.printStackTrace();
    }
}
    
   public void totalPagar() {
   String query = "CALL calcularTotalPagar(?, ?)";
    try (Connection db = dbConection.conectar();
         CallableStatement cs = db.prepareCall(query)) {
        
       
       cs.setInt(1, idTransaccion);
        cs.setDouble(2, java.sql.Types.DOUBLE); 

        cs.executeUpdate();

        double totalPagar = cs.getDouble(2); 

        if (totalPagar == -1) {
            System.out.println("Fechas no válidas para la transacción.");
           
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error en la consulta SQL: " + e.getMessage());
        e.printStackTrace();
      
    }
}
    
}
