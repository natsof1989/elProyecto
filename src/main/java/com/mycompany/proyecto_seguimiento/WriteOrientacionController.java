/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.Orientacion;
import com.mycompany.proyecto_seguimiento.clases.OrientacionDAO;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Button btn_imprimir;
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
    @FXML
    private TextArea txt_orientacion;
    @FXML
    private VBox autorContent;
    /**
     * Initializes the controller class.
     */
    CasoSeleccionado casoSelected = CasoSeleccionado.getInstancia(); 
    @FXML
    private Text txt_Autor;
    @FXML
    private Text txt_departamento;
    @FXML
    private Text txt_autorCI;
    private final conexion dbConexion = new conexion();
    private OrientacionDAO orientaDAO = new OrientacionDAO(dbConexion.getConnection()); 
    
    private SessionManager session = SessionManager.getInstance(); 
    @FXML
    private Label txt_idOrienta;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt_estudiante.setText(casoSelected.getEstudiante());
        txt_curso.setText(casoSelected.getCurso());
        txt_espe.setText(casoSelected.getEspecialidad()); 
        txt_profesor.setText(casoSelected.getNombreProfesor());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txt_fecha.setText(casoSelected.getFecha().format(formatter));
        txt_idCaso.setText(String.valueOf(casoSelected.getIdCaso()));
        if(txt_orientacion.getText().isEmpty()){
            autorContent.setVisible(false);
        } else{
            autorContent.setVisible(true);
        }
          
    }    
    
    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista(Orientacion.getInstancia().getFxmlAnterior());
    }

    @FXML
   private void guardar(ActionEvent event) throws SQLException {
        if (!ControladorUtils.hayCamposVacios(txt_orientacion)) {
            

            if (ControladorUtils.mostrarConfirmacion(
                    "Guardar orientación",
                    "¿Desea guardar la orientación?\nEsta acción no puede deshacerse")) {
                autorContent.setVisible(true);

                Orientacion.getInstancia().setOrientacion(txt_orientacion.getText());
                int idCaso = casoSelected.getIdCaso();
                int codOrienta = orientaDAO.insertarOrientacion(txt_orientacion.getText(), idCaso);

                if (codOrienta > 0) {
                    Timestamp fecha = orientaDAO.getFechaOrientacion(codOrienta);
                    Orientacion.getInstancia().setFecha(fecha);

                    if (fecha != null) {
                        System.out.println(fecha);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String fechaFormateada = fecha.toLocalDateTime().format(formatter);
                        txt_fecha.setText(fechaFormateada);
                        txt_Autor.setText(session.getUsuarioDatos().getNombre() + " " + session.getUsuarioDatos().getApellido());
                        txt_idOrienta.setText("Orientación: " + String.valueOf(codOrienta));
                        txt_autorCI.setText(session.getCiUsuario());
                        int ci = Integer.parseInt(session.getCiUsuario()); 
                        String depa = orientaDAO.getDescripcionDepartamentoPorCI(ci); 
                        if(depa!=null){
                            txt_departamento.setText(depa);
                        }else{
                            ControladorUtils.mostrarAlerta("Aviso", "El departamento del equipo tecnico no ha sido isnertado en la base de datos ");
                        }
                        

                        ControladorUtils.mostrarAlertaChill(
                            "Carga exitosa",
                            "La orientación fue guardada con éxito"
                        );
                        txt_orientacion.setDisable(true);
                    } else {
                        ControladorUtils.mostrarAlerta(
                            "Aviso",
                            "Ocurrió un error en la carga de la orientación"
                        );
                    }
                } else {
                    ControladorUtils.mostrarAlerta(
                        "Aviso",
                        "Ocurrió un error en la carga de la orientación"
                    );
                }
            }
        } else {
            ControladorUtils.mostrarAlerta(
                "Aviso",
                "No puede cargar una orientación vacía"
            );
        }
}


   

    @FXML
    private void imprimir(ActionEvent event) {
    }



}
