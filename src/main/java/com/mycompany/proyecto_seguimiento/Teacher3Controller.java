package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.EmailUtils;
import com.mycompany.proyecto_seguimiento.clases.ProfesorDAO;
import com.mycompany.proyecto_seguimiento.clases.Reporte;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Alumno;
import com.mycompany.proyecto_seguimiento.modelo.Curso;
import com.mycompany.proyecto_seguimiento.modelo.Especialidad;

import java.io.File;
import java.net.URL;
import java.util.*;
import javafx.concurrent.Task;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Teacher3Controller implements Initializable {

    @FXML
    private TextArea txt_caso;
    @FXML
    private Button btn_guardar, btn_adjuntar, btn_cancelar, btn_imprimir;
    @FXML
    private ComboBox<Especialidad> cmb_espe;
    @FXML
    private ComboBox<Curso> cmb_curso;
    @FXML
    private ComboBox<Alumno> cmb_alumno;
    @FXML
    private Text txt_estudiante;

    @FXML
    private VBox vboxBotones; // Contenedor de botones principal (izquierda)
    @FXML
    private HBox hboxArchivoSeleccionado; // HBox que reemplaza botón de adjuntar

    private final SessionManager session = SessionManager.getInstance();
    private final conexion dbConexion = new conexion();
    private ProfesorDAO profesorDao = new ProfesorDAO(dbConexion.getConnection()); 
    private CasoDAO casoDao = new CasoDAO(dbConexion.getConnection()); 

    private final String profCI = session.getCiUsuario(); 
    private File archivoSeleccionado;

    private static final long MAX_FILE_SIZE = 16L * 1024 * 1024; // 16 MB
    private int idCaso; 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Especialidad> especialidades = profesorDao.obtenerEspecialidad(profCI);
        cmb_espe.getItems().clear();
        cmb_espe.getItems().addAll(especialidades);
        txt_caso.setDisable(true);
        cmb_curso.setDisable(true);
        cmb_alumno.setDisable(true);
        btn_adjuntar.setDisable(true);
        btn_guardar.setDisable(true);
        btn_cancelar.setDisable(true);
        btn_imprimir.setDisable(true);
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

            List<Curso> cursos = profesorDao.obtenerCursos(profCI, seleccion.getId());
            cmb_curso.getItems().clear();
            cmb_alumno.getItems().clear();
            cmb_alumno.setDisable(true);
            btn_adjuntar.setDisable(true);
            btn_guardar.setDisable(true);
            txt_estudiante.setText("");
            txt_caso.setDisable(true);
            cmb_curso.getItems().addAll(cursos);
        }
    }

    @FXML
    private void habilitarAlumno(ActionEvent event) {
        Curso seleccion = cmb_curso.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            cmb_alumno.setDisable(false);
            List<Alumno> alumnos = profesorDao.obtenerAlumnos(seleccion.getId());
            cmb_alumno.getItems().clear();
            cmb_alumno.getItems().addAll(alumnos);
            txt_estudiante.setText("");
            btn_adjuntar.setDisable(true);
            btn_guardar.setDisable(true);
            txt_caso.setDisable(true);
        }
    }

    @FXML
    private void cargarAlumno(ActionEvent event) {
        Alumno seleccion = cmb_alumno.getSelectionModel().getSelectedItem();
        if (seleccion != null) {
            txt_estudiante.setText(seleccion.getNombre());
            txt_caso.setDisable(false);
            btn_adjuntar.setDisable(false);
            btn_guardar.setDisable(false);
        }
    }

    @FXML
    private void cargarArchivo(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar archivo");
        File seleccionado = fileChooser.showOpenDialog(btn_adjuntar.getScene().getWindow());

        if (seleccionado != null && ControladorUtils.validarTamanoArchivo(seleccionado, MAX_FILE_SIZE)) {
            archivoSeleccionado = seleccionado;

            hboxArchivoSeleccionado.getChildren().clear();
            Label lblArchivo = new Label(archivoSeleccionado.getName());
            Button btnQuitar = new Button("X");
            btnQuitar.setTooltip(new Tooltip("Quitar archivo"));

            hboxArchivoSeleccionado.getChildren().addAll(lblArchivo, btnQuitar);

            btnQuitar.setOnAction(ev -> quitarArchivo());
        }
        btn_adjuntar.setDisable(true);
    }

    private void quitarArchivo() {
        archivoSeleccionado = null;
        hboxArchivoSeleccionado.getChildren().clear();
        btn_adjuntar.setDisable(false);
    }

    @FXML
    private void cancelar(ActionEvent event) {
        cmb_espe.getSelectionModel().clearSelection();
        cmb_curso.getItems().clear();
        cmb_curso.setDisable(true);
        cmb_alumno.getItems().clear();
        cmb_alumno.setDisable(true);
        txt_caso.clear();
        txt_caso.setDisable(true);
        txt_estudiante.setText("");
        btn_guardar.setDisable(true);
        btn_cancelar.setDisable(true);
        btn_adjuntar.setDisable(true);
        btn_imprimir.setDisable(true);
        quitarArchivo();
    }

    @FXML
    private void GuardarCaso(ActionEvent event) {
        if (ControladorUtils.hayCamposVacios(txt_caso)) {
            ControladorUtils.mostrarAlertaChill("Informamos", "No puede enviar un caso sin descripción.\nDescriba el caso antes de enviar");
            return;
        }

        if (!ControladorUtils.mostrarConfirmacion("Confirmar acción", "¿Desea guardar el caso?\nEsta acción no puede ser deshecha.")) {
            return;
        }

        if (archivoSeleccionado != null && !ControladorUtils.validarTamanoArchivo(archivoSeleccionado, MAX_FILE_SIZE)) {
            return;
        }

        try {
            int ciAlumno = cmb_alumno.getSelectionModel().getSelectedItem().getCi();
            int profe_CI = Integer.parseInt(profCI);

            int exito = profesorDao.insertarCaso(txt_caso.getText(), profe_CI, ciAlumno, archivoSeleccionado);
            idCaso = exito; 
            if (exito != -1) {

                // Bloquear campos tras guardar
                cmb_alumno.getSelectionModel().clearSelection();
                quitarArchivo();
                
                txt_caso.setDisable(true);
                btn_imprimir.setDisable(false);
                btn_cancelar.setDisable(true);
                btn_guardar.setDisable(true);
                btn_adjuntar.setDisable(true);
                cmb_espe.setDisable(true);
                cmb_curso.setDisable(true);
                cmb_alumno.setDisable(true);

                // Mostrar modal de carga
                Stage cargando = ControladorUtils.mostrarAlertaCargando(
                        "Enviando notificaciones",
                        "Notificando a los miembros del equipo técnico..."
                );

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        enviarNotificaciones(exito);
                        return null;
                    }
                };

                task.setOnSucceeded(e -> {
                    ControladorUtils.cerrarAlertaCargando();
                    ControladorUtils.mostrarAlertaChill("Éxito", "El caso fue guardado correctamente y las notificaciones fueron enviadas.");
                });

                task.setOnFailed(e -> {
                    ControladorUtils.cerrarAlertaCargando();
                    ControladorUtils.mostrarError("Error", "El caso fue guardado, pero ocurrió un problema al enviar notificaciones.", (Exception) task.getException());
                });

                new Thread(task).start();

            } else {
                ControladorUtils.mostrarError("Error", "No se pudo guardar el caso.", null);
            }

        } catch (Exception ex) {
            ControladorUtils.mostrarError("Excepción", "Ocurrió un error al guardar el caso.", ex);
        }
    }


    private void enviarNotificaciones(int casoId) throws Exception {
        String emailEvaluadora = casoDao.getEmailEvaluadora();

        // correo para la evaluadora
        String cuerpo = String.format(
                "Hola,\n\nSe ha creado un nuevo caso en el sistema que requiere ser asignado a un miembro del equipo técnico.\n\n" +
                        "Detalles del caso:\n- ID del caso: %d\n- Descripción: %s\n- Profesor: %s\n- Estudiante: %s\n\n" +
                        "Por favor, ingrese al sistema para asignar este caso.\n\nSaludos,\nSistema de Seguimiento",
                casoId,
                txt_caso.getText(),
                session.getUsuarioDatos().getNombre() + " " + session.getUsuarioDatos().getApellido(),
                txt_estudiante.getText()
        );

        EmailUtils.enviarCorreo(emailEvaluadora, "Nuevo caso", cuerpo);

        // correos para los demás (excepto la evaluadora)
        for (String emailDest : casoDao.getEmailsExceptoEvaluadora()) {
            if (!emailDest.equals(emailEvaluadora)) {
                String mensaje = String.format(
                        "Hola,\n\nSe ha creado un nuevo caso en el sistema.\n\n" +
                                "Detalles del caso:\n- ID: %d\n- Profesor: %s\n- Estudiante: %s\n\n" +
                                "Por favor, ingrese al sistema para revisar este caso.\n\nSaludos,\nSistema de Seguimiento",
                        casoId,
                        session.getUsuarioDatos().getNombre() + " " + session.getUsuarioDatos().getApellido(),
                        txt_estudiante.getText()
                );
                EmailUtils.enviarCorreo(emailDest, "Nuevo caso disponible", mensaje);
            }
        }
    }


    @FXML
    private void imprimir(ActionEvent event) {
        if(ControladorUtils.mostrarConfirmacion("Confirmar acción", "¿Desea generar un PDF de este caso?")){
             List<Integer> id_caso = new ArrayList<>(); 
            id_caso.add(idCaso); 
            Reporte reporte = new Reporte();
            reporte.generarYGuardarReporte(btn_imprimir.getScene().getWindow(), "caso_"+idCaso, "reporte_casos", id_caso);
            // Lógica para generar PDF
        }
       
    }
}
