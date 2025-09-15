package com.mycompany.proyecto_seguimiento;

import com.mycompany.proyecto_seguimiento.clases.CasoDAO;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.ProfesorDAO;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class Teacher2Controller implements Initializable {

    @FXML
    private TableView<CasoResumen> tabla_casos;
    @FXML
    private TableColumn<CasoResumen, Integer> col_idCaso;
    @FXML
    private TableColumn<CasoResumen, LocalDateTime> col_fecha;
    @FXML
    private TableColumn<CasoResumen, String> col_estudiante;
    @FXML
    private TableColumn<CasoResumen, String> col_especialidad;
    @FXML
    private TableColumn<CasoResumen, String> col_curso;
    @FXML
    private TableColumn<CasoResumen, Boolean> col_seleccion;
    @FXML
    private TextField txt_buscar;
    @FXML
    private Button btn_abrir;
    @FXML
    private Button btn_volver;

    private final SessionManager session = SessionManager.getInstance();
    private final conexion dbConexion = new conexion();
    
    private final ProfesorDAO profesorDao = new ProfesorDAO(dbConexion.getConnection());

    private final CasoDAO casoDAO = new CasoDAO(dbConexion.getConnection()); 
    private ObservableList<CasoResumen> registros; // Lista completa de casos
    @FXML
    private Button btn_informe;
    

    @Override
   public void initialize(URL url, ResourceBundle rb) {
    // Configurar columnas
        col_idCaso.setCellValueFactory(new PropertyValueFactory<>("id_caso"));
        col_fecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        col_estudiante.setCellValueFactory(new PropertyValueFactory<>("estudiante"));
        col_especialidad.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        col_curso.setCellValueFactory(new PropertyValueFactory<>("curso"));

        // 🔹 Formato personalizado para la columna fecha
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

        
        btn_abrir.setDisable(true);

        
        mostrarDatos();
    }

    /**
     * Carga todos los casos del profesor en la tabla y actualiza la lista completa.
     */
    private void mostrarDatos() {
        String profesorCI = session.getCiUsuario();
        registros = FXCollections.observableArrayList(
                profesorDao.obtenerCasosPorProfesor(profesorCI)
        );
        tabla_casos.setItems(registros);
    }

    @FXML
    private void mostrarFila(MouseEvent event) {
        btn_abrir.setDisable(tabla_casos.getSelectionModel().getSelectedItem() == null);
        
    }

    @FXML
    private void buscarCaso(KeyEvent event) {
        String busqueda = txt_buscar.getText().toLowerCase().trim();

        if (busqueda.isEmpty()) {
            tabla_casos.setItems(registros); // Mostrar todos los registros
        } else {
            ObservableList<CasoResumen> registrosFiltrados = FXCollections.observableArrayList();

            for (CasoResumen caso : registros) {
                if ((caso.getEstudiante() != null && caso.getEstudiante().toLowerCase().contains(busqueda)) ||
                    (caso.getEspecialidad() != null && caso.getEspecialidad().toLowerCase().contains(busqueda)) ||
                    (caso.getCurso() != null && caso.getCurso().toLowerCase().contains(busqueda)) ||
                    (caso.getFecha() != null && caso.getFecha().toString().toLowerCase().contains(busqueda))) {
                    registrosFiltrados.add(caso);
                }
            }

            tabla_casos.setItems(registrosFiltrados);
        }
    }

    @FXML
    private void volver(ActionEvent event) {
        
        ControladorUtils.cambiarVista("teacher1");
        
    }

    @FXML
    private void AbrirCaso(ActionEvent event) {

        CasoResumen seleccionado = tabla_casos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            
            CasoSeleccionado.getInstancia().setIdCaso(seleccionado.getId_caso());
            CasoSeleccionado.getInstancia().setFecha(seleccionado.getFecha());
            CasoSeleccionado.getInstancia().setEstudiante(seleccionado.getEstudiante());
            CasoSeleccionado.getInstancia().setCurso(seleccionado.getCurso());
            CasoSeleccionado.getInstancia().setFxmlAnterior("teacher2"); 
            casoDAO.cargarDetalleCaso(seleccionado.getId_caso());

            ControladorUtils.cambiarVista("AbrirCaso"); 
        }

    }

    @FXML
    private void generarInforme(ActionEvent event) {
    }
}
