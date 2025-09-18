module com.mycompany.proyecto_seguimiento {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;
    requires java.mail;
    requires jbcrypt;

    opens com.mycompany.proyecto_seguimiento to javafx.fxml;
    opens com.mycompany.proyecto_seguimiento.clases to javafx.fxml;
    opens com.mycompany.proyecto_seguimiento.modelo to javafx.base;
    requires java.desktop;

    exports com.mycompany.proyecto_seguimiento;
    // si necesitas usar clases de estos paquetes desde fuera del módulo:
    exports com.mycompany.proyecto_seguimiento.clases;
    exports com.mycompany.proyecto_seguimiento.modelo;
}
