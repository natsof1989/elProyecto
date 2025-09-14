/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.modelo.CasoResumen;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author natha
 */
public class EquipoTecnico3Controller implements Initializable {

    @FXML
    private TableView<CasoResumen> tabla_casos;
    @FXML
    private TableColumn<CasoResumen, Integer> col_idCaso;
    @FXML
    private TableColumn<CasoResumen, LocalDateTime> col_fecha;
    @FXML
    private TableColumn<CasoResumen, String> col_estudiante;
    @FXML
    private TableColumn<CasoResumen, String> col_espe;
    @FXML
    private TableColumn<CasoResumen, String> col_curso;
    @FXML
    private TableColumn<CasoResumen, Boolean> col_estado;
    @FXML
    private Button btn_abrir;

    /**
     * Initializes the controller class.
     */
    private final conexion dbConexion = new conexion();
    private final equipoTecnicoDAO equipoTecDao = new equipoTecnicoDAO(dbConexion.getConnection());
    private final CasoDAO casoDAO = new CasoDAO(dbConexion.getConnection()); 
    private ObservableList<CasoResumen> registros;
    ObservableList<CasoResumen> registrosFiltrados; 
    private String ci_equipo;
    @FXML
    private TextField txt_buscar;

    @Override
   public void initialize(URL url, ResourceBundle rb) {
        // Asignación correcta de ci_equipo si no es final
        ci_equipo = SessionManager.getInstance().getCiUsuario(); 

        // Columnas
        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_espe.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        col_estado.setCellValueFactory(new PropertyValueFactory<>("activo")); // 🔹 Esto faltaba

        // Formato personalizado para fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        col_fecha.setCellFactory(column -> new TableCell<CasoResumen, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText((empty || fecha == null) ? null : fecha.format(formatter));
            }
        });

        // Mostrar "Activo"/"Inactivo" en col_estado
        col_estado.setCellFactory(column -> new TableCell<CasoResumen, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                } else {
                    setText(activo ? "Activo" : "Inactivo");
                }
            }
        });

        btn_abrir.setDisable(true);

        try {
            mostrarDatos(); // tu método que carga la lista de CasoResumen
        } catch (SQLException ex) {
            Logger.getLogger(EquipoTecnico3Controller.class.getName()).log(Level.SEVERE, null, ex);
            ControladorUtils.mostrarAlerta("Error", "Los datos no pudieron cargarse");
        }

        // Listener para habilitar el botón al seleccionar fila
        tabla_casos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btn_abrir.setDisable(newSelection == null);
        });
    }

        
     private void mostrarDatos() throws SQLException {
        registros = FXCollections.observableArrayList(equipoTecDao.obtenerCasosAsignados(ci_equipo));
        tabla_casos.setItems(registros);
    }
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("equipoTecnico");
    }

   

     @FXML
    private void buscar(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();

        if (registros == null) return; // Evita NullPointer si aún no hay datos

        ObservableList<CasoResumen> registrosFiltrados = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (CasoResumen caso : registros) {
            String fechaStr = (caso.getFecha() != null) ? caso.getFecha().format(formatter).toLowerCase() : "";
            String estudiante = (caso.getEstudiante() != null) ? caso.getEstudiante().toLowerCase() : "";
            String especialidad = (caso.getEspecialidad() != null) ? caso.getEspecialidad().toLowerCase() : "";
            String curso = (caso.getCurso() != null) ? caso.getCurso().toLowerCase() : "";
            String estado = caso.isActivo() ? "activo" : "inactivo";

            if (busqueda.isEmpty()
                    || estudiante.contains(busqueda)
                    || especialidad.contains(busqueda)
                    || curso.contains(busqueda)
                    || fechaStr.contains(busqueda)
                    || estado.contains(busqueda)) {
                registrosFiltrados.add(caso);
            }
        }

        tabla_casos.setItems(registrosFiltrados);
    }



    @FXML
    private void abrirCaso(ActionEvent event) {
        CasoResumen seleccionado = tabla_casos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        CasoSeleccionado cs = CasoSeleccionado.getInstancia();
        cs.setIdCaso(seleccionado.getId_caso());
        cs.setFecha(seleccionado.getFecha());
        cs.setEstudiante(seleccionado.getEstudiante());
        cs.setCurso(seleccionado.getCurso());
        cs.setEspecialidad(seleccionado.getEspecialidad());
        cs.setFxmlAnterior("equipoTecnico4");
        cs.setActivo(seleccionado.isActivo());
        casoDAO.cargarDetalleCaso(seleccionado.getId_caso());
        ControladorUtils.cambiarVista("AbrirCaso");
    }
    
}
