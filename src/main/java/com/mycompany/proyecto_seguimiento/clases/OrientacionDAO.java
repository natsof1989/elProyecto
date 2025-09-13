/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 *
 * @author natha
 */
public class OrientacionDAO {
    private final Connection conexion;
    
    

    public OrientacionDAO(Connection conexion) {
        this.conexion = conexion;
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
    
   public int insertarOrientacion(String descripcion, int idCaso) throws SQLException {
        String sql = "INSERT INTO orientacion (observaciones, fecha, id_caso) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp fecha = new Timestamp(System.currentTimeMillis());
            ps.setString(1, descripcion);
            ps.setTimestamp(2, fecha);
            ps.setInt(3, idCaso);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // devuelve el cod_orientacion generado
                    }
                }
            }
        }
        return -1; 
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


}

    
    
    
    

    
    
    

