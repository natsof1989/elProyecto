package com.mycompany.proyecto_seguimiento.clases;

import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import com.mycompany.proyecto_seguimiento.modelo.UsuarioDatos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * SessionManager gestiona la sesión actual del usuario en la aplicación.
 * Es un singleton y ahora soporta persistencia en archivo Properties.
 */
public class SessionManager {

    private static SessionManager instance;
    private static final String ARCHIVO_SESSION = "session.properties";

    private String ciUsuario;
    private List<String> rolesUsuario;
    private String rolSeleccionado;
    private String correoUsuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String passwordTemporal;
    private boolean registroEnProgreso = false;
    private UsuarioDatos usuarioDatos; 
    private String pagina_anterior; 
    private Especialidad espe; 
    private String codigoVerificacion;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // --- Getters y setters ---
    public String getCiUsuario() { return ciUsuario; }
    public void setCiUsuario(String ciUsuario) { this.ciUsuario = ciUsuario; }

    public List<String> getRolesUsuario() { return rolesUsuario; }
    public void setRolesUsuario(List<String> rolesUsuario) { this.rolesUsuario = rolesUsuario; }

    public String getRolSeleccionado() { return rolSeleccionado; }
    public void setRolSeleccionado(String rolSeleccionado) { this.rolSeleccionado = rolSeleccionado; }

    public String getCorreoUsuario() { return correoUsuario; }
    public void setCorreoUsuario(String correoUsuario) { this.correoUsuario = correoUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPasswordTemporal() { return passwordTemporal; }
    public void setPasswordTemporal(String passwordTemporal) { this.passwordTemporal = passwordTemporal; }

    public boolean isRegistroEnProgreso() { return registroEnProgreso; }
    public void setRegistroEnProgreso(boolean registroEnProgreso) { this.registroEnProgreso = registroEnProgreso; }

    public String getPagina_anterior() { return pagina_anterior; }
    public void setPagina_anterior(String pagina_anterior) { this.pagina_anterior = pagina_anterior; }

    public String getCodigoVerificacion() { return codigoVerificacion; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }

    public Especialidad getEspe() { return espe; }
    public void setEspe(Especialidad espe) { this.espe = espe; }

    public UsuarioDatos getUsuarioDatos() { return usuarioDatos; }
    public void setUsuarioDatos(UsuarioDatos usuarioDatos) { this.usuarioDatos = usuarioDatos; }

    // --- Limpieza de sesión ---
    public void limpiarSesion() {
        this.ciUsuario = null;
        this.rolesUsuario = null;
        this.rolSeleccionado = null;
        this.correoUsuario = null;
        this.nombre = null;
        this.apellido = null;
        this.telefono = null;
        this.passwordTemporal = null;
        this.pagina_anterior = null;
        this.registroEnProgreso = false;
        this.usuarioDatos = null;
        this.codigoVerificacion = null;
        this.espe = null;

        // Borrar archivo de sesión
        File f = new File(ARCHIVO_SESSION);
        if(f.exists()) f.delete();
    }

    // --- Guardar sesión en Properties ---
    public void guardarSesionEnArchivo() {
        Properties props = new Properties();
        try (FileOutputStream out = new FileOutputStream(ARCHIVO_SESSION)) {
            props.setProperty("ciUsuario", ciUsuario != null ? ciUsuario : "");
            props.setProperty("rolSeleccionado", rolSeleccionado != null ? rolSeleccionado : "");
            if (rolesUsuario != null && !rolesUsuario.isEmpty()) {
                props.setProperty("rolesUsuario", String.join(",", rolesUsuario));
            }
            // Puedes agregar más campos si quieres
            // props.setProperty("nombre", nombre != null ? nombre : "");
            props.store(out, "Datos de sesión del usuario");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Cargar sesión desde Properties ---
    public boolean cargarSesionDeArchivo() {
        File f = new File(ARCHIVO_SESSION);
        if (!f.exists()) return false;

        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(f)) {
            props.load(in);
            ciUsuario = props.getProperty("ciUsuario", null);
            rolSeleccionado = props.getProperty("rolSeleccionado", null);
            String roles = props.getProperty("rolesUsuario", null);
            if (roles != null && !roles.isEmpty()) {
                rolesUsuario = new ArrayList<>();
                for (String r : roles.split(",")) {
                    rolesUsuario.add(r);
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
