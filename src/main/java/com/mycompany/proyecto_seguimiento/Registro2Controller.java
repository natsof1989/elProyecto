/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import java.io.IOException;


import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class Registro2Controller implements Initializable {

    @FXML
    private TextField txt_nombre;
    @FXML
    private TextField txt_apellido;
    
    @FXML
    private Button btn_registrarse;
    @FXML
    private Text txt_CI;
    @FXML
    private Text txt_correo;

    /**
     * Initializes the controller class.
     */
    private final SessionManager session = SessionManager.getInstance();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt_CI.setText(session.getCiUsuario()); 
        txt_correo.setText(session.getCorreoUsuario()); 
        
        // TODO
    }    

    @FXML
    private void reg_insert(ActionEvent event) throws IOException {
    
        if (ControladorUtils.hayCamposVacios(txt_nombre, txt_apellido)) {
            ControladorUtils.mostrarAlerta("Error", "Todos los campos son obligatorios"); 
            return;
        }

        
        
        

        // 3. Validar formato del teléfono (CORRECCIÓN IMPORTANTE: se niega la condición)
        
        if (!ControladorUtils.validarCamposLetras(txt_nombre, txt_apellido)) {
            ControladorUtils.mostrarAlerta("Error", 
                "Nombre y apellido solo pueden contener letras");
            return;
        }
        String nombre = txt_nombre.getText(); 
        String apellido = txt_apellido.getText();

    
        session.setNombre(nombre); 
        session.setApellido(apellido); 
        
        ControladorUtils.cambiarVista("registro3"); 
        
    }
    
}
