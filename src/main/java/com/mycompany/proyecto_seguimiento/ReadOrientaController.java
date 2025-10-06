package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.OrientacionSelected;
import com.mycompany.proyecto_seguimiento.clases.Reporte;
import com.mycompany.proyecto_seguimiento.modelo.OrientacionResumen;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
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
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.input.MouseEvent;

public class ReadOrientaController implements Initializable {

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

    private ObservableList<OrientacionResumen> registros = FXCollections.observableArrayList();
    private ObservableList<OrientacionResumen> registrosFiltrados = FXCollections.observableArrayList();

    private Set<OrientacionResumen> seleccionados = new HashSet<>();
    @FXML
    private Button btn_informe;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // 1. Cargar todas las orientaciones
        try {
            registros.setAll(equipoTecDao.obtenerOrientaciones());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // 2. Configurar columnas
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

        // 4. Guardar en singleton solo las orientaciones del autor de la sesión
        String autorSesion = SessionManager.getInstance().getUsuarioDatos().getNombre() + 
                             " " + SessionManager.getInstance().getUsuarioDatos().getApellido();

        List<OrientacionResumen> orientacionesDelAutor = registros.stream()
                .filter(o -> o.getAutor() != null && o.getAutor().equalsIgnoreCase(autorSesion.trim()))
                .collect(Collectors.toList());

        OrientacionSelected.getInstancia().setOrientaciones(orientacionesDelAutor);

        // 5. Inicializar TableView con todas las orientaciones
        registrosFiltrados.setAll(registros);
        tabla_casos.setItems(registrosFiltrados);

        // 6. Inicializar botón
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
        if (seleccionados.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione al menos una orientación para generar el informe.");
            alert.showAndWait();
            return;
        }
        if(ControladorUtils.mostrarConfirmacion("Confirmar acción", "¿Desea generar un PDF?")){
            List<Integer> idsSeleccionados = seleccionados.stream()
                                          .map(OrientacionResumen::getCod_orientacion)
                                          .collect(Collectors.toList());

            // Llamar al generador de reportes
            Reporte reporte = new Reporte();
            reporte.generarYGuardarReporte(
                btn_informe.getScene().getWindow(),
                "/reporte/orientaR.jasper", // ruta al jasper de orientaciones
                "reporte_orientaciones",        // nombre del archivo generado
                idsSeleccionados
            );
        }
        // Extraer los códigos de orientación seleccionados
       
    }
    @FXML
    private void mostrarFila(MouseEvent event) {
        btn_informe.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);

    }
}