package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.ProfesorDAO;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import com.mycompany.proyecto_seguimiento.modelo.UsuarioDatos;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InicioSesionController implements Initializable {

    @FXML private TextField txtCI;
    @FXML private PasswordField txtContrasenhia;
    @FXML private Button btInicioSesion;
    @FXML private Hyperlink link1;
    @FXML private Hyperlink link2;
    @FXML private StackPane rootPane;

    private UsuarioDAO usuarioDao;
    private final SessionManager session = SessionManager.getInstance();
    private final conexion dbConexion = new conexion();
    private UsuarioDatos datos;
    private boolean isCoordi = false; 
    private ProfesorDAO profesorDao; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.usuarioDao = new UsuarioDAO(dbConexion.getConnection());
        this.profesorDao = new ProfesorDAO(dbConexion.getConnection());
    }

    @FXML
    private void iniciarSesion(ActionEvent event) {
        if (ControladorUtils.hayCamposVacios(txtCI, txtContrasenhia)) {
            ControladorUtils.mostrarAlerta("Error", "Todos los campos son obligatorios");
            return;
        }

        String ci = txtCI.getText().trim();
        String contrasenia = txtContrasenhia.getText();

        if (!ControladorUtils.validarNumero(ci, "CI")) {
            ControladorUtils.mostrarAlerta("Error", "Introducción del número de cédula errónea");
            return;
        }

        try {
            if (!usuarioDao.existeCI(ci)) {
                ControladorUtils.mostrarAlerta("Error", "El número de cédula no está registrado");
                return;
            }

            if (!usuarioDao.existeRegistroCompleto(ci)) {
                ControladorUtils.mostrarAlertaChill("Aviso", "Usted aún no se ha registrado. \nCree su cuenta antes de iniciar sesión.");
                txtCI.clear();
                txtContrasenhia.clear();
                return;
            }

            if (!usuarioDao.validarCredenciales(ci, contrasenia)) {
                ControladorUtils.mostrarAlerta("Error", "Contraseña incorrecta");
                return;
            }

            // Guardamos datos en el SessionManager
            session.setCiUsuario(ci);
            session.setRolesUsuario(usuarioDao.obtenerRoles(ci));
            datos = usuarioDao.obtenerDatosUsuario(ci);
            session.setUsuarioDatos(datos);

            try {
                Especialidad espe = profesorDao.esPioCoordi(Integer.parseInt(ci));
                if (espe != null) {
                    session.setEspe(espe);
                    isCoordi = true; 
                }
            } catch (SQLException ex) {
                Logger.getLogger(InicioSesionController.class.getName()).log(Level.SEVERE, null, ex);
            }

            List<String> roles = session.getRolesUsuario();

            if (roles.size() == 1) {
                session.setRolSeleccionado(roles.get(0));
                String vista = roles.get(0).equals("PROFESOR") ? "teacher1" : "equipoTecnico";

                // Guardar sesión persistente
                session.guardarSesionEnArchivo();

                App.setRoot(vista);

            } else if (roles.size() > 1) {
                if (isCoordi) {
                    session.setRolSeleccionado("PROFESOR"); // Forzar coordinador
                    session.guardarSesionEnArchivo();
                    App.setRoot("teacher1");
                } else {
                    session.guardarSesionEnArchivo();
                    App.setRoot("SeleccionRol");
                }
            } else {
                ControladorUtils.mostrarAlerta("Error", "Usuario sin roles asignados");
            }

        } catch (SQLException | IOException ex) {
            ControladorUtils.mostrarError("Error", "Ocurrió un error en el login", ex);
        }
    }

    @FXML
    private void registrarse(ActionEvent event) throws IOException {
        ControladorUtils.cambiarVista("registro");
    }

    @FXML
    private void recuperarAcceso(ActionEvent event) throws IOException {
        ControladorUtils.cambiarVista("recuperarAcceso");
    }
}
