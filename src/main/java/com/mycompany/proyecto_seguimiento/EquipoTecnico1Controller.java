package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.modelo.CasoResumen;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

public class EquipoTecnico1Controller implements Initializable {

    @FXML private Label label_title;
    @FXML private TableView<CasoResumen> tabla_caso;
    @FXML private TableColumn<CasoResumen, Integer> col_idCaso;
    @FXML private TableColumn<CasoResumen, LocalDateTime> col_fecha;
    @FXML private TableColumn<CasoResumen, String> col_estudiante;
    @FXML private TableColumn<CasoResumen, String> col_especialidad;
    @FXML private TableColumn<CasoResumen, String> col_curso;
    @FXML private TextField txt_buscar;
    @FXML private Button btn_abrir;
    @FXML private Button btn_volver;

    private final conexion dbConexion = new conexion();
    private final equipoTecnicoDAO equipoTecDao = new equipoTecnicoDAO(dbConexion.getConnection());
    private final CasoDAO casoDAO = new CasoDAO(dbConexion.getConnection()); 
    private ObservableList<CasoResumen> registros;
    ObservableList<CasoResumen> registrosFiltrados; 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_especialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));

        // Formato personalizado para fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        col_fecha.setCellFactory(column -> new TableCell<CasoResumen, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                setText((empty || fecha == null) ? null : fecha.format(formatter));
            }
        });

        btn_abrir.setDisable(true);

        mostrarDatos();

        // Listener para habilitar el botón al seleccionar fila
        tabla_caso.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btn_abrir.setDisable(newSelection == null);
        });
    }

    @FXML
    private void buscar(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();

        if (registros == null) return; // Evita nullpointer si tabla no cargó

        registrosFiltrados = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (CasoResumen caso : registros) {
            String fechaStr = (caso.getFecha() != null) ? caso.getFecha().format(formatter).toLowerCase() : "";
            String estudiante = (caso.getEstudiante() != null) ? caso.getEstudiante().toLowerCase() : "";
            String especialidad = (caso.getEspecialidad() != null) ? caso.getEspecialidad().toLowerCase() : "";
            String curso = (caso.getCurso() != null) ? caso.getCurso().toLowerCase() : "";

            if (busqueda.isEmpty() ||
                estudiante.contains(busqueda) ||
                especialidad.contains(busqueda) ||
                curso.contains(busqueda) ||
                fechaStr.contains(busqueda)) {
                registrosFiltrados.add(caso);
            }
        }

        tabla_caso.setItems(registrosFiltrados);
    }

    @FXML
    private void abrirCaso(ActionEvent event) {
        CasoResumen seleccionado = tabla_caso.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        CasoSeleccionado cs = CasoSeleccionado.getInstancia();
        cs.setIdCaso(seleccionado.getId_caso());
        cs.setFecha(seleccionado.getFecha());
        cs.setEstudiante(seleccionado.getEstudiante());
        cs.setEspecialidad(seleccionado.getEspecialidad());
        cs.setCurso(seleccionado.getCurso());
        cs.setFxmlAnterior("equipoTecnico1");
        System.out.println(seleccionado.isActivo());
        cs.setActivo(seleccionado.isActivo());
        casoDAO.cargarDetalleCaso(seleccionado.getId_caso());
        ControladorUtils.cambiarVista("AbrirCaso");
    }

    private void mostrarDatos() {
        registros = FXCollections.observableArrayList(equipoTecDao.obtenerCasosSinEquipo());
        tabla_caso.setItems(registros);
    }

    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("equipoTecnico");
    }
}
