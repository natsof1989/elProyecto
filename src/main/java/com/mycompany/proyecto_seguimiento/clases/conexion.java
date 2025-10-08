package com.mycompany.proyecto_seguimiento.clases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class conexion {
    private String base; 
    private String host; 
    private String usuario; 
    private String password; 
    /*primero nos fuimos a java dependencies, pegamos la dependencia, luego a default packages y 
    a module info java y dps ya no me acuerdo lgmt pero ajam*/
    
    private Connection con; 

    
    //Hola Sofi, así me dijo copilot que haga para que no me de un error raro en CasoDAO:
    public Connection getConnection() {
        try {
            if (con == null || con.isClosed()) {
                String url = "jdbc:mysql://" + host + "/" + base + "?useSSL=false&serverTimezone=UTC";
                con = DriverManager.getConnection(url, usuario, password);
                System.out.println("Conectado a la base de datos");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("No Conectado: " + ex.getMessage());
        }
        return con;
    }
    /*public Connection getConnection() {
        try {
            String url= "jdbc:mysql://"+host+"/"+base;//direccion de BD
            con = DriverManager.getConnection(url, this.usuario, this.password);
            System.out.println("Conectado");
        } catch (SQLException ex) {
            Logger.getLogger(conexion.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("No Conectado");
        }
        return con;
        
    }*/
    
    
   public conexion() {
        this.base = "seguimiento";
        this.host = "192.168.100.11:3306";
        this.usuario = "info";
        this.password = "Infoctn024";


    }


    public conexion(String base, String host, String usuario, String password) {
        this.base = base;
        this.host = host;
        this.usuario = usuario;
        this.password = password;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
