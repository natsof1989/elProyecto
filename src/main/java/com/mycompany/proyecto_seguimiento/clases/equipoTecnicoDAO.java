/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import com.mycompany.proyecto_seguimiento.modelo.CasoResumen;
import com.mycompany.proyecto_seguimiento.modelo.equipoTecnico;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author natha
 */
public class equipoTecnicoDAO {
    
    private final Connection conexion;

    public equipoTecnicoDAO(Connection conexion) {
        this.conexion = conexion;
    }
    
    public List<CasoResumen> obtenerCasosSinEquipo() {
        List<CasoResumen> casos = new ArrayList<>();
        String sql = "SELECT id_caso, fecha, estudiante_nombre, especialidad, curso, activo "
                   + "FROM casos_sin_equipo";

        try (PreparedStatement ps = conexion.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CasoResumen cr = new CasoResumen();
                cr.setId_caso(rs.getInt("id_caso"));
                // Convertir de Timestamp a LocalDateTime
                Timestamp ts = rs.getTimestamp("fecha");
                if (ts != null) {
                    cr.setFecha(ts.toLocalDateTime());
                }
                cr.setEstudiante(rs.getString("estudiante_nombre"));
                cr.setEspecialidad(rs.getString("especialidad"));
                cr.setCurso(rs.getString("curso"));
                cr.setActivo(rs.getBoolean("activo"));

                casos.add(cr);
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }

        return casos;
    }
    public List<equipoTecnico> obtenerEquipos(){
        List <equipoTecnico> equipos = new ArrayList<>(); 
         String sql = "SELECT ci, nombre_completo, departamento FROM vista_equipo_tecnico_especialidad1;";
         
        try (PreparedStatement ps = conexion.prepareStatement(sql); 
            ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                equipoTecnico et = new equipoTecnico(); 
                et.setCi(String.valueOf(rs.getInt("ci")));
                et.setNombreCompleto(rs.getString("nombre_completo"));
                et.setDepartamento(rs.getString("departamento"));
                equipos.add(et); 
                
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return equipos; 
        
    }
    public List<Integer> obtenerAsignados(int idCaso){
        List<Integer> asignados = new ArrayList<>(); 
            String sql = "SELECT equipo_tecnico_CI FROM det_caso WHERE id_caso = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {   
            ps.setInt(1, idCaso);                                       
            try (ResultSet rs = ps.executeQuery()) {                    
                while (rs.next()) {
                    asignados.add(rs.getInt("equipo_tecnico_CI"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return asignados; 
    }
    
        
    
    public boolean perteneceDepartamento1(int ci) throws SQLException {
        String sql = "SELECT id_departamento FROM equipo_tecnico WHERE CI = ?";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, ci);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_departamento") == 1;
                }
            }
        }
        return false;
    }


        public List<CasoResumen> obtenerCasosAsignados(String ciEquipoTec) throws SQLException {
        List<CasoResumen> lista = new ArrayList<>();
        int ciEquipo = Integer.parseInt(ciEquipoTec); 
        String sql = "SELECT id_caso, fecha, estudiante, especialidad, curso, activo " +
                     "FROM v_casos_equipo_tecnico WHERE equipo_tecnico_CI = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, ciEquipo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CasoResumen caso = new CasoResumen();
                    caso.setId_caso(rs.getInt("id_caso"));
                    Timestamp ts = rs.getTimestamp("fecha");
                    if (ts != null) {
                        caso.setFecha(ts.toLocalDateTime());
                    }
                    caso.setEstudiante(rs.getString("estudiante"));
                    caso.setEspecialidad(rs.getString("especialidad"));
                    caso.setCurso(rs.getString("curso"));
                    caso.setActivo(rs.getBoolean("activo"));
                    lista.add(caso);
                }
            }
        }

        return lista;
    }
    public List<CasoResumen> obtenerTodosLosCasosConDepartamentos() throws SQLException {
    List<CasoResumen> lista = new ArrayList<>();

    // Agrupamos por caso para que los departamentos queden en una lista
    String sql = "SELECT id_caso, fecha, estudiante, especialidad, curso, activo, " +
                 "GROUP_CONCAT(DISTINCT departamento SEPARATOR ', ') AS departamentos " +
                 "FROM v_casos_equipo_tecnico " +
                 "GROUP BY id_caso, fecha, estudiante, especialidad, curso, activo";

    try (PreparedStatement ps = conexion.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            CasoResumen caso = new CasoResumen();
            caso.setId_caso(rs.getInt("id_caso"));

            Timestamp ts = rs.getTimestamp("fecha");
            if (ts != null) {
                caso.setFecha(ts.toLocalDateTime());
            }

            caso.setEstudiante(rs.getString("estudiante"));
            caso.setEspecialidad(rs.getString("especialidad"));
            caso.setCurso(rs.getString("curso"));
            caso.setActivo(rs.getBoolean("activo"));

            // Convertimos la cadena de departamentos en lista
            String deps = rs.getString("departamentos");
            List<String> listaDeps = new ArrayList<>();
            if (deps != null && !deps.isEmpty()) {
                listaDeps.addAll(Arrays.asList(deps.split(",\\s*")));
            }
            caso.setDepartamentos((ArrayList<String>) listaDeps);

            lista.add(caso);
        }
    }

    return lista;
}


   


}
