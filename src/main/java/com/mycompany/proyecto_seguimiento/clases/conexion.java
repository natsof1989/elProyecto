package com.mycompany.proyecto_seguimiento.clases;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase para gestionar la conexión a la base de datos.
 * Permite la configuración dinámica de los parámetros de conexión mediante un archivo config.properties.
 * Compatible con los controllers y DAOs del sistema previo.
 */
public class conexion {

    private static final String CONFIG_FILE = "config1.properties";
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    // Bloque estático: carga la configuración al iniciar la clase
    static {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            URL = props.getProperty("url", "jdbc:mysql://127.0.0.1:3306/seguimiento?useSSL=false&serverTimezone=UTC");
            USER = props.getProperty("usuario", "root");
            PASSWORD = props.getProperty("contrasena", "");
        } catch (IOException e) {
            // Si no existe el archivo, usar valores por defecto
            URL = "jdbc:mysql://127.0.0.1:3306/seguimiento?useSSL=false&serverTimezone=UTC";
            USER = "root";
            PASSWORD = "";
        }
    }

    /**
     * Obtiene una nueva conexión a la base de datos usando los parámetros actuales.
     * Este método puede ser usado por los controladores y DAOs sin modificar su lógica.
     * @return Connection activa a la base de datos
     */
    public static Connection getConnection() {
        try {
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conectado a la base de datos");
            return con;
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println("No Conectado: " + ex.getMessage());
            return null;
        }
    }

    /**
     * Guarda la configuración de la conexión a la base de datos en config.properties.
     * También actualiza los valores estáticos para futuras conexiones en la misma ejecución.
     *
     * @param url        URL completa JDBC de la base de datos
     * @param usuario    Usuario de la base de datos
     * @param contrasena Contraseña de la base de datos
     */
    public static void guardarConfiguracion(String url, String usuario, String contrasena) {
        Properties props = new Properties();
        props.setProperty("url", url);
        props.setProperty("usuario", usuario);
        props.setProperty("contrasena", contrasena != null ? contrasena : "");

        try (OutputStream os = new FileOutputStream(CONFIG_FILE)) {
            props.store(os, "Configuración de la conexión a la base de datos");
            // Actualiza los valores en memoria
            URL = url;
            USER = usuario;
            PASSWORD = contrasena != null ? contrasena : "";
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
 
       
   public conexion() {
        this.base = "seguimiento";
        this.host = "192.168.100.11:3306";
        this.usuario = "info";
        this.password = "Infoctn024";


    }

    // Getters para los valores actuales (opcional, por si los necesitas)
    public static String getUrl() {
        return URL;
    }

    public static String getUsuario() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}