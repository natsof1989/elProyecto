package com.mycompany.proyecto_seguimiento.clases;

import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import com.mycompany.proyecto_seguimiento.modelo.UsuarioDatos;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private static SessionManager instance;
    
    // Campos existentes
    private String ciUsuario;
    private List<String> rolesUsuario;
    private String rolSeleccionado;
    
    // Nuevos campos para el registro
    private String correoUsuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String passwordTemporal;
    private boolean registroEnProgreso = false;
    private UsuarioDatos usuarioDatos; 
    private String pagina_anterior; 
    private Especialidad espe; 

    public String getPagina_anterior() {
        return pagina_anterior;
    }

    public void setPagina_anterior(String pagina_anterior) {
        this.pagina_anterior = pagina_anterior;
    }

    private String codigoVerificacion; // Nuevo campo del mauri
    public UsuarioDatos getUsuarioDatos() {
        return usuarioDatos;
    }

    public void setUsuarioDatos(UsuarioDatos usuarioDatos) {
        this.usuarioDatos = usuarioDatos;
    }

    

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    
    public String getCiUsuario() { 
        return ciUsuario; 
    }
    public void setCiUsuario(String ciUsuario) { 
        this.ciUsuario = ciUsuario; 
    }
    
    public List<String> getRolesUsuario() { 
        return rolesUsuario; 
    }
    public void setRolesUsuario(List<String> rolesUsuario) { 
        this.rolesUsuario = rolesUsuario; 
    }
    
    public String getRolSeleccionado() { 
        return rolSeleccionado; 
    }
    public void setRolSeleccionado(String rolSeleccionado) { 
        this.rolSeleccionado = rolSeleccionado; 
    }

    // Nuevos métodos para el flujo de registro
    public String getCorreoUsuario() { 
        return correoUsuario; 
    }
    public void setCorreoUsuario(String correoUsuario) { 
        this.correoUsuario = correoUsuario; 
    }
    
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        this.nombre = nombre; 
    }
    
    public String getApellido() { 
        return apellido; 
    }
    public void setApellido(String apellido) { 
        this.apellido = apellido; 
    }
    
    public String getTelefono() { 
        return telefono; 
    }
    public void setTelefono(String telefono) { 
        this.telefono = telefono; 
    }
    
    public String getPasswordTemporal() { 
        return passwordTemporal; 
    }
    public void setPasswordTemporal(String passwordTemporal) { 
        this.passwordTemporal = passwordTemporal; 
    }
    
    public boolean isRegistroEnProgreso() { 
        return registroEnProgreso; 
    }
    public void setRegistroEnProgreso(boolean registroEnProgreso) { 
        this.registroEnProgreso = registroEnProgreso; 
    }

    // Getter y Setter para el código de verificación
    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }
    
    // Método limpiarSesion modificado
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
    }

    public Especialidad getEspe() {
        return espe;
    }

    public void setEspe(Especialidad espe) {
        this.espe = espe;
    }
    

}