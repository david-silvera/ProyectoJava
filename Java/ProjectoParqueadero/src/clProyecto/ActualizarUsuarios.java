/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clProyecto;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author David
 */
public class ActualizarUsuarios {

    public ActualizarUsuarios() {
    }
    
    public void ActualizarPersona(int id , String nombre,String apellido,String telefono,String correo,String direccion){
    
            
                  try{
                         Connection db = dbConection.conectar();
                        CallableStatement cs = db.prepareCall("CALL ActualizarPersona(?, ?, ?, ?, ?, ?)");
                        cs.setInt(1, id);
                        cs.setString(2, nombre);
                        cs.setString(3, apellido);
                        cs.setString(4, telefono);
                        cs.setString(5, correo);
                        cs.setString(6, direccion);
                        cs.executeUpdate();

        
                }catch(SQLException ex){
                    ex.printStackTrace();
                }    

    }
    
    public void ActualizarContraseña(int id,String contraseña){
        
        
    try (Connection db = dbConection.conectar();) {
        
        CallableStatement cs = db.prepareCall("CALL ActualizarContraseña(?, ?)");
        cs.setInt(1, id);
        cs.setString(2, contraseña);
        cs.executeUpdate();
        
        JOptionPane.showMessageDialog(null, "¡Datos Actualizados!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } catch (SQLException ex) {


       
    }
    }
    
}
