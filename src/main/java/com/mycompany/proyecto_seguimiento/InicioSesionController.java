package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDatos;
import com.mycompany.proyecto_seguimiento.modelo.UsuarioDAO;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class InicioSesionController implements Initializable {

    @FXML private TextField txtCI;
    @FXML private PasswordField txtContrasenhia;
    @FXML private Button btInicioSesion;
    @FXML private Hyperlink link1;
    @FXML private Hyperlink link2;

    private UsuarioDAO usuarioDao;
    private final SessionManager session = SessionManager.getInstance();
    private final conexion dbConexion = new conexion();
    private UsuarioDatos datos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.usuarioDao = new UsuarioDAO(dbConexion.getConnection());
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

            session.setCiUsuario(ci);
            session.setRolesUsuario(usuarioDao.obtenerRoles(ci));
            datos = usuarioDao.obtenerDatosUsuario(ci);
            session.setUsuarioDatos(datos);

            List<String> roles = session.getRolesUsuario();

            if (roles.size() == 1) {
                session.setRolSeleccionado(roles.get(0));

                String vista = roles.get(0).equals("PROFESOR")
                        ? "teacher1"
                        : "equipoTecnico";

                App.setRoot(vista);

            } else if (roles.size() > 1) {
                App.setRoot("SeleccionRol");
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
