package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.EmailUtils;
import com.mycompany.proyecto_seguimiento.clases.ProfesorDAO;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Alumno;
import com.mycompany.proyecto_seguimiento.modelo.Curso;
import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import java.io.File;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Teacher3Controller implements Initializable {

    @FXML
    private TextArea txt_caso;
    @FXML
    private Button btn_guardar;
    @FXML
    private ComboBox<Especialidad> cmb_espe;
    @FXML
    private ComboBox<Curso> cmb_curso;
    @FXML
    private ComboBox<Alumno> cmb_alumno;
    @FXML
    private Text txt_estudiante;
    @FXML
    private Button btn_adjuntar;
    @FXML
    private Button btn_cancelar;
    @FXML
    private HBox hboxBotones;

    private final SessionManager session = SessionManager.getInstance();
    private final conexion dbConexion = new conexion();
    private UsuarioDAO usuarioDao;
    private ProfesorDAO profesorDao = new ProfesorDAO(dbConexion.getConnection()); 
    private CasoDAO casoDao = new CasoDAO(dbConexion.getConnection()); 
    
    private final String profCI = session.getCiUsuario(); 
    private File archivoSeleccionado;
    private HBox hboxArchivoSeleccionado; 
    
    private static final long MAX_FILE_SIZE = 16L * 1024 * 1024; // 16 MB
    @FXML
    private Button btn_imprimir;
   
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        List<Especialidad> especialidades = profesorDao.obtenerEspecialidad(profCI);
        cmb_espe.getItems().clear(); // limpiamos por si ya tenía algo
        cmb_espe.getItems().addAll(especialidades);
        txt_caso.setDisable(true);
        
          
    }


    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("teacher1");
    }

    @FXML
    private void habilitarCurso(ActionEvent event) {
        
        Especialidad seleccion = cmb_espe.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            btn_cancelar.setDisable(false);
            cmb_curso.setDisable(false);
            int idEspecialidad = seleccion.getId();  // Aquí tenés el ID
            List<Curso> cursos = profesorDao.obtenerCursos(profCI, idEspecialidad);
            cmb_curso.getItems().clear();
            cmb_alumno.getItems().clear();
            cmb_alumno.setDisable(true);
            btn_adjuntar.setDisable(true);
            btn_guardar.setDisable(true);
            txt_estudiante.setText("");
            txt_caso.setDisable(true);// limpiamos por si ya tenía algo
            cmb_curso.getItems().addAll(cursos);
        }   
        
    }




    @FXML
    private void habilitarAlumno(ActionEvent event) {
        
        Curso seleccion = cmb_curso.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            cmb_alumno.setDisable(false);
            int idCurso = seleccion.getId();  // Aquí tenés el ID
            

            List<Alumno> alumnos = profesorDao.obtenerAlumnos(idCurso);
            
            txt_estudiante.setText("");
            btn_adjuntar.setDisable(true);
            btn_guardar.setDisable(true);
            cmb_alumno.getItems().clear(); // limpiamos por si ya tenía algo
            cmb_alumno.getItems().addAll(alumnos);
            txt_caso.setDisable(true);
            
        }   
        
        
    }

   @FXML
    private void cargarAlumno(ActionEvent event) {
        Alumno seleccion = cmb_alumno.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            btn_guardar.setDisable(false); 
            btn_adjuntar.setDisable(false); 
            txt_caso.setDisable(false);
            txt_estudiante.setText(seleccion.getNombre()); // ahora usamos getNombre()
        }   
    }

    @FXML
    private void cargarArchivo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        File seleccionado = fileChooser.showOpenDialog(btn_adjuntar.getScene().getWindow());

        if (seleccionado != null) {
            // validar tamaño antes de asignar y mostrar
            if (!ControladorUtils.validarTamanoArchivo(seleccionado, MAX_FILE_SIZE)) {
                // no asignamos archivo, dejamos el botón de adjuntar como está
                return;
            }

            this.archivoSeleccionado = seleccionado;
            hboxArchivoSeleccionado = new HBox(5);
            Label lblArchivo = new Label(archivoSeleccionado.getName());
            Button btnQuitar = new Button("X");
            btnQuitar.setTooltip(new Tooltip("Quitar archivo"));

            hboxArchivoSeleccionado.getChildren().addAll(lblArchivo, btnQuitar);

            // Reemplazar botón de adjuntar por HBox archivo
            int index = hboxBotones.getChildren().indexOf(btn_adjuntar);
            hboxBotones.getChildren().set(index, hboxArchivoSeleccionado);

            // Evento para quitar archivo
            btnQuitar.setOnAction(ev -> quitarArchivo(index));
        } else {
            btn_adjuntar.setText("Adjuntar archivo");
        }
    }

    private void quitarArchivo(int index) {
        this.archivoSeleccionado = null;
        // Reemplazar HBox archivo por el botón de adjuntar original
        hboxBotones.getChildren().set(index, btn_adjuntar);
        hboxArchivoSeleccionado = null;
    }


    @FXML
  private void cancelar(ActionEvent event) {
    // Resetear combos
        cmb_espe.getSelectionModel().clearSelection();
        cmb_curso.getItems().clear();
        cmb_curso.setDisable(true);
        cmb_alumno.getItems().clear();
        cmb_alumno.setDisable(true);
        txt_caso.setDisable(true);

        // Resetear campos de texto
        txt_caso.clear();
        txt_estudiante.setText("");

        // Resetear botones
        btn_guardar.setDisable(true);
        btn_cancelar.setDisable(true);
        btn_adjuntar.setDisable(true);

        // Si hay HBox archivo, reemplazarlo por botón de adjuntar
        if (hboxArchivoSeleccionado != null) {
            int index = hboxBotones.getChildren().indexOf(hboxArchivoSeleccionado);
            if (index >= 0) {
                hboxBotones.getChildren().set(index, btn_adjuntar);
            }
            hboxArchivoSeleccionado = null;
        }

        archivoSeleccionado = null;
    }

    @FXML
    private void GuardarCaso(ActionEvent event)  {

        if(ControladorUtils.hayCamposVacios(txt_caso)){
            ControladorUtils.mostrarAlertaChill("Informamos", "No puede enviar un caso sin descripción. \n Describa el caso antes de enviar");
            return; 
        } 
        if(ControladorUtils.mostrarConfirmacion("Confirmar acción", "¿Desea guardar el caso?\n Esta acción no puede ser deshecha.")){
                // Validación tamaño archivo (última barrera)
            if (this.archivoSeleccionado != null && !ControladorUtils.validarTamanoArchivo(this.archivoSeleccionado, MAX_FILE_SIZE)) {
                // ya muestra la alerta desde validarTamanoArchivo
                return;
            }

            String descripcion = txt_caso.getText(); 
            int ciAlumno = cmb_alumno.getSelectionModel().getSelectedItem().getCi();
            int profe_CI = Integer.parseInt(profCI); 
            File archivo = this.archivoSeleccionado;
            try {
                int exito = profesorDao.insertarCaso(descripcion, profe_CI, ciAlumno, archivo);

                if (exito!=-1) {


                    String email = casoDao.getEmailEvaluadora(); 
                    String cuerpo = String.format(
                        "Hola,\n\n" +
                        "Se ha creado un nuevo caso en el sistema que requiere ser asignado a un miembro del equipo técnico.\n\n" +
                        "Detalles del caso:\n" +
                        "- ID del caso: %d\n" +
                        "- Descripción del caso: %s\n"+
                        "- Profesor que generó el caso: %s\n" +
                        "- Estudiante: %s\n\n" +
                        "Por favor, ingrese al sistema para asignar este caso.\n\n" +
                        "Saludos,\n" +
                        "Sistema de Seguimiento", exito, txt_caso.getText(), SessionManager.getInstance().getUsuarioDatos().getNombre() + " " + SessionManager.getInstance().getUsuarioDatos().getApellido(), 
                        txt_estudiante.getText());
                        EmailUtils.enviarCorreo(email, "Nuevo caso", cuerpo);
                        List<String> emails = new ArrayList<>(); 
                        emails = casoDao.getEmailsExceptoEvaluadora(); 

                        for (String emailDestinatario : emails) {
                            if(!emailDestinatario.equals(email)){
                                String asunto = "Nuevo caso disponible";
                                String mensaje = String.format(
                                "Hola,\n\n" +
                                "Se ha creado un nuevo caso en el sistema.\n\n" +
                                "Detalles del caso:\n" +
                                "- ID del caso: %d\n" +
                                "- Profesor que generó el caso: %s\n" +
                                "- Estudiante: %s\n\n" +
                                "Por favor, ingrese al sistema para revisar este caso.\n\n" +
                                "Saludos,\n" +
                                "Sistema de Seguimiento",
                                exito, 
                                SessionManager.getInstance().getUsuarioDatos().getNombre() + " " +
                                SessionManager.getInstance().getUsuarioDatos().getApellido(),
                                txt_estudiante.getText()
                                );

                            // Llamada a EmailUtils
                            EmailUtils.enviarCorreo(emailDestinatario, asunto, mensaje);
                            }
                            // Podés armar el mensaje según el caso

                        }

                        cmb_alumno.getSelectionModel().clearSelection();
                    // limpiar la variable de instancia y UI
                        this.archivoSeleccionado = null;
                    ControladorUtils.mostrarAlertaChill("Éxito", "El caso fue guardado correctamente.");
                    btn_imprimir.setDisable(false);

                    cmb_espe.setDisable(true);
                    cmb_curso.setDisable(true);
                    cmb_alumno.setDisable(true); 
                    txt_caso.setDisable(true);
                    btn_cancelar.setDisable(true);
                    btn_imprimir.setDisable(false); 

                } else {
                    ControladorUtils.mostrarError("Error", "No se pudo guardar el caso.", null);
                }

            } catch (Exception ex) {
                ControladorUtils.mostrarError("Excepción", "Ocurrió un error al guardar el caso.", ex);
            }        
        }
        
    }

    @FXML
    private void imprimir(ActionEvent event) {
    }

}
