package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.clases.equipoTecnicoDAO;
import com.mycompany.proyecto_seguimiento.modelo.CasoResumen;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class ReadCasosController implements Initializable {

    @FXML
    private TextField txt_buscar;
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
    private TableColumn<CasoResumen, String> col_departamentos;
    @FXML
    private TableColumn<CasoResumen, Boolean> col_seleccion; // NUEVA columna para checkboxes
    @FXML
    private Button btn_abrirCaso;
    @FXML
    private Button btn_informe;

    private final conexion dbConexion = new conexion();
    private final equipoTecnicoDAO equipoTecDao = new equipoTecnicoDAO(dbConexion.getConnection());
    private final CasoDAO casoDAO = new CasoDAO(dbConexion.getConnection());

    private ObservableList<CasoResumen> registros = FXCollections.observableArrayList();
    private ObservableList<CasoResumen> registrosFiltrados = FXCollections.observableArrayList();

    // Para llevar registro de los seleccionados amigablemente
    private Set<CasoResumen> seleccionados = new HashSet<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configuración de columnas y datos
        try {
            registros.addAll(equipoTecDao.obtenerTodosLosCasosConDepartamentos());
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_espe.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        col_estado.setCellValueFactory(new PropertyValueFactory<>("activo"));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        col_fecha.setCellFactory(column -> new TableCell<CasoResumen, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime fecha, boolean empty) {
                super.updateItem(fecha, empty);
                if (empty || fecha == null) {
                    setText(null);
                } else {
                    setText(fecha.format(formatter));
                }
            }
        });

        col_estado.setCellFactory(column -> new TableCell<CasoResumen, Boolean>() {
            @Override
            protected void updateItem(Boolean activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                } else {
                    setText(activo ? "Activo" : "Cerrado");
                }
            }
        });

        col_departamentos.setCellValueFactory(cellData -> {
            var lista = cellData.getValue().getDepartamentos();
            String deps = (lista != null) ? String.join(", ", lista) : "";
            return new SimpleStringProperty(deps);
        });

        // NUEVA columna de checkboxes para seleccionar amigablemente
        col_seleccion.setCellValueFactory(data -> {
            // No se usa para binding, el cellFactory se encarga
            return new SimpleBooleanProperty(false);
        });
        col_seleccion.setCellFactory(tc -> new TableCell<CasoResumen, Boolean>() {
            private final CheckBox check = new CheckBox();
            {
                check.setOnAction(e -> {
                    CasoResumen caso = getTableRow().getItem();
                    if (caso != null) {
                        if (check.isSelected()) {
                            seleccionados.add(caso);
                        } else {
                            seleccionados.remove(caso);
                        }
                        actualizarBotones();
                    }
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    CasoResumen caso = getTableRow().getItem();
                    check.setSelected(seleccionados.contains(caso));
                    setGraphic(check);
                }
            }
        });

        registrosFiltrados.setAll(registros);
        tabla_casos.setItems(registrosFiltrados);

        btn_abrirCaso.setDisable(true);
        btn_informe.setDisable(true);
    }

    private void actualizarBotones() {
        int sel = seleccionados.size();
        btn_informe.setDisable(sel == 0);
        btn_abrirCaso.setDisable(sel != 1);
    }

   

    
    @FXML
    private void buscar(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();

        if (registros == null) return; // Evita NullPointer si aún no hay datos

        ObservableList<CasoResumen> registrosFiltrados = FXCollections.observableArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (CasoResumen caso : registros) {
            // Fecha como String (segura con null-check)
            

            String fechaStr = (caso.getFecha() != null) 
            ? caso.getFecha().format(formatter).toLowerCase()
            : "";
            String estudiante   = (caso.getEstudiante() != null)   ? caso.getEstudiante().toLowerCase()   : "";
            String especialidad = (caso.getEspecialidad() != null) ? caso.getEspecialidad().toLowerCase() : "";
            String curso        = (caso.getCurso() != null)        ? caso.getCurso().toLowerCase()        : "";

            // Unir departamentos en un solo string
            String departamentos = (caso.getDepartamentos() != null) 
                    ? String.join(", ", caso.getDepartamentos()).toLowerCase()
                    : "";

            String idCaso = String.valueOf(caso.getId_caso());

            // Condiciones de filtrado
            if (busqueda.isEmpty()
                    || estudiante.contains(busqueda)
                    || especialidad.contains(busqueda)
                    || curso.contains(busqueda)
                    || departamentos.contains(busqueda)
                    || fechaStr.contains(busqueda)
                    || idCaso.contains(busqueda)) {

                registrosFiltrados.add(caso);
            }
        }

        tabla_casos.setItems(registrosFiltrados);
    }


    @FXML
    private void abrirCaso(ActionEvent event) {
        CasoResumen seleccionado;

        if (!seleccionados.isEmpty()) {
            // Prioridad al checkbox
            seleccionado = seleccionados.iterator().next();
        } else {
            // Tomar fila seleccionada
            seleccionado = tabla_casos.getSelectionModel().getSelectedItem();
        }

        if (seleccionado == null) return; // nada seleccionado

        CasoSeleccionado cs = CasoSeleccionado.getInstancia();
        cs.setIdCaso(seleccionado.getId_caso());
        cs.setFecha(seleccionado.getFecha());
        cs.setEstudiante(seleccionado.getEstudiante());
        cs.setCurso(seleccionado.getCurso());
        cs.setEspecialidad(seleccionado.getEspecialidad());
        cs.setFxmlAnterior("equipoTecnico4");
        cs.setActivo(seleccionado.isActivo());
        casoDAO.cargarDetalleCaso(seleccionado.getId_caso());
        ControladorUtils.cambiarVista("abrirCaso");
    }


    @FXML
    private void generarInforme(ActionEvent event) {
        if (seleccionados.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Seleccione al menos un caso para generar el informe.");
            alert.showAndWait();
            return;
        }

        // Aquí tu lógica para JasperReports:
        // JasperReportUtils.generarInformeCasos(seleccionados);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Informe generado para " + seleccionados.size() + " casos (aquí iría Jasper).");
        alert.showAndWait();
    }

    @FXML
    private void mostrarFila(MouseEvent event) {
        btn_abrirCaso.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);
        btn_informe.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);
        
    }

}