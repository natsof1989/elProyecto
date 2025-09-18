package com.mycompany.proyecto_seguimiento;
import com.mycompany.proyecto_seguimiento.clases.SessionManager;
import com.mycompany.proyecto_seguimiento.clases.ControladorUtils;
import com.mycompany.proyecto_seguimiento.clases.ProfesorDAO;
import com.mycompany.proyecto_seguimiento.clases.UsuarioDAO;
import com.mycompany.proyecto_seguimiento.clases.conexion;
import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

public class Teacher1Controller implements Initializable {

    @FXML
    private Label txt_bienvenida;
    @FXML
    private Text txt_nombre;
    @FXML
    private Text txt_ci;
    @FXML
    private Button bt_verCaso;
    @FXML
    private Button bt_casoNew;
    @FXML
    private Button bt_orientaciones;
    @FXML
    private Button bt_configurar;
    @FXML
    private Button bt_cerrarSesion;

    private final int ci= Integer.parseInt(SessionManager.getInstance().getCiUsuario()); 
    private final conexion dbConexion = new conexion();
    private ProfesorDAO profesorDao = new ProfesorDAO(dbConexion.getConnection()); 
    @FXML
    private Button btn_casosEspe;
    @FXML
    private Button btn_ayuda;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txt_ci.setText(SessionManager.getInstance().getCiUsuario());
        txt_nombre.setText(SessionManager.getInstance().getUsuarioDatos().getNombre() + " " + SessionManager.getInstance().getUsuarioDatos().getApellido());
        SessionManager.getInstance().setPagina_anterior("teacher1");
        Especialidad espe = SessionManager.getInstance().getEspe(); 
        if(espe!=null){
            btn_casosEspe.setVisible(true); 
        } else{
            btn_casosEspe.setVisible(false);
        }
        
    }

    @FXML
    private void casosEnviados(ActionEvent event) {
        // Implementa navegación si lo necesitas, ejemplo:
        ControladorUtils.cambiarVista("teacher2");
    }

    @FXML
    private void casoNuevo(ActionEvent event) {
        // Implementa navegación si lo necesitas, ejemplo:
        ControladorUtils.cambiarVista("teacher3");
    }

    @FXML
    private void orientaciones(ActionEvent event) {
        // Implementa navegación si lo necesitas, ejemplo:
        ControladorUtils.cambiarVista("teacher5");
    }

    @FXML
    private void configurar(ActionEvent event) {
        
        ControladorUtils.cambiarVista("configurarCuenta");
    }

    @FXML
    private void logout(ActionEvent event) {
         if(ControladorUtils.mostrarConfirmacion("Confirmar logout", "¿Desea cerrar sesión?")){
                // Limpiar todo el estado de la sesión
           SessionManager.getInstance().limpiarSesion();

           // Cambiar a la vista de inicio de sesión
           ControladorUtils.cambiarVista("inicioSesion");
        }
    }


    @FXML
    private void abrirCasosEspe(ActionEvent event) {
        ControladorUtils.cambiarVista("teacher4");
    }

    @FXML
    private void ayuda(ActionEvent event) {
         String AyudaPath = getClass().getResource("/Ayuda/Seguimiento.chm").getPath(); 
        File file = new File(AyudaPath); 
        if(file.exists()){
            try{
                java.awt.Desktop.getDesktop().open(file);
            } catch(IOException ex) {
                Logger.getLogger(Teacher1Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else{
            System.out.println("El archivo CHM no existe");
        }
    }
}