/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ET_singleton;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.modelo.equipoTecnico;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class AbrirCasoController implements Initializable {

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
    private TextArea txt_descripcion;
    @FXML
    private Text txt_profe;
    @FXML
    private Text txt_CI;
    @FXML
    private Button btn_volver;
    @FXML
    private Button btn_descargar;

    /**
     * Initializes the controller class.
     */
    private int ci = Integer.parseInt(SessionManager.getInstance().getCiUsuario());
    
    private final conexion dbConexion = new conexion();
    
    private final equipoTecnicoDAO equipoTecDAO = new equipoTecnicoDAO(dbConexion.getConnection());
    private final int idCaso = CasoSeleccionado.getInstancia().getIdCaso();
    List<Integer> asignados = new ArrayList<>(); 
    
    CasoSeleccionado datosCaso = CasoSeleccionado.getInstancia(); 
    LocalDateTime fecha = datosCaso.getFecha();
    @FXML
    private TitledPane equipoT_content;
    @FXML
    private ScrollPane scroll_equipo;
    @FXML
    private Button btn_asignar;
    @FXML
    private TitledPane opcionesCaso;
    @FXML
    private TitledPane opcionesET;
    @FXML
    private Button btn_cas_historial;
    @FXML
    private Button btn_orienta_historial;
    @FXML
    private TitledPane jefa_content;
    @FXML
    private Button btn_finCaso;
    @FXML
    private Button btn_cargarOrienta;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        txt_idCaso.setText(String.valueOf(idCaso)); 
        txt_estudiante.setText(datosCaso.getEstudiante());
        txt_espe.setText(datosCaso.getEspecialidad());
        txt_curso.setText(datosCaso.getCurso());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        txt_fecha.setText(fecha.format(formatter));
        txt_descripcion.setText(datosCaso.getDescripcion());
        txt_profe.setText(datosCaso.getNombreProfesor());
        txt_CI.setText(datosCaso.getCiProfesor());
        
        
         if (datosCaso.getArchivo() != null) {
            btn_descargar.setVisible(true);
            btn_descargar.setDisable(false);
        } else {
            btn_descargar.setVisible(false);
            btn_descargar.setDisable(true);
        }
        
        Tooltip tooltip = new Tooltip("Descargar archivo adjunto");
        btn_descargar.setTooltip(tooltip);
        
            boolean jefa;
            
        try {
            jefa = equipoTecDAO.perteneceDepartamento1(ci);
            asignados = equipoTecDAO.obtenerAsignados(idCaso); 
            
            if(asignados!=null){
                CasoSeleccionado.getInstancia().setAsignados(asignados);
            }
            
            jefa_content.setVisible(jefa);
           
        } catch (SQLException ex) {
            Logger.getLogger(AbrirCasoController.class.getName()).log(Level.SEVERE, null, ex);
        }
          
        String fxml = CasoSeleccionado.getInstancia().getFxmlAnterior(); 

        if ("teacher1".equals(fxml)) {
            equipoT_content.setVisible(false);
            opcionesET.setVisible(false);
            
        } else{
           equipoT_content.setVisible(true);
           opcionesET.setVisible(true);   
           if (!asignados.isEmpty()) {
                btn_asignar.setText("Reasignar");
                datosCaso.setAsignados(asignados);
                mostrarEquipoContent(asignados);  // <-- SOLO AQUÍ LO LLAMÁS
            }
           // <-- NO llamás a mostrarEquipoContent(), por eso el ScrollPane queda vacío
        }


        // TODO
    }    
    
    private void mostrarEquipoContent(List<Integer> asignados) {
    VBox content = new VBox(8); // Contenedor vertical con espacio
    scroll_equipo.setContent(content);

    List<equipoTecnico> equipos = ET_singleton.getInstancia().getEquipos();

    for (equipoTecnico et : equipos) {
        if (asignados.contains(Integer.valueOf(et.getCi()))) {
            
            Label label = new Label(et.getNombreCompleto() + " - " + et.getDepartamento());
            label.setStyle("-fx-padding: 6; -fx-background-color: #f1f1f1; -fx-border-color: #ccc; -fx-background-radius: 5;");
            content.getChildren().add(label);
        }


    }
}


    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista(datosCaso.getFxmlAnterior());
    }

    @FXML
    private void descargarArchivo(ActionEvent event) {
        
        byte[] archivo = datosCaso.getArchivo();


        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo del caso");

        // Obtener extensión y generar nombre por defecto
        String extension = datosCaso.getExtension();
        if (extension == null || extension.isEmpty()) {
            extension = "dat"; // fallback por si no hay extensión
        }

        fileChooser.setInitialFileName("archivo_caso_" + datosCaso.getIdCaso() + "." + extension);

        File file = fileChooser.showSaveDialog(btn_descargar.getScene().getWindow());
        if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(archivo);
                ControladorUtils.mostrarAlertaChill("Guardado exitoso", "Archivo guardado en: " + file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
    }


    @FXML
    private void asignarCaso(ActionEvent event) {
        ControladorUtils.abrirModal("asignar_Caso", "Asignación de los encargados de atender el caso");
       
        
    }

    @FXML
    private void hisOrientacion(ActionEvent event) {
    }

    @FXML
    private void finCaso(ActionEvent event) {
    }

    @FXML
    private void cargarOrienta(ActionEvent event) {
    }

}
