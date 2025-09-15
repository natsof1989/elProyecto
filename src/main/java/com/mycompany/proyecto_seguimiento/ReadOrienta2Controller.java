package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.OrientacionSelected;
import com.mycompany.proyecto_seguimiento.modelo.OrientacionResumen;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class ReadOrienta2Controller implements Initializable {

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
    @FXML
    private Button btn_informe;

    private ObservableList<OrientacionResumen> registros = FXCollections.observableArrayList();
    private ObservableList<OrientacionResumen> registrosFiltrados = FXCollections.observableArrayList();
    private Set<OrientacionResumen> seleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Obtener la lista de orientaciones del singleton
        List<OrientacionResumen> orientacionesDelAutor = OrientacionSelected.getInstancia().getOrientaciones();

        registros.setAll(orientacionesDelAutor);

        // 2. Configurar columnas
        col_idOrienta.setCellValueFactory(new PropertyValueFactory<>("cod_orientacion"));
        col_autor.setCellValueFactory(new PropertyValueFactory<>("autor"));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_fecha.setCellFactory(column -> new TableCell<OrientacionResumen, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(sdf.format(item));
            }
        });

        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_espe.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));

        // 3. Configurar checkboxes de selección
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

    // 4. Inicializar TableView con datos del singleton
    registrosFiltrados.setAll(registros);
    tabla_casos.setItems(registrosFiltrados);

    btn_informe.setDisable(true);
}


    private void actualizarBoton() {
        btn_informe.setDisable(seleccionados.isEmpty());
    }

    @FXML
    private void buscar(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();
        registrosFiltrados.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (OrientacionResumen ori : registros) {
            String fechaStr = (ori.getFecha() != null) ? ori.getFecha().toLocalDateTime().format(formatter).toLowerCase() : "";
            String estudiante = (ori.getEstudiante() != null) ? ori.getEstudiante().toLowerCase() : "";
            String espe = (ori.getEspecialidad() != null) ? ori.getEspecialidad().toLowerCase() : "";
            String curso = (ori.getCurso() != null) ? ori.getCurso().toLowerCase() : "";
            String autor = (ori.getAutor() != null) ? ori.getAutor().toLowerCase() : "";
            String idCaso = String.valueOf(ori.getId_caso());
            String idOrienta = String.valueOf(ori.getCod_orientacion());

            if (busqueda.isEmpty()
                    || estudiante.contains(busqueda)
                    || espe.contains(busqueda)
                    || curso.contains(busqueda)
                    || autor.contains(busqueda)
                    || fechaStr.contains(busqueda)
                    || idCaso.contains(busqueda)
                    || idOrienta.contains(busqueda)) {
                registrosFiltrados.add(ori);
            }
        }

        tabla_casos.setItems(registrosFiltrados);
        seleccionados.clear();
        actualizarBoton();
    }

    @FXML
    private void generarInforme(ActionEvent event) {
        if (seleccionados.isEmpty()) return;
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION,
                "Generar informe para " + seleccionados.size() + " orientaciones."
        );
        alert.showAndWait();
    }

    @FXML
    private void mostrarFila(MouseEvent event) {
        btn_informe.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);
        
    }
}
