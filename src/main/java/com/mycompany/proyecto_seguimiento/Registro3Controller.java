package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Seguridad;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class Registro3Controller implements Initializable {

    @FXML
    private PasswordField txt_password;
    @FXML
    private PasswordField txt_password2;
    @FXML
    private Button btn_registro;
    
    private SessionManager session;
    private UsuarioDAO usuarioDao;
    private final conexion dbConexion = new conexion();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        session = SessionManager.getInstance();
        this.usuarioDao = new UsuarioDAO(dbConexion.getConnection());
    }    

    @FXML
    private void guardar_datos(ActionEvent event) throws IOException {
        // 1. Validar que las contraseñas no estén vacías
        if (ControladorUtils.hayCamposVacios(txt_password, txt_password2)) {
            ControladorUtils.mostrarAlerta("Error", "Debe ingresar y confirmar la contraseña");
            return;
        }
        
        String password = txt_password.getText();
        String confirmacion = txt_password2.getText();
        
        // 2. Validar que coincidan
        if (!password.equals(confirmacion)) {
            ControladorUtils.mostrarAlertaChill("Error", "Las contraseñas no coinciden");
            txt_password.requestFocus();
            return;
        }
        
        // 3. Validar fortaleza de la contraseña (opcional)
        if (password.length() < 8) {
            ControladorUtils.mostrarAlerta("Error", 
                "La contraseña debe tener al menos 8 caracteres");
            txt_password.requestFocus();
            return;
        }
        
        // 4. Encriptar la contraseña con BCrypt
        String passwordEncriptada = Seguridad.encriptarPassword(password);
        
        // 5. Guardar en sesión
        session.setPasswordTemporal(passwordEncriptada);
        
        // 6. Completar el registro en la base de datos
        try {
            boolean registroExitoso = completarRegistroEnBD();
            
            if (registroExitoso) {
                ControladorUtils.mostrarAlertaChill("Éxito", "Registro completado correctamente");
                limpiarCampos();
                ControladorUtils.cambiarVista("inicioSesion");
                
            } else {
                ControladorUtils.mostrarAlerta("Error", "No se pudo completar el registro");
            }
        } catch (Exception e) {
            ControladorUtils.mostrarAlerta("Error", "Error al registrar: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean completarRegistroEnBD() throws SQLException {
        // Obtener datos de sesión
        String ci = session.getCiUsuario();
        String nombre = session.getNombre();
        String apellido = session.getApellido();
        
        String correo = session.getCorreoUsuario();
        String password = session.getPasswordTemporal();
        
        // Aquí deberías llamar a tu UsuarioDAO
        // Ejemplo:
        // UsuarioDAO usuarioDao = new UsuarioDAO(conexion);
        return usuarioDao.insertarUsuarioCompletoTransaccional(ci, nombre, apellido, correo, password);
        
        
    }
    
    private void limpiarCampos() {
        txt_password.clear();
        txt_password2.clear();
        session.limpiarSesion(); // Limpiar todos los datos de sesión
    }
}