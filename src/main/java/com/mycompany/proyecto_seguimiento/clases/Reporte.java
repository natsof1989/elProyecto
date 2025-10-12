package com.mycompany.proyecto_seguimiento.clases;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.Level;

import java.util.stream.Collectors;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperExportManager;

public class Reporte extends conexion {

    public Reporte() {}

    public void generarYGuardarReporte(Window ventana, String ubicacion, String nombreSugerido, List<Integer> idsCasos) {
        try {
            if (idsCasos == null || idsCasos.isEmpty()) {
                Logger.getLogger(Reporte.class.getName()).log(Level.WARNING, "No se seleccionaron elementos");
                return;
            }

            // Convertir lista de IDs a cadena separada por comas
            String idsString = idsCasos.stream()
                                       .map(String::valueOf)
                                       .collect(Collectors.joining(","));

            // Parámetros del reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("IDS", idsString);

            // Generar el reporte
            String reportPath = getClass().getResource(ubicacion).getPath();
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportPath, parametros, getConnection());

            // Exportar a PDF en memoria
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            byte[] archivoPDF = baos.toByteArray();

            // Diálogo para guardar el archivo
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar reporte de casos");
            fileChooser.setInitialFileName(nombreSugerido + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));

            File destino = fileChooser.showSaveDialog(ventana);
            if (destino != null) {
                try (FileOutputStream fos = new FileOutputStream(destino)) {
                    fos.write(archivoPDF);
                    Logger.getLogger(Reporte.class.getName()).log(Level.INFO, "Reporte guardado en: " + destino.getAbsolutePath());

                    // Abrir el archivo PDF automáticamente
                    java.awt.Desktop.getDesktop().open(destino);
                } catch (Exception e) {
                    Logger.getLogger(Reporte.class.getName()).log(Level.SEVERE, "Error al guardar o abrir el archivo", e);
                }
            }

        } catch (JRException ex) {
            Logger.getLogger(Reporte.class.getName()).log(Level.SEVERE, "Error JasperReports", ex);
        } catch (Exception ex) {
            Logger.getLogger(Reporte.class.getName()).log(Level.SEVERE, "Error general", ex);
        }
    }

}
