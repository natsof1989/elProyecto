/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class EquipoTecnico2Controller implements Initializable {

    @FXML
    private Button btn_volver;
    @FXML
    private Button btn_Orientaciones;
    @FXML
    private StackPane contenedor;
    @FXML
    private Button btn_myOrienta;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ControladorUtils.cargarVistaStackPane("ReadOrienta.fxml", contenedor);
        // TODO
    }    

    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("equipoTecnico");
    }

    

    @FXML
    private void abrirOrientaciones(ActionEvent event) {
        ControladorUtils.cargarVistaStackPane("ReadOrienta.fxml", contenedor);
        
    }

    @FXML
    private void abrirMisOrienta(ActionEvent event) {
        ControladorUtils.cargarVistaStackPane("ReadOrienta2.fxml", contenedor);
        
    }

    
    
}
