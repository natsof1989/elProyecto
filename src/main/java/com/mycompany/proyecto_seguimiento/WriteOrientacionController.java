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

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
/**
 * FXML Controller class
 *
 * @author natha
 */
public class WriteOrientacionController implements Initializable {


    @FXML
    private Button btn_volver;
    @FXML
    private Button btn_guardar;
    @FXML
    private Button btn_cancelar;
    @FXML
    private Button btn_imprimir;
    @FXML
    private Label id_orientacion;
    @FXML
    private Text txt_profesor;
    @FXML
    private Text txt_idCaso;
    @FXML
    private Text txt_estudiante;
    @FXML
    private Text txt_espe;
    @FXML
    private Text txt_curso;
    @FXML
    private Text txt_fecha;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void volver(ActionEvent event) {
    }

    @FXML
    private void guardar(ActionEvent event) {
    }

    @FXML
    private void cancelar(ActionEvent event) {
    }

    @FXML
    private void imprimir(ActionEvent event) {
    }

}
