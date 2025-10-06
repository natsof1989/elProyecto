package com.mycompany.proyecto_seguimiento.clases;

import com.mycompany.proyecto_seguimiento.App;
import javafx.geometry.Insets;


import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ControladorUtils {
    // Para uso directo con nodos contenedores
    public static void cambiarVista(String fxmlName) {
        try {
            App.setRoot(fxmlName);

            // Obtener el Stage desde la escena
            Stage stage = (Stage) App.scene.getWindow();
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);

        } catch (IOException e) {
            mostrarError("Error", "No se pudo cambiar la vista", e);
        }
    }

   public static void cargarVistaStackPane(String fxmlName, StackPane contenedor) {
    try {
        Parent nuevaVista = FXMLLoader.load(App.class.getResource(fxmlName));
        
        // Asegurar que ocupa todo el stackpane
        if (nuevaVista instanceof Region) {
            ((Region) nuevaVista).setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        }
        StackPane.setAlignment(nuevaVista, Pos.CENTER);
        
        // Animación
        nuevaVista.setOpacity(0);
        contenedor.getChildren().add(nuevaVista);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), nuevaVista);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(e -> {
            if (contenedor.getChildren().size() > 1) {
                contenedor.getChildren().remove(0);
            }
        });
        fadeIn.play();
        
    } catch (IOException e) {
        mostrarError("Error", "No se pudo cargar la vista: " + fxmlName, e);
    }
}

    public static void abrirModal(String fxmlNombre, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(ControladorUtils.class.getResource("/com/mycompany/proyecto_seguimiento/" + fxmlNombre + ".fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // esto lo hace modal
            stage.setResizable(false);

            stage.showAndWait(); // espera hasta que se cierre la ventana
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Stage loadingStage;

    public static Stage mostrarAlertaCargando(String titulo, String mensaje) {
        if (loadingStage != null && loadingStage.isShowing()) return loadingStage;

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

        Label label = new Label(mensaje);
        VBox box = new VBox(10, spinner, label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Scene scene = new Scene(box, 300, 120);

        loadingStage = new Stage();
        loadingStage.initModality(Modality.APPLICATION_MODAL);
        loadingStage.setTitle(titulo);
        loadingStage.setScene(scene);
        loadingStage.setResizable(false);
        loadingStage.show();

        return loadingStage;
    }

    public static void cerrarAlertaCargando() {
        if (loadingStage != null) {
            Platform.runLater(() -> {
                loadingStage.close();
                loadingStage = null;
            });
        }
    }


    public static void mostrarAlerta(String titulo, String mensaje) {
        new Alert(Alert.AlertType.ERROR, mensaje).showAndWait();
    }
    public static void mostrarAlertaChill(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("Aviso");  // Esto elimina el "Message" por defecto
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public static void mostrarError(String titulo, String mensaje, Exception ex) {
        // Log del error
        System.err.println("ERROR [" + titulo + "]: " + mensaje);
        if (ex != null) {
            ex.printStackTrace();
        }

        // Mostrar alerta
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
}
    // En tu clase ControladorUtils
    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        // Personalizar botones (opcional)
        ButtonType buttonTypeYes = new ButtonType("Sí", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        // Mostrar diálogo y esperar respuesta
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;
    }

    // Retorna true si alguno de los campos está vacío (trim para ignorar espacios)
    public static boolean hayCamposVacios(TextInputControl... campos) {
        for (TextInputControl campo : campos) {
            if (campo.getText() == null || campo.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean validarNumero(String texto, String tipo) {
        
        // Eliminar espacios y guiones si los hubiera
        String textoLimpio = texto.replaceAll("[\\s-]", "");
        
        try {
            // Verificar que sean solo dígitos
            Long.parseLong(textoLimpio);
        } catch (NumberFormatException e) {
            return false;
        }
        
        // Validar según el tipo
        switch(tipo.toUpperCase()) {
            case "CI":
                // Validar cédula uruguaya (8 dígitos)
                return textoLimpio.length() == 7;
                
            case "TEL":
                // Validar teléfono uruguayo (8 o 9 dígitos)
                return textoLimpio.length() == 8 || textoLimpio.length() == 9;
                
            default:
                return false;
        }
    }
    
     public static boolean contieneSoloLetras(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }
        // Expresión regular que permite:
        // - Letras mayúsculas y minúsculas (a-z, A-Z)
        // - Vocales con acentos (áéíóúÁÉÍÓÚ)
        // - Letra ñ/Ñ
        // - Espacios en blanco
        return texto.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$");
    }
     
    public static boolean validarCamposLetras(TextInputControl... campos) {
        for (TextInputControl campo : campos) {
            if (campo.getText() == null || !contieneSoloLetras(campo.getText())) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean validarCorreo(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            mostrarAlerta("Error", "Formato de correo inválido");
            return false;
        }

        return true;
    }
    
    // tamaño en bytes: 16 MB = 16 * 1024 * 1024
    public static boolean validarTamanoArchivo(File file, long maxBytes) {
        if (file == null) return true; // nada que validar
        if (file.length() > maxBytes) {
            long mb = maxBytes / (1024 * 1024);
            String msg = String.format("El archivo supera el tamaño máximo permitido de %d MB. Seleccione un archivo más pequeño.", mb);
            mostrarAlertaChill("Archivo demasiado grande", msg);
            return false;
        }
        return true;
    }

}