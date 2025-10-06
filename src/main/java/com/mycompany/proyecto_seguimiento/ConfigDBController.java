/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConfigDBController {

    @FXML
    private TextField hostField;
    @FXML
    private TextField puertoField;
    @FXML
    private TextField nombreBDField;
    @FXML
    private TextField usuarioField;
    @FXML
    private PasswordField contrasenaField;
    @FXML
    private Button btn_salir;

    @FXML
    private void guardarConfiguracion() {
        String host = hostField.getText().trim();
        String puerto = puertoField.getText().trim();
        String nombreBD = nombreBDField.getText().trim();
        String usuario = usuarioField.getText().trim();
        String contrasena = contrasenaField.getText();

        if (host.isEmpty() || puerto.isEmpty() || nombreBD.isEmpty() || usuario.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios (excepto contraseña).");
            return;
        }

        String url = String.format("jdbc:mysql://%s:%s/%s", host, puerto, nombreBD);

        //conexion.guardarConfiguracion(url, usuario, contrasena);

        mostrarAlerta("¡Configuración guardada exitosamente!");

        // Cerrar la ventana de configuración
        Stage stage = (Stage) hostField.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Configuración");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void salir(ActionEvent event) {
        SessionManager.getInstance().limpiarSesion();
        ControladorUtils.cambiarVista("inicioSesion");
    }
}