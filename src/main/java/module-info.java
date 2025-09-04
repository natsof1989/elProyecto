module com.mycompany.proyecto_seguimiento {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.sql; 
    requires java.base; 
    requires java.mail;
    opens com.mycompany.proyecto_seguimiento to javafx.fxml;
    exports com.mycompany.proyecto_seguimiento;
    requires jbcrypt;
    
    // abre también el paquete de clases si EmailUtils está en otro
    opens com.mycompany.proyecto_seguimiento.clases to javafx.fxml;
    
    opens com.mycompany.proyecto_seguimiento.modelo to javafx.base;
    requires java.desktop;
}
