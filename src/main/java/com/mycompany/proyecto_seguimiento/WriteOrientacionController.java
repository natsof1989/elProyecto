/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.EmailUtils;
import com.mycompany.proyecto_seguimiento.clases.Orientacion;
import com.mycompany.proyecto_seguimiento.clases.OrientacionDAO;
import com.mycompany.proyecto_seguimiento.clases.Reporte;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.OrientacionResumen;
import com.mycompany.proyecto_seguimiento.modelo.Profes;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    private int idOrienta; 
   
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
        btn_imprimir.setDisable(true);
          
    }    
    
    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista(Orientacion.getInstancia().getFxmlAnterior());
    }

    @FXML
    private void guardar(ActionEvent event) throws SQLException {
        if (ControladorUtils.hayCamposVacios(txt_orientacion)) {
            ControladorUtils.mostrarAlerta("Aviso", "No puede cargar una orientación vacía");
            return;
        }

        if (!ControladorUtils.mostrarConfirmacion(
                "Guardar orientación",
                "¿Desea guardar la orientación?\nEsta acción no puede deshacerse")) {
            return;
        }

        // Bloquear campos y botones
        autorContent.setVisible(true);
        txt_orientacion.setDisable(true);
        btn_guardar.setDisable(true);
        btn_imprimir.setDisable(false);

        // Guardar orientación
        int idCaso = casoSelected.getIdCaso();
        int ci = Integer.parseInt(SessionManager.getInstance().getCiUsuario());
        int codOrienta = orientaDAO.insertarOrientacion(txt_orientacion.getText(), idCaso, ci);
        idOrienta = codOrienta; 
        if (codOrienta <= 0) {
            ControladorUtils.mostrarAlerta("Aviso", "Ocurrió un error en la carga de la orientación");
            return;
        }

        Timestamp fecha = orientaDAO.getFechaOrientacion(codOrienta);
        if (fecha == null) {
            ControladorUtils.mostrarAlerta("Aviso", "Ocurrió un error en la carga de la orientación");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = fecha.toLocalDateTime().format(formatter);
        txt_fecha.setText(fechaFormateada);
        txt_Autor.setText(session.getUsuarioDatos().getNombre() + " " + session.getUsuarioDatos().getApellido());
        txt_idOrienta.setText("Orientación: " + codOrienta);
        txt_autorCI.setText(session.getCiUsuario());

        String depa = orientaDAO.getDescripcionDepartamentoPorCI(ci);
        if (depa != null) txt_departamento.setText(depa);

        // Preparar envío de correos
        int id_curso = casoSelected.getId_curso(); 
        int ciEstudiante = casoSelected.getCiEstudiante(); 
        
        List<Profes> profesList = orientaDAO.getProfesByEspecialidad(id_curso, ciEstudiante);

        String subject = "Nueva orientación";
        String body = "\nEstimado profesor/a,\n\n" +
                      "Se ha registrado una nueva orientación\n" +
                        "\nEstudiante: " + casoSelected.getEstudiante() + "\n" +
                      "Especialidad: " + casoSelected.getEspecialidad() + "\n" +
                      "Curso: " + casoSelected.getCurso() + "\n" +
                      "Profesor responsable: " + casoSelected.getNombreProfesor() + "\n" +
                      "Fecha: " + fechaFormateada + "\n\n" +
                      "Detalle de la orientación:\n" + txt_orientacion.getText() + "\n\n" +
                      "Autor: " + session.getUsuarioDatos().getNombre() + " " + session.getUsuarioDatos().getApellido() + "\n" +
                      "Atentamente,\nSistema de Seguimiento";

        // Abrir modal de cargando
        Stage cargando = ControladorUtils.mostrarAlertaCargando(
            "Enviando correos",
            "Notificando a los profesores del estudiante...."
        );

        // Ejecutar envío en segundo plano
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                for (Profes profe : profesList) {
                    if (profe.getEmail() != null && !profe.getEmail().isEmpty()) {
                        EmailUtils.enviarCorreo(profe.getEmail(), subject, body);
                    }
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> {
            ControladorUtils.cerrarAlertaCargando(); // cierra el Stage
            ControladorUtils.mostrarAlertaChill(
                "Carga exitosa",
                "La orientación fue guardada y los profesores han sido notificados."
            );
        });

        task.setOnFailed(e -> {
            ControladorUtils.cerrarAlertaCargando();
            ControladorUtils.mostrarAlerta(
                "Error",
                "La orientación se guardó, pero hubo problemas al notificar a los profesores."
            );
        });

        new Thread(task).start();
    }



   

    @FXML
    private void imprimir(ActionEvent event) {
        if(ControladorUtils.mostrarConfirmacion("Confirmar acción", "¿Desea generar un pdf de esta orietnació?")){
             // Extraer los códigos de orientación seleccionados
            List<Integer> idsSeleccionados = new ArrayList<>(); 
            idsSeleccionados.add(idOrienta); 
            // Llamar al generador de reportes
            Reporte reporte = new Reporte();
            reporte.generarYGuardarReporte(
                btn_imprimir.getScene().getWindow(),
                "/reporte/orientaR.jasper", // ruta al jasper de orientaciones
                "orientacion"+idOrienta,        // nombre del archivo generado
                idsSeleccionados
            );
        }

       
    }



}
