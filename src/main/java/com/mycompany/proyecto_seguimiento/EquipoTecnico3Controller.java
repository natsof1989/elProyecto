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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

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
    private TableColumn<CasoResumen, Boolean> col_seleccionar;
    @FXML
    private Button btn_abrir;
    @FXML
    private Button btn_informe;
    @FXML
    private TextField txt_buscar;

    private final conexion dbConexion = new conexion();
    private final equipoTecnicoDAO equipoTecDao = new equipoTecnicoDAO(dbConexion.getConnection());
    private final CasoDAO casoDAO = new CasoDAO(dbConexion.getConnection());

    private ObservableList<CasoResumen> registros = FXCollections.observableArrayList();
    private Set<CasoResumen> seleccionados = new HashSet<>();
    private String ci_equipo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ci_equipo = SessionManager.getInstance().getCiUsuario();

        // Columnas fijas
        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_espe.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));
        col_estado.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Formato fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        col_fecha.setCellFactory(col -> new TableCell<CasoResumen, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(formatter));
            }
        });

        // Mostrar Activo/Inactivo
        col_estado.setCellFactory(col -> new TableCell<CasoResumen, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : (item ? "Activo" : "Inactivo"));
            }
        });

        // Columna de selección con checkboxes
        inicializarColumnaSeleccion();

        btn_abrir.setDisable(true);
        btn_informe.setDisable(true);

        try {
            cargarDatos();
        } catch (SQLException ex) {
            ControladorUtils.mostrarAlerta("Error", "No se pudieron cargar los casos");
        }
    }

    private void inicializarColumnaSeleccion() {
        col_seleccionar.setCellValueFactory(param -> new javafx.beans.property.SimpleBooleanProperty(seleccionados.contains(param.getValue())));
        col_seleccionar.setCellFactory(tc -> new TableCell<CasoResumen, Boolean>() {
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
    }

    private void actualizarBotones() {
        btn_informe.setDisable(seleccionados.isEmpty());
        btn_abrir.setDisable(seleccionados.size() != 1);
    }

    private void cargarDatos() throws SQLException {
        registros.setAll(equipoTecDao.obtenerCasosAsignados(ci_equipo));
        tabla_casos.setItems(registros);
        seleccionados.clear();
        actualizarBotones();
    }

    @FXML
    private void buscar(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();
        ObservableList<CasoResumen> filtrados = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (CasoResumen caso : registros) {
            String fechaStr = (caso.getFecha() != null) ? caso.getFecha().format(formatter).toLowerCase() : "";
            String estudiante = (caso.getEstudiante() != null) ? caso.getEstudiante().toLowerCase() : "";
            String espe = (caso.getEspecialidad() != null) ? caso.getEspecialidad().toLowerCase() : "";
            String curso = (caso.getCurso() != null) ? caso.getCurso().toLowerCase() : "";
            String estado = (caso.isActivo()) ? "activo" : "inactivo";

            if (busqueda.isEmpty() || estudiante.contains(busqueda) || espe.contains(busqueda)
                    || curso.contains(busqueda) || fechaStr.contains(busqueda) || estado.contains(busqueda)) {
                filtrados.add(caso);
            }
        }

        tabla_casos.setItems(filtrados);
        seleccionados.clear();
        actualizarBotones();
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
        if (seleccionados.isEmpty()) return;
        ControladorUtils.mostrarAlerta("Informe", "Generando informe para " + seleccionados.size() + " caso(s).");
    }

    private void volver(ActionEvent event) {
        ControladorUtils.cambiarVista("equipoTecnico");
    }

    @FXML
    private void mostrarFila(MouseEvent event) {
        btn_abrir.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);
        btn_informe.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);
        
    }
    
}
