
package clProyecto;

import java.sql.*;

public class dbConection {
    
     static String url="jdbc:mysql://localhost:3306/Parqueadero";
    static String user="Frank";
   static String pass="12345";
      public static Connection conectar()
    {
       Connection con=null;
       try
       {
       con=DriverManager.getConnection(url,user,pass);
           System.out.println("Conexión exitosa");
       }catch(SQLException e)
       {
        e.printStackTrace();
       }
       
       return con;
               
    }
   
}
