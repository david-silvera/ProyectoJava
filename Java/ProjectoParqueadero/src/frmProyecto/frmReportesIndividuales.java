/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package frmProyecto;

import clProyecto.dbConection;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author David
 */
public class frmReportesIndividuales extends javax.swing.JFrame {

    int idUsuario,ocupacion=0;
    double total=0;
    String fechaIngresada="",fechaLimite="";
     String[] DF={"dentro","fuera"};
    /**
     * Creates new form frmReportesIndividuales
     */
    public frmReportesIndividuales(int id) {
        initComponents();
        
        idUsuario=id;
        comboHoraInicio.setEnabled(false);
        comboHoraFinal.setEnabled(false);
        comboDentroFuera.setEnabled(false);
        lbOcupacion.setVisible(false);
        lbTitulo.setVisible(false);
        comboTipo.setEnabled(false);
        comboDentroFuera.setEnabled(false);
        calendarFechaInicio.setEnabled(false);
        calendarFechaFin.setEnabled(false);
        btnBuscar.setEnabled(false);
        lbOcupacion.setText("0");
        lbTotal.setText("0.0");
    }
    
    
    public void llenarComboDf(){
    
        comboDentroFuera.removeAllItems();
         comboDentroFuera.addItem("Seleccione...");
         
          for (int i = 0; i < DF.length; i++) {
            comboDentroFuera.addItem(DF[i]);
            
        }
    }
    
      public void llenarComboTipo() {
    comboTipo.removeAllItems();
    comboTipo.addItem("Seleccione...");

    try {
        Connection db = dbConection.conectar();
        CallableStatement cs = db.prepareCall("CALL ObtenerTiposVehiculo()");
        ResultSet rs = cs.executeQuery();

        while (rs.next()) {
            comboTipo.addItem(rs.getString("tipoVehiculo"));
        }

        rs.close();
        cs.close();
        db.close();

    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}
      

public void mostrarVehiculos() { ocupacion=0; total=0; 
if (comboDentroFuera.getSelectedIndex()>0){ 
    String query = "CALL ObtenerVehiculosTransaccion(?)";
    try {
    Connection db = dbConection.conectar(); 
    CallableStatement cs = db.prepareCall(query);
    
    cs.setString(1, comboDentroFuera.getSelectedItem().toString());
    
    ResultSet rs = cs.executeQuery(); 
    
    ResultSetMetaData metaData = rs.getMetaData(); 
    
    int columnCount = metaData.getColumnCount(); 
    
    String[] encabezado = new String[columnCount]; for (int i = 1; i <= columnCount; i++) 
    { encabezado[i - 1] = metaData.getColumnLabel(i); }
    
    DefaultTableModel modelo = new DefaultTableModel (); modelo.setColumnIdentifiers(encabezado);
    while(rs.next())
    { 
        
    if(comboDentroFuera.getSelectedItem().toString().equals("dentro")) {
        ocupacion++; 
        modelo.addRow(new Object[]{ rs.getString("Placa"),
            rs.getString("Tipo de Vehículo"),
            rs.getString("Ingresado por"),
            rs.getString("Fecha de Entrada"),
            rs.getString("Método de Pago"),
            rs.getDouble("Tarifa"),
            rs.getString("Tiempo de Estancia") }); 
    } else if(comboDentroFuera.getSelectedItem().toString().equals("fuera")) { 
        total += rs.getDouble("total a pagar");
        modelo.addRow(new Object[]{ rs.getString("Placa"),
            rs.getString("Tipo de Vehículo"),
            rs.getString("Ingresado por"),
            rs.getString("Fecha de Entrada"),
            rs.getString("Fecha de Salida"),
            rs.getString("Retirado por"),
            rs.getString("Método de Pago"),
            rs.getDouble("Tarifa"),
            rs.getDouble("Total a Pagar") }); 
        }
    } 
    tableReporte.setModel(modelo);
    lbOcupacion.setText("" + ocupacion);
    lbTotal.setText("$" + Double.toString(total));
    } catch(SQLException ex){
        ex.printStackTrace(); 
    } 
    }else{ 
    
    }

}

     public void mostrarInfoTipo(){
      total = 0;
    ocupacion = 0;
    
    if(comboTipo.getSelectedIndex() > 0){

        String query = "CALL obtenerTransaccionesPorTipoVehiculo(?)";

        try {
            Connection db = dbConection.conectar();
            CallableStatement cs = db.prepareCall(query);
            
            cs.setString(1, comboTipo.getSelectedItem().toString());
            
            ResultSet rs = cs.executeQuery();
            
               ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String[] encabezado = new String[columnCount];
                    
                     for (int i = 1; i <= columnCount; i++) {
                        encabezado[i - 1] = metaData.getColumnLabel(i);
                    }
                    DefaultTableModel modelo = new DefaultTableModel ();
                    modelo.setColumnIdentifiers(encabezado);

            
            while(rs.next()){
                ocupacion++;
                total += rs.getDouble("total a pagar");
                
                modelo.addRow(new Object[]{
                    rs.getString("Placa"),
                    rs.getString("Tipo de Vehículo"),
                    rs.getString("Ingresado por"),
                    rs.getString("Fecha de Entrada"),
                    rs.getString("Fecha de Salida"),
                    rs.getString("Retirado por"),
                    rs.getString("Método de Pago"),
                    rs.getDouble("Tarifa"),
                    rs.getDouble("Total a Pagar")
                });
            }
            
            tableReporte.setModel(modelo);
            lbOcupacion.setText("" + ocupacion);
            lbTotal.setText("$" + Double.toString(total));
            
        } catch(SQLException ex){
            ex.printStackTrace();
        }
    }
     }
    
    public void mostrarInfoHora(){
    
        if(comboHoraInicio.getSelectedIndex()>0 && comboHoraFinal.getSelectedIndex()>0 ){
            ocupacion=0;
             total=0;   
               fechaIngresada=comboHoraInicio.getSelectedItem().toString()+":"+"00"+":"+"00";
               fechaLimite=comboHoraFinal.getSelectedItem().toString()+":"+"00"+":"+"00";
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
               LocalTime fechaActual = LocalTime.parse(fechaIngresada,formatter);
              LocalTime fecha2 = LocalTime.parse(fechaLimite,formatter);;
              if(fecha2.isAfter(fechaActual)){
                String fechaInicial=fechaActual.format(formatter);
                 String fechaFinal=fecha2.format(formatter);
                 
                  String query = "CALL ObtenerTransaccionesPorHora(?, ?)";

                try { 
                    Connection db = dbConection.conectar();
                    CallableStatement cs = db.prepareCall(query);
                    cs.setString(1, fechaInicial);
                    cs.setString(2, fechaFinal);
                    ResultSet rs = cs.executeQuery();
                    
                     ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    String[] encabezado = new String[columnCount];
                    
                     for (int i = 1; i <= columnCount; i++) {
                        encabezado[i - 1] = metaData.getColumnLabel(i);
                    }
                    DefaultTableModel modelo = new DefaultTableModel ();
                    modelo.setColumnIdentifiers(encabezado);

                    while(rs.next()){
                        ocupacion++;
                        total += rs.getDouble("Total a Pagar");
                        modelo.addRow(new Object[]{
                            rs.getString("Placa"),
                            rs.getString("Tipo de Vehículo"),
                            rs.getString("Ingresado por"),
                            rs.getString("Fecha de Entrada"),
                            rs.getString("Fecha de Salida"),
                            rs.getString("Retirado por"),
                            rs.getString("Método de Pago"),
                            rs.getDouble("Tarifa"),
                            rs.getDouble("Total a Pagar")
                        });
                    }
                    tableReporte.setModel(modelo);
                    lbOcupacion.setText("" + ocupacion);
                    lbTotal.setText("$" + Double.toString(total));
                } catch(SQLException ex){
                    ex.printStackTrace();
                }
         }else{
            
            JOptionPane.showMessageDialog(rootPane, "Ingrese una hora final mayor");
        
        }
      }
    }
    
   public void validarFechas() {
    if (calendarFechaInicio.getDate() == null || calendarFechaFin.getDate() == null) {
        JOptionPane.showMessageDialog(rootPane, "Seleccione ambas fechas antes de continuar.");
        
    }else{

    if (calendarFechaFin.getDate().after(calendarFechaInicio.getDate())) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        String fechaIngresadaStr = formato.format(calendarFechaInicio.getDate());
        String fechaLimiteStr = formato.format(calendarFechaFin.getDate());


        mostrarInfoFecha(fechaIngresadaStr, fechaLimiteStr);
    } else {
        JOptionPane.showMessageDialog(rootPane, "Ingrese una fecha final mayor.");
    }
    }
   }
public void mostrarInfoFecha(String fechaInicioStr, String fechaFinStr) {
       ocupacion=0;
   String query = "{CALL obtenerTransaccionesPorFecha(?, ?)}";

try (Connection db = dbConection.conectar();
     CallableStatement cs = db.prepareCall(query)) {

    cs.setDate(1, Date.valueOf(fechaInicioStr));
    cs.setDate(2, Date.valueOf(fechaFinStr));

    boolean hasResults = cs.execute();

    if (hasResults) {
        ResultSet rs = cs.getResultSet();
        DefaultTableModel modelo = new DefaultTableModel();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            modelo.addColumn(metaData.getColumnLabel(i));
        }

        while (rs.next()) {
            ocupacion++;
            Object[] fila = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                fila[i - 1] = rs.getObject(i);
            }
            modelo.addRow(fila);
        }
        lbOcupacion.setText(""+ocupacion);
        tableReporte.setModel(modelo);

        if (cs.getMoreResults()) {
            ResultSet rsGanancias = cs.getResultSet();
            if (rsGanancias.next()) {
                double totalGanancias = rsGanancias.getDouble("TotalGanancias");
                lbTotal.setText("$" + totalGanancias);
            }
        }
    }

} catch (SQLException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error al obtener las transacciones y ganancias: " + ex.getMessage());
}
}
     
   public void mostrarInfo() {
        total=0;
        obtenerTotalRecaudado();
    DefaultTableModel modelo = new DefaultTableModel();
    String query = "CALL obtenerTodasTransacciones()";

    try (Connection db = dbConection.conectar();
         CallableStatement cs = db.prepareCall(query)) {
        
        ResultSet rs = cs.executeQuery();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            modelo.addColumn(metaData.getColumnLabel(i));
        }

        while (rs.next()) {
            Object[] fila = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                fila[i - 1] = rs.getObject(i);
            }
            modelo.addRow(fila);
        }
         lbTotal.setText("$"+Double.toString(total));
        tableReporte.setModel(modelo);

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al obtener las transacciones: " + ex.getMessage());
    }  
}
   public void obtenerTotalRecaudado() {
    String query = "SELECT calcularTotalRecaudado()";

    try (Connection db = dbConection.conectar();
         PreparedStatement ps = db.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {
        
        if (rs.next()) {
            BigDecimal total = rs.getBigDecimal(1);
            this.total=Double.parseDouble(total.toString());
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al obtener el total recaudado: " + ex.getMessage());
    }  
}
    
    
    
    
   
   
     public void llenarComboHora(){
     comboHoraInicio.removeAllItems();
      comboHoraFinal.removeAllItems();
      comboHoraInicio.addItem("Seleccione una hora inicial...");
      comboHoraFinal.addItem("Seleccione una hora final...");
      for (int i = 0; i <24; i++) {
                       String hora = String.format("%02d", i);
                       comboHoraInicio.addItem(hora);
                       comboHoraFinal.addItem(hora);
             }
     
     }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lbTotal = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tableReporte = new javax.swing.JTable();
        btnSalir = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        rabtnGanacias = new javax.swing.JRadioButton();
        rabtnHistorial = new javax.swing.JRadioButton();
        rabtnVehiculoTipo = new javax.swing.JRadioButton();
        rabtnHorarios = new javax.swing.JRadioButton();
        comboDentroFuera = new javax.swing.JComboBox<>();
        rbtnDentroFuera = new javax.swing.JRadioButton();
        comboHoraInicio = new javax.swing.JComboBox<>();
        comboHoraFinal = new javax.swing.JComboBox<>();
        lbTitulo = new javax.swing.JLabel();
        lbOcupacion = new javax.swing.JLabel();
        comboTipo = new javax.swing.JComboBox<>();
        calendarFechaFin = new com.toedter.calendar.JDateChooser();
        calendarFechaInicio = new com.toedter.calendar.JDateChooser();
        btnBuscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lbTotal.setText("jLabel3");

        tableReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "None"
            }
        ));
        jScrollPane1.setViewportView(tableReporte);

        btnSalir.setText("Salir");
        btnSalir.setVerifyInputWhenFocusTarget(false);
        btnSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalirActionPerformed(evt);
            }
        });

        jLabel2.setText("Total de Ganancias :");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel1.setText("Que reporte quiere ver");

        buttonGroup1.add(rabtnGanacias);
        rabtnGanacias.setText("Ganancias");
        rabtnGanacias.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rabtnGanaciasActionPerformed(evt);
            }
        });

        buttonGroup1.add(rabtnHistorial);
        rabtnHistorial.setText("Historial");
        rabtnHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rabtnHistorialActionPerformed(evt);
            }
        });

        buttonGroup1.add(rabtnVehiculoTipo);
        rabtnVehiculoTipo.setText("Vahiculos ");
        rabtnVehiculoTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rabtnVehiculoTipoActionPerformed(evt);
            }
        });

        buttonGroup1.add(rabtnHorarios);
        rabtnHorarios.setText("Hora de ocupacion");
        rabtnHorarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rabtnHorariosActionPerformed(evt);
            }
        });

        comboDentroFuera.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboDentroFuera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboDentroFueraActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtnDentroFuera);
        rbtnDentroFuera.setText("Dentro/Fuera");
        rbtnDentroFuera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnDentroFueraActionPerformed(evt);
            }
        });

        comboHoraInicio.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        comboHoraFinal.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboHoraFinal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboHoraFinalActionPerformed(evt);
            }
        });

        lbTitulo.setText("Ocupacion del Parquedero :");

        lbOcupacion.setText("jLabel3");

        comboTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboTipo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboTipoActionPerformed(evt);
            }
        });

        btnBuscar.setText("Buscar");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbOcupacion, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30))
            .addGroup(layout.createSequentialGroup()
                .addGap(56, 56, 56)
                .addComponent(rabtnHistorial, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(274, 274, 274)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnBuscar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(calendarFechaFin, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                    .addComponent(calendarFechaInicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(16, 16, 16)
                                    .addComponent(rabtnGanacias, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(103, 103, 103)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(comboHoraInicio, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(comboHoraFinal, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(rabtnHorarios))
                        .addGap(109, 109, 109)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(comboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rabtnVehiculoTipo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(85, 85, 85)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtnDentroFuera, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(comboDentroFuera, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(58, 58, 58)
                        .addComponent(btnSalir, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnSalir, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(rbtnDentroFuera)
                                .addGap(18, 18, 18)
                                .addComponent(comboDentroFuera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(rabtnHorarios)
                                        .addComponent(rabtnGanacias))
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(comboHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(calendarFechaInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(rabtnVehiculoTipo)
                                    .addGap(27, 27, 27)
                                    .addComponent(comboTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(calendarFechaFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(comboHoraFinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(rabtnHistorial, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscar)
                .addGap(21, 21, 21)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbTotal)
                    .addComponent(lbTitulo)
                    .addComponent(lbOcupacion))
                .addGap(24, 24, 24))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalirActionPerformed
        // TODO add your handling code here:
       

                frmAdmin fmadmin = new  frmAdmin(idUsuario);
                fmadmin.setVisible(true);

                dispose();
        
    }//GEN-LAST:event_btnSalirActionPerformed

    private void rabtnGanaciasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rabtnGanaciasActionPerformed
        // TODO add your handling code here:
         comboDentroFuera.setEnabled(false);
        comboHoraInicio.setEnabled(false);
        comboHoraFinal.setEnabled(false);
        comboDentroFuera.setEnabled(false);
        calendarFechaInicio.setEnabled(true);
         btnBuscar.setEnabled(true);
        calendarFechaFin.setEnabled(true);
        lbOcupacion.setVisible(true);
        lbTitulo.setVisible(true);
        lbTotal.setText("0.0");
        lbOcupacion.setText("0");
    }//GEN-LAST:event_rabtnGanaciasActionPerformed

    private void rabtnHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rabtnHistorialActionPerformed
        // TODO add your handling code here:
        try{
            lbTotal.setText("0.0");
            mostrarInfo();
     calendarFechaInicio.setEnabled(false);
        calendarFechaFin.setEnabled(false);
             comboHoraInicio.setEnabled(false);
             comboHoraFinal.setEnabled(false);
            comboDentroFuera.setEnabled(false);
              lbOcupacion.setVisible(false);
              lbTitulo.setVisible(false);
               btnBuscar.setEnabled(false);
               comboDentroFuera.setEnabled(false);
             lbOcupacion.setText("0");
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Hubo un error");
        }
    }//GEN-LAST:event_rabtnHistorialActionPerformed

    private void rabtnHorariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rabtnHorariosActionPerformed
        // TODO add your handling code here:
       
         try{
            lbTotal.setText("0.0");
            llenarComboHora();
            comboHoraInicio.setEnabled(true);
            comboHoraFinal.setEnabled(true);
            comboDentroFuera.setEnabled(false);
              lbOcupacion.setVisible(true);
               btnBuscar.setEnabled(false);
              lbTitulo.setVisible(true);
               comboDentroFuera.setEnabled(false);
          calendarFechaInicio.setEnabled(false);
        calendarFechaFin.setEnabled(false);
             lbOcupacion.setText("0");
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Hubo un error");
        }
    }//GEN-LAST:event_rabtnHorariosActionPerformed

    private void comboHoraFinalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboHoraFinalActionPerformed
        // TODO add your handling code here:
        mostrarInfoHora();
    }//GEN-LAST:event_comboHoraFinalActionPerformed

    private void rabtnVehiculoTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rabtnVehiculoTipoActionPerformed
        // TODO add your handling code here:
        try{
            lbTotal.setText("0.0");
            llenarComboTipo();
            comboHoraInicio.setEnabled(false);
            comboHoraFinal.setEnabled(false);
            comboDentroFuera.setEnabled(false);
              lbOcupacion.setVisible(true);
               btnBuscar.setEnabled(false);
              lbTitulo.setVisible(true);
              comboTipo.setEnabled(true);
               comboDentroFuera.setEnabled(false);
         calendarFechaInicio.setEnabled(false);
        calendarFechaFin.setEnabled(false);
             lbOcupacion.setText("0");
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Hubo un error");
        }
        
    }//GEN-LAST:event_rabtnVehiculoTipoActionPerformed

    private void comboTipoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboTipoActionPerformed
        // TODO add your handling code here:
        mostrarInfoTipo();
    }//GEN-LAST:event_comboTipoActionPerformed

    private void rbtnDentroFueraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnDentroFueraActionPerformed
        // TODO add your handling code here:
        try{
            lbTotal.setText("0.0");
            llenarComboDf();
            comboHoraInicio.setEnabled(false);
            comboHoraFinal.setEnabled(false);
            comboDentroFuera.setEnabled(false);
              lbOcupacion.setVisible(true);
              lbTitulo.setVisible(true);
              comboTipo.setEnabled(false);
               comboDentroFuera.setEnabled(true);
                btnBuscar.setEnabled(false);
             calendarFechaInicio.setEnabled(false);
              calendarFechaFin.setEnabled(false);
             lbOcupacion.setText("0");
        }catch(Exception e){
            JOptionPane.showMessageDialog(rootPane, "Hubo un error");
        }
    }//GEN-LAST:event_rbtnDentroFueraActionPerformed

    private void comboDentroFueraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboDentroFueraActionPerformed
        // TODO add your handling code here:
        mostrarVehiculos();
       
  
    }//GEN-LAST:event_comboDentroFueraActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        // TODO add your handling code here:
        validarFechas();
    }//GEN-LAST:event_btnBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnSalir;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser calendarFechaFin;
    private com.toedter.calendar.JDateChooser calendarFechaInicio;
    private javax.swing.JComboBox<String> comboDentroFuera;
    private javax.swing.JComboBox<String> comboHoraFinal;
    private javax.swing.JComboBox<String> comboHoraInicio;
    private javax.swing.JComboBox<String> comboTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbOcupacion;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JLabel lbTotal;
    private javax.swing.JRadioButton rabtnGanacias;
    private javax.swing.JRadioButton rabtnHistorial;
    private javax.swing.JRadioButton rabtnHorarios;
    private javax.swing.JRadioButton rabtnVehiculoTipo;
    private javax.swing.JRadioButton rbtnDentroFuera;
    private javax.swing.JTable tableReporte;
    // End of variables declaration//GEN-END:variables
}
