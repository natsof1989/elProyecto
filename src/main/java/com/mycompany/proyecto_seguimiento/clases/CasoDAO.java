/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import java.sql.Connection;
import com.mycompany.proyecto_seguimiento.clases.CasoSeleccionado;
import com.mycompany.proyecto_seguimiento.modelo.Tutores;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author natha
 */
public class CasoDAO {
    
    private final Connection conexion;

    public CasoDAO(Connection conexion) {
        this.conexion = conexion;
    }
    
    public void cargarDetalleCaso(int idCaso) {
    String sql = "SELECT * FROM seguimiento.vista_caso_detalle WHERE id_caso = ?";

    try (PreparedStatement stmt = conexion.prepareStatement(sql)) {

        stmt.setInt(1, idCaso);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                CasoSeleccionado caso = CasoSeleccionado.getInstancia();

                // Datos del caso
                caso.setDescripcion(rs.getString("descripcion"));
                caso.setArchivo(rs.getBytes("archivo"));
                caso.setExtension(rs.getString("extension") != null ? rs.getString("extension") : "");
                caso.setCiProfesor(String.valueOf(rs.getInt("profesor_CI")));
                caso.setNombreProfesor(rs.getString("profesor"));

                // Limpiar lista de tutores
                if (caso.getTutores() == null) {
                    caso.setTutores(new ArrayList<>());
                } else {
                    caso.getTutores().clear();
                }

                // Crear objetos Tutores y agregarlos a la lista
                Tutores tutor1 = new Tutores();
                tutor1.setGmail(rs.getString("correo_tutor1"));
                tutor1.setTelefono(rs.getString("telefono_tutor1"));
                caso.getTutores().add(tutor1);

                Tutores tutor2 = new Tutores();
                tutor2.setGmail(rs.getString("correo_tutor2"));
                tutor2.setTelefono(rs.getString("telefono_tutor2"));
                caso.getTutores().add(tutor2);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    
    public void aplicarCambiosAsignacion(int idCaso, List<Integer> aInsertar, List<Integer> aEliminar) throws SQLException {
        String insertSQL = "INSERT INTO det_caso (id_caso, equipo_tecnico_CI) VALUES (?, ?)";
        String deleteSQL = "DELETE FROM det_caso WHERE id_caso = ? AND equipo_tecnico_CI = ?";

        try {
            conexion.setAutoCommit(false);

            if (aInsertar != null && !aInsertar.isEmpty()) {
                try (PreparedStatement ps = conexion.prepareStatement(insertSQL)) {
                    for (Integer ci : aInsertar) {
                        ps.setInt(1, idCaso);
                        ps.setInt(2, ci);
                        ps.executeUpdate();
                    }
                }
            }

            if (aEliminar != null && !aEliminar.isEmpty()) {
                try (PreparedStatement ps = conexion.prepareStatement(deleteSQL)) {
                    for (Integer ci : aEliminar) {
                        ps.setInt(1, idCaso);
                        ps.setInt(2, ci);
                        ps.executeUpdate();
                    }
                }
            }

            conexion.commit();
        } catch (SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    

    
}
