package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.OrientacionSelected;
import com.mycompany.proyecto_seguimiento.clases.ProfesorDAO;
import com.mycompany.proyecto_seguimiento.modelo.OrientacionResumen;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.input.MouseEvent;

public class Teacher5Controller implements Initializable {

    @FXML
    private TextField txt_buscar;
    @FXML
    private TableView<OrientacionResumen> tabla_casos;
    @FXML
    private TableColumn<OrientacionResumen, Integer> col_idOrienta;
    @FXML
    private TableColumn<OrientacionResumen, String> col_autor;
    @FXML
    private TableColumn<OrientacionResumen, Timestamp> col_fecha;
    @FXML
    private TableColumn<OrientacionResumen, String> col_estudiante;
    @FXML
    private TableColumn<OrientacionResumen, String> col_espe;
    @FXML
    private TableColumn<OrientacionResumen, String> col_curso;
    @FXML
    private TableColumn<OrientacionResumen, Integer> col_idCaso;
    @FXML
    private TableColumn<OrientacionResumen, Boolean> col_seleccionar;
    
    
            
    private final conexion dbConexion = new conexion();
    private final equipoTecnicoDAO equipoTecDao = new equipoTecnicoDAO(dbConexion.getConnection());
    private ProfesorDAO profesorDao = new ProfesorDAO(dbConexion.getConnection()); 

    private ObservableList<OrientacionResumen> registros = FXCollections.observableArrayList();
    private final String profCI = SessionManager.getInstance().getCiUsuario(); 
    private ObservableList<OrientacionResumen> registrosFiltrados = FXCollections.observableArrayList();

    private Set<OrientacionResumen> seleccionados = new HashSet<>();
    @FXML
    private Button btn_informe;
    @FXML
    private Button btn_volver;
    private List<Especialidad> espes = new ArrayList<>(); 

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // 1. Obtén las especialidades del profesor
            espes = profesorDao.obtenerEspecialidad(profCI);
            // 2. Obtén todas las orientaciones
            registros.setAll(equipoTecDao.obtenerOrientaciones());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // 3. Configura las columnas
        col_idOrienta.setCellValueFactory(new PropertyValueFactory<>("cod_orientacion"));
        col_autor.setCellValueFactory(new PropertyValueFactory<>("autor"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_fecha.setCellFactory(column -> new TableCell<OrientacionResumen, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : sdf.format(item));
            }
        });

        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_espe.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));

        // 4. Configura la columna de checkboxes
        col_seleccionar.setCellValueFactory(data -> new SimpleBooleanProperty(false));
        col_seleccionar.setCellFactory(tc -> new TableCell<OrientacionResumen, Boolean>() {
            private final CheckBox check = new CheckBox();
            {
                check.setOnAction(e -> {
                    OrientacionResumen ori = getTableRow().getItem();
                    if (ori != null) {
                        if (check.isSelected()) seleccionados.add(ori);
                        else seleccionados.remove(ori);
                        actualizarBoton();
                    }
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    OrientacionResumen ori = getTableRow().getItem();
                    check.setSelected(seleccionados.contains(ori));
                    setGraphic(check);
                }
            }
        });

        // 5. Filtra orientaciones por especialidad del profesor
        ObservableList<OrientacionResumen> registrosEspecialidad = FXCollections.observableArrayList();
        List<String> nombresEspecialidades = espes.stream()
            .map(e -> e.getNombre().toLowerCase().trim())
            .collect(Collectors.toList());
        for (OrientacionResumen ori : registros) {
            if (ori.getEspecialidad() != null &&
                nombresEspecialidades.contains(ori.getEspecialidad().toLowerCase().trim())) {
                registrosEspecialidad.add(ori);
            }
        }
        tabla_casos.setItems(registrosEspecialidad);

        // 6. Inicializa el botón
        btn_informe.setDisable(true);
    }
    private void actualizarBoton() {
        btn_informe.setDisable(seleccionados.isEmpty());
    }

   

   @FXML
    private void buscar(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();

        if (registros == null) return; // Evita NullPointer si aún no hay datos

        ObservableList<OrientacionResumen> registrosFiltrados = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (OrientacionResumen ori : registros) {
            // Convertir Timestamp a String legible
            String fechaStr = "";
            if (ori.getFecha() != null) {
                fechaStr = ori.getFecha()
                              .toLocalDateTime()  // de Timestamp a LocalDateTime
                              .format(formatter)
                              .toLowerCase();
            }

            String estudiante = (ori.getEstudiante() != null) ? ori.getEstudiante().toLowerCase() : "";
            String especialidad = (ori.getEspecialidad() != null) ? ori.getEspecialidad().toLowerCase() : "";
            String curso = (ori.getCurso() != null) ? ori.getCurso().toLowerCase() : "";
            String autor = (ori.getAutor() != null) ? ori.getAutor().toLowerCase() : "";
            String idCaso = String.valueOf(ori.getId_caso());
            String idOrienta = String.valueOf(ori.getCod_orientacion());

            if (busqueda.isEmpty()
                    || estudiante.contains(busqueda)
                    || especialidad.contains(busqueda)
                    || curso.contains(busqueda)
                    || autor.contains(busqueda)
                    || fechaStr.contains(busqueda)
                    || idCaso.contains(busqueda)
                    || idOrienta.contains(busqueda)) {
                registrosFiltrados.add(ori);
            }
        }

        tabla_casos.setItems(registrosFiltrados);
    }


    

    @FXML
    private void generarInforme(ActionEvent event) {
        if (seleccionados.isEmpty()) return;

        // Aquí tu lógica: generar informe de las seleccionadas (o lo que quieras hacer)
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Generar informe para " + seleccionados.size() + " orientaciones.");
        alert.showAndWait();
        // Ejemplo: JasperReportUtils.generarInformeOrientaciones(seleccionados);
    }
    @FXML
    private void mostrarFila(MouseEvent event) {
        btn_informe.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);

    }

    @FXML
    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("teacher1");
    }
}