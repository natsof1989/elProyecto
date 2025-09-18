/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.ET_singleton;
import com.mycompany.proyecto_seguimiento.clases.EmailUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.modelo.equipoTecnico;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class Asignar_CasoController implements Initializable {

    @FXML
    private Button btn_aceptar;
    @FXML
    private Button btn_cancelar;
    @FXML
    private ListView<CheckBox> lista_tecnicos;


    /**
     * Initializes the controller class.
     */
    
    
    private final conexion dbConexion = new conexion();
    private final CasoDAO casoDAO = new CasoDAO(dbConexion.getConnection()); 
    
   @Override
    public void initialize(URL url, ResourceBundle rb) {
        int idCaso = CasoSeleccionado.getInstancia().getIdCaso();

        // Equipos disponibles (los tenés guardados en el singleton)
        List<equipoTecnico> equipos = ET_singleton.getInstancia().getEquipos(); 

        // IDs de los ya asignados al caso
        List<Integer> asignados = CasoSeleccionado.getInstancia().getAsignados();

        // Limpiamos la lista por si acaso
        lista_tecnicos.getItems().clear();
        
        for (equipoTecnico eq : equipos) {
            
            CheckBox check = new CheckBox(eq.getNombreCompleto() + " " + eq.getDepartamento()); // mostrar nombre en el check
            check.setUserData(Integer.parseInt(eq.getCi())); // <-- guardar como Integer

            int ciEquipo = Integer.parseInt(eq.getCi());
            if (asignados != null && asignados.contains(ciEquipo)) {
                check.setSelected(true);
            }

            lista_tecnicos.getItems().add(check);
        }
    }

    @FXML
    private void aceptar(ActionEvent event) {
        
        CasoSeleccionado caso = CasoSeleccionado.getInstancia();
        int idCaso = caso.getIdCaso();

        // Lo seleccionado ahora
        List<Integer> seleccionados = new ArrayList<>();
        for (CheckBox check : lista_tecnicos.getItems()) {
            if (check.isSelected()) {
                seleccionados.add((Integer) check.getUserData());
            }
        }

        // Lo que había antes
        List<Integer> originales = caso.getAsignados();
        if (originales == null) originales = new ArrayList<>();

        // Diferencias
        List<Integer> aInsertar = new ArrayList<>(seleccionados);
        aInsertar.removeAll(originales); // nuevos responsables

        List<Integer> aEliminar = new ArrayList<>(originales);
        aEliminar.removeAll(seleccionados);

        if (!aInsertar.isEmpty() || !aEliminar.isEmpty()) {
            try {
                casoDAO.aplicarCambiosAsignacion(idCaso, aInsertar, aEliminar);

                // Traer todos los miembros del equipo técnico
                List<equipoTecnico> equipo = casoDAO.getEmailsEquipoTec();

                // Recorrer cada miembro y enviar correo si fue asignado
                for (equipoTecnico miembro : equipo) {
                    String ci = miembro.getCi();
                    String email = miembro.getEmail();

                    // Si el CI está en la lista de asignados recientemente
                    if (aInsertar.contains(Integer.valueOf(ci))) {
                        String asunto = "Nuevo caso asignado";
                        String mensaje = String.format(
                            "Hola,\n\n" +
                            "Se le ha asignado un nuevo caso en el sistema.\n\n" +
                            "Detalles del caso:\n" +
                            "- ID del caso: %d\n" + 
                            "- Descripción del caso: %s\n"+
                            "- Profesor que generó el caso: %s\n" +
                            "- Estudiante: %s\n" +
                            "- Especialidad: %s\n" +
                            "- Curso: %s\n\n" +
                            "Por favor, ingrese al sistema para revisarlo.\n\n" +
                            "Saludos,\n" +
                            "Sistema de Seguimiento",
                            idCaso, caso.getDescripcion(),
                            caso.getNombreProfesor(),
                            caso.getEstudiante(),
                            caso.getEspecialidad(),
                            caso.getCurso()
                        );

                        EmailUtils.enviarCorreo(email, asunto, mensaje);
                    }
                }

                // Actualizar el caso en memoria
                caso.setAsignados(seleccionados);

                ControladorUtils.mostrarAlertaChill("Éxito", "Asignación exitosa.");
                cerrarModal(event);
                ControladorUtils.cambiarVista("abrirCaso");
            } catch (SQLException e) {
                e.printStackTrace();
                ControladorUtils.mostrarAlerta("Error", "No se pudo asignar el caso");
            }
        } else {
            // No hubo cambios → simplemente cerrar
            cerrarModal(event);
        }
    }



    @FXML
    private void cancelar(ActionEvent event) {
        
        cerrarModal(event);
        

    }

    // Método helper para cerrar el modal
    private void cerrarModal(ActionEvent event) {
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        

    }

}