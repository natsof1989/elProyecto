/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;


import com.mycompany.proyecto_seguimiento.modelo.Alumno;
import com.mycompany.proyecto_seguimiento.modelo.CasoResumen;
import com.mycompany.proyecto_seguimiento.modelo.Curso;
import com.mycompany.proyecto_seguimiento.modelo.Especialidad;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

/**
 *
 * @author natha
 */
public class ProfesorDAO {
    private final Connection conexion;

    public ProfesorDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // Obtener especialidades que enseña ese profesor
    public List<Especialidad> obtenerEspecialidad(String ci) {
        List<Especialidad> especialidades = new ArrayList<>();
        String sql = "SELECT id_especialidad, especialidad " +
                     "FROM vista_profesor_especialidad " +
                     "WHERE profesor_id = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);  // CI del profesor

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_especialidad");
                    String nombre = rs.getString("especialidad");
                    especialidades.add(new Especialidad(id, nombre));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return especialidades;
    }

    // Obtener los cursos que enseña el profesor en una especialidad
    public List<Curso> obtenerCursos(String ci, int id_especialidad) {
        List<Curso> cursos = new ArrayList<>();
        String sql = "SELECT id_curso, curso " +
                     "FROM vista_profesor_curso " +
                     "WHERE profesor_id = ? AND id_especialidad = ?"; 

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setInt(2, id_especialidad);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_curso");  // id_curso ya actualizado en la vista
                    String nombre = rs.getString("curso");
                    cursos.add(new Curso(id, nombre));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cursos;
    }

    // Obtener los alumnos de un curso
    public List<Alumno> obtenerAlumnos(int idCurso) {
        List<Alumno> alumnos = new ArrayList<>();
        String sql = "SELECT estudiante_id, alumno " +
                     "FROM vista_curso_alumnos " +
                     "WHERE id_curso = ?";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idCurso);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int ci = rs.getInt("estudiante_id");
                    String nombre = rs.getString("alumno");
                    alumnos.add(new Alumno(ci, nombre));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return alumnos;
    }
    
    
    //insertar casos a la base de datos 
    public boolean insertarCaso(String observaciones, int ciProfesor, int ciEstudiante, File archivoSeleccionado) {
        String sql = "INSERT INTO caso (fecha, obs_generales, activo, profesor_CI, estudiante_CI, archivo, extension) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // Fecha actual
            stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));

            // Observaciones
            stmt.setString(2, observaciones);

            // Activo por defecto
            stmt.setBoolean(3, true);

            // Claves foráneas
            stmt.setInt(4, ciProfesor);
            stmt.setInt(5, ciEstudiante);

            // Archivo opcional
            if (archivoSeleccionado != null && archivoSeleccionado.exists()) {
                FileInputStream fis = new FileInputStream(archivoSeleccionado);
                stmt.setBinaryStream(6, fis, (int) archivoSeleccionado.length());

                // Guardar extensión, incluyendo el punto (ej: ".pdf")
                String nombre = archivoSeleccionado.getName();
                String extension = "";
                int i = nombre.lastIndexOf('.');
                if (i > 0) {
                    extension = nombre.substring(i); // ".pdf", ".jpg", etc.
                }
                stmt.setString(7, extension);

            } else {
                stmt.setNull(6, Types.BLOB);
                stmt.setNull(7, Types.VARCHAR);
            }

            stmt.executeUpdate();
            return true;

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<CasoResumen> obtenerCasosPorProfesor(String profesorCI) {
        List<CasoResumen> listaCasos = new ArrayList<>();
        String sql = "SELECT id_caso, fecha, estudiante, especialidad, curso " +
                     "FROM seguimiento.profesor_caso_resumen " +
                     "WHERE profesor_CI = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, profesorCI);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CasoResumen caso = new CasoResumen();
                    caso.setId_caso(rs.getInt("id_caso"));

                    // Convertimos de java.sql.Timestamp a LocalDateTime
                    Timestamp ts = rs.getTimestamp("fecha");
                    if (ts != null) {
                        caso.setFecha(ts.toLocalDateTime());
                    }

                    caso.setEstudiante(rs.getString("estudiante"));
                    caso.setEspecialidad(rs.getString("especialidad"));
                    caso.setCurso(rs.getString("curso"));

                    listaCasos.add(caso);
                }
            }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return listaCasos;
         }

    public Especialidad esPioCoordi(int ci) throws SQLException {
        String sql = "SELECT id_especialidad, nombre_especialidad FROM v_coordinadores WHERE ci_coordinador = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Especialidad especial = new Especialidad();
                    especial.setId(rs.getInt("id_especialidad"));
                    especial.setNombre(rs.getString("nombre_especialidad"));
                    
                    return especial;
                }
            }
        }
        return null; // si no se encuentra el CI
    }

     public List<CasoResumen> obtenerCasosPorEspe(String Espe) {
        List<CasoResumen> listaCasos = new ArrayList<>();
        String sql = "SELECT id_caso, fecha, estudiante, especialidad, curso " +
                     "FROM seguimiento.profesor_caso_resumen " +
                     "WHERE especialidad = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, Espe);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CasoResumen caso = new CasoResumen();
                    caso.setId_caso(rs.getInt("id_caso"));

                    // Convertimos de java.sql.Timestamp a LocalDateTime
                    Timestamp ts = rs.getTimestamp("fecha");
                    if (ts != null) {
                        caso.setFecha(ts.toLocalDateTime());
                    }

                    caso.setEstudiante(rs.getString("estudiante"));
                    caso.setEspecialidad(rs.getString("especialidad"));
                    caso.setCurso(rs.getString("curso"));

                    listaCasos.add(caso);
                }
            }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return listaCasos;
         }
     

}

