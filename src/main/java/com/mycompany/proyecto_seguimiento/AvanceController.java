/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
/**
 * FXML Controller class
 *
 * @author natha
 */
public class AvanceController implements Initializable {


    @FXML
    private TextArea txt_avance;
    @FXML
    private Button btn_guardar;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_editar;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void guardar(ActionEvent event) {
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cerrarModal(event);
    }

    @FXML
    private void editar(ActionEvent event) {
    }
    
     private void cerrarModal(ActionEvent event) {
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        

    }

}
