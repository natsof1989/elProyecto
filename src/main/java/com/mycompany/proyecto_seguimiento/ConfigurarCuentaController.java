/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;

import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Seguridad;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class ConfigurarCuentaController implements Initializable {

    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_apellido;
    @FXML
    private Button btn_edit;
    private TextField txt_telefono;
    @FXML
    private Button btn_eliminar;
    @FXML
    private Button btn_guardar;
    @FXML
    private Button btn_cancelar;
    
    /**
     * Initializes the controller class.
     */
    private SessionManager session;
    private UsuarioDAO usuarioDao;
    private final conexion dbConexion = new conexion();
    private final String ci = SessionManager.getInstance().getCiUsuario(); 
    @FXML
    private Button btn_salir;
    @FXML
    private Button btn_password;
    
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        session = SessionManager.getInstance();
        this.usuarioDao = new UsuarioDAO(dbConexion.getConnection());
        txt_nombre.setText(session.getUsuarioDatos().getNombre());
        txt_apellido.setText(session.getUsuarioDatos().getApellido());
        
        
    }    

    @FXML
    private void editar(ActionEvent event) {
        btn_guardar.setDisable(false); 
        btn_cancelar.setDisable(false); 
        txt_nombre.setDisable(false); 
        txt_apellido.setDisable(false); 
        txt_telefono.setDisable(false); 
        btn_eliminar.setDisable(true); 
    }

    @FXML
    private void eliminar(ActionEvent event) throws SQLException, IOException {
        // Mostrar diálogo de confirmación
        boolean confirmado = ControladorUtils.mostrarConfirmacion(
            "Confirmar eliminación",
            "¿Está seguro que desea eliminar su cuenta?\nEsta acción no se puede deshacer."
        );

        if (confirmado) {
            try {

                // Ejecutar desactivación
                usuarioDao.desactivarCuenta(ci); 
                session.limpiarSesion();

                // Mostrar confirmación
                ControladorUtils.mostrarAlertaChill(
                    "Cuenta eliminada", 
                    "La cuenta ha sido eliminada con éxito.\nSerá redirigido al login."
                );

                // Redirigir

                ControladorUtils.cambiarVista( "inicioSesion");

            } catch (SQLException ex) {
                ControladorUtils.mostrarError(
                    "Error al eliminar", 
                    "No se pudo completar la eliminación de la cuenta", 
                    ex
                );
            }
        } else {
            ControladorUtils.mostrarAlerta(
                "Operación cancelada", 
                "La eliminación de la cuenta fue cancelada"
            );
        }
    }

    @FXML
    private void guardar(ActionEvent event) throws SQLException {
        // Deshabilitar controles temporalmente
        txt_nombre.setDisable(true); 
        txt_apellido.setDisable(true); 
        
        btn_cancelar.setDisable(true); 
        btn_guardar.setDisable(true);

        // Validar campos vacíos
        if(ControladorUtils.hayCamposVacios(txt_nombre, txt_apellido, txt_telefono)){
            ControladorUtils.mostrarAlerta("Error", "No pueden haber campos vacíos."
                + "Si desea eliminar su información, entonces elimine su cuenta");
           
            reactivarControles();
            return; 
        }

        
        String nombre = txt_nombre.getText().trim();
        String apellido = txt_apellido.getText().trim();

        // Validar formato del teléfono
       

        // Validar campos de texto
        if (!ControladorUtils.validarCamposLetras(txt_nombre, txt_apellido)) {
            ControladorUtils.mostrarAlerta("Error", 
                "Nombre y apellido solo pueden contener letras");
            reactivarControles();
            return;
        }

        // Mostrar confirmación antes de guardar
        boolean confirmar = ControladorUtils.mostrarConfirmacion(
            "Confirmar cambios",
            "¿Está seguro que desea guardar los cambios?\n\n" +
            "Nombre: " + nombre + "\n" +
            "Apellido: " + apellido + "\n"
        );

        if (confirmar) {
            try {
                usuarioDao.actualizarDatosBasicos(ci, nombre, apellido);
                String email = session.getUsuarioDatos().getEmail(); 
                session.setUsuarioDatos(usuarioDao.obtenerDatosUsuario(ci)); 
                ControladorUtils.mostrarAlertaChill("Éxito", "Datos actualizados correctamente");
                reactivarControles(); 
            } catch (SQLException ex) {
                ControladorUtils.mostrarError("Error", "No se pudieron guardar los cambios", ex);
                reactivarControles();
            }
        } else {
            ControladorUtils.mostrarAlerta("Información", "Cambios no guardados");
            reactivarControles();
        }
    }

// Método auxiliar para reactivar controles
    private void reactivarControles() {
        txt_nombre.setText(session.getUsuarioDatos().getNombre());
        txt_apellido.setText(session.getUsuarioDatos().getApellido());
        
        txt_nombre.setDisable(true);
        txt_apellido.setDisable(true);
        txt_telefono.setDisable(true);
        btn_cancelar.setDisable(true);
        btn_guardar.setDisable(true);
        btn_eliminar.setDisable(false); 
    }

    @FXML
    private void cancelar(ActionEvent event) {
        btn_guardar.setDisable(true); 
        btn_cancelar.setDisable(true); 
        reactivarControles(); 
        
    }
    
    

    @FXML
    private void salir(ActionEvent event) throws IOException {
        String pagina = SessionManager.getInstance().getPagina_anterior(); 
        ControladorUtils.cambiarVista(pagina);
        
    }

    @FXML
    private void cambiarPassword(ActionEvent event) throws IOException {
        ControladorUtils.cambiarVista( "recuperarAcceso");
    }
    
}
