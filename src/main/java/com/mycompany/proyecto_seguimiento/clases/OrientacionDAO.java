/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import com.mycompany.proyecto_seguimiento.modelo.Profes;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author natha
 */
public class OrientacionDAO {
    private final Connection conexion;
    
    

    public OrientacionDAO(Connection conexion) {
        this.conexion = conexion;
    }
    
    
    public List<Integer> getCodOrientacionesPorAlumno(int idCaso) throws SQLException {
        String sql = "SELECT o2.cod_orientacion FROM orientacion o2 JOIN caso c2 ON o2.id_caso = c2.id_caso WHERE c2.estudiante_CI = (SELECT c1.estudiante_CI FROM caso c1 WHERE c1.id_caso = ?)";

        List<Integer> codigos = new ArrayList<>();

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCaso);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    codigos.add(rs.getInt("cod_orientacion"));
                }
            }
        }

        return codigos;
    }


   public boolean existeUnaOrientacion(int ciEquipo) throws SQLException {
        String sql = "SELECT cod_orientacion FROM detalle_orientacion WHERE equipo_tecnico_CI = ? LIMIT 1"; 
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, ciEquipo); 
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        }
   }
    
    public boolean finalizarCaso(int idCaso) throws SQLException{
        String sql = "UPDATE caso SET activo=0 where id_caso=?"; 
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idCaso);
            int filasAfectadas = ps.executeUpdate(); 
                return filasAfectadas > 0; 
        }
    }
    public boolean reactivarCaso(int idCaso) throws SQLException{
        String sql = "UPDATE caso SET activo=1 where id_caso=?"; 
        try(PreparedStatement ps = conexion.prepareStatement(sql)){
            ps.setInt(1, idCaso);
            int filasAfectadas = ps.executeUpdate(); 
                return filasAfectadas > 0; 
        }
    }
    
   public int insertarOrientacion(String descripcion, int idCaso, int ciEquipo) throws SQLException {
        int codOrientacion = -1;
        boolean exito = false;

        String sql = "{CALL insertar_orientacion_con_detalle(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cs = conexion.prepareCall(sql)) {
            // Parámetros de entrada
            cs.setString(1, descripcion);
            cs.setInt(2, idCaso);
            cs.setInt(3, ciEquipo);
            cs.setTimestamp(4, new Timestamp(System.currentTimeMillis())); // fecha actual

            // Parámetros de salida
            cs.registerOutParameter(5, Types.INTEGER); // p_cod_orientacion
            cs.registerOutParameter(6, Types.BOOLEAN); // p_exito

            // Ejecutar procedimiento
            cs.execute();

            exito = cs.getBoolean(6);
            if (exito) {
                codOrientacion = cs.getInt(5);
            }
        }

        return codOrientacion;
    }
    
   public Timestamp getFechaOrientacion(int codOrientacion) throws SQLException {
        String sql = "SELECT fecha FROM orientacion WHERE cod_orientacion = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codOrientacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("fecha");
                }
            }
        }
        return null; // si no encontró nada
    }
   
   public String getDescripcionDepartamentoPorCI(int ci) throws SQLException {
        String descripcion = null;
        String sql = "SELECT d.descripcion " +
                     "FROM equipo_tecnico et " +
                     "JOIN departamento d ON et.id_departamento = d.id_departamento " +
                     "WHERE et.CI = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, ci);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    descripcion = rs.getString("descripcion");
                }
            }
        }
        return descripcion;
    }

  public List<Profes> getProfesByEspecialidad(int idCurso, int id_estudiante) throws SQLException {
        List<Profes> listaProfes = new ArrayList<>();

        // Año actual
        int anhio = java.time.LocalDate.now().getYear();

        String sql = "SELECT id_profe, email " +
                     "FROM vw_curso_profesor " +
                     "WHERE id_curso = ? AND anhio_lectivo = ? AND id_estudiante = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCurso);
            ps.setInt(2, anhio);
            ps.setInt(3, id_estudiante);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Profes profe = new Profes();
                    profe.setCi(rs.getInt("id_profe"));
                    profe.setEmail(rs.getString("email"));

                    listaProfes.add(profe);
                }
            }
        }

        return listaProfes;
    }


}

    
    
    
    

    
    
    

