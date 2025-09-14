/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class EquipoTecnico4Controller implements Initializable {

    @FXML
    private Button btn_volver;
    @FXML
    private Button btn_Casos;
    @FXML
    private Button btn_misCasos;
    @FXML
    private StackPane contenedor;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ControladorUtils.cargarVistaStackPane("ReadCasos.fxml", contenedor);
        // TODO
    }    

    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("equipoTecnico");
    }

    @FXML
    private void CasosAbrir(ActionEvent event) {
        ControladorUtils.cargarVistaStackPane("ReadCasos.fxml", contenedor);
    }


    @FXML
    private void abrirMisCasos(ActionEvent event) {
        ControladorUtils.cargarVistaStackPane("equipoTecnico3.fxml", contenedor);
        
    }
    
}
