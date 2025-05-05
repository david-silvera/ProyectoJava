
package clProyecto;

import java.sql.*;

public class dbConection {
    
     static String url="jdbc:mysql://localhost:3306/Parqueadero";
    static String user="root";
   static String pass="2006frank";
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
