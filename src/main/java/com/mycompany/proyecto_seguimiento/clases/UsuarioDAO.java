/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

/**
 *
 * @author natha y mauri
 */



import com.mycompany.proyecto_seguimiento.modelo.Seguridad;
import com.mycompany.proyecto_seguimiento.modelo.Seguridad;
import com.mycompany.proyecto_seguimiento.modelo.UsuarioDatos;
import com.mycompany.proyecto_seguimiento.modelo.UsuarioDatos;
import java.sql.*;
import java.util.*;

public class UsuarioDAO {
    private final Connection conexion;

    public UsuarioDAO(Connection conexion) {
        this.conexion = conexion;
    }

    /** 1) Verifica si existe CI en profesor o equipo_tecnico */
    public boolean existeCI(String ci) throws SQLException {
        String sql = "SELECT 1 FROM profesor WHERE CI = ? UNION ALL " +
                     "SELECT 1 FROM equipo_tecnico WHERE CI = ? LIMIT 1";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setString(2, ci);
            return stmt.executeQuery().next();
        }
    }
    
   public boolean existeEquipoTecnico(int ci) throws SQLException {
    String sql = "SELECT 1 FROM equipo_tecnico WHERE CI = ? LIMIT 1";
    
    try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
        stmt.setInt(1, ci);
        try (ResultSet rs = stmt.executeQuery()) {
            return rs.next(); // si hay resultado, existe
        }
    }
}


    // Obtiene todos los roles del usuario
    public List<String> obtenerRoles(String ci) throws SQLException {
        List<String> roles = new ArrayList<>();
        
        if (esDeRol(ci, "profesor")) {
            roles.add("PROFESOR");
        }
        
        if (esDeRol(ci, "equipo_tecnico")) {
            roles.add("EQUIPO_TECNICO");
        }
        
        return roles;
    }
    
    // Verifica si existe un registro COMPLETO (con todos los campos obligatorios)
    public boolean existeRegistroCompleto(String ci) throws SQLException {
        String sql = "SELECT 1 FROM profesor WHERE CI = ? AND nombre IS NOT NULL AND apellido IS NOT NULL AND password IS NOT NULL " +
                    "UNION ALL " +
                    "SELECT 1 FROM equipo_tecnico WHERE CI = ? AND nombre IS NOT NULL AND apellido IS NOT NULL AND password IS NOT NULL " +
                    "LIMIT 1";
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setString(2, ci);
            return stmt.executeQuery().next();
        }
    }

    // Valida credenciales usando tu clase Seguridad
    public boolean validarCredenciales(String ci, String contrasenia) throws SQLException {
        String sql = "SELECT password FROM profesor WHERE CI = ? UNION ALL " +
                     "SELECT password FROM equipo_tecnico WHERE CI = ?";
        
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setString(2, ci);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String hashGuardado = rs.getString("password");
                return Seguridad.verificarPassword(contrasenia, hashGuardado);
            }
            return false;
        }
    }

    // Mira quye lindo
    public boolean actualizarPassword(String ci, String nuevaContrasenia) throws SQLException {
        String hashNuevo = Seguridad.encriptarPassword(nuevaContrasenia);

        String sqlProfesor = "UPDATE profesor SET password = ? WHERE CI = ?";
        String sqlEquipo = "UPDATE equipo_tecnico SET password = ? WHERE CI = ?";

        boolean actualizado = false;
        boolean originalAutoCommit = true;

        try {
            // Guardar estado autoCommit y desactivarlo para la transacción
            originalAutoCommit = conexion.getAutoCommit();
            conexion.setAutoCommit(false);

            try (PreparedStatement psProf = conexion.prepareStatement(sqlProfesor);
                 PreparedStatement psEq = conexion.prepareStatement(sqlEquipo)) {

                psProf.setString(1, hashNuevo);
                psProf.setString(2, ci);
                int filasProf = psProf.executeUpdate();

                psEq.setString(1, hashNuevo);
                psEq.setString(2, ci);
                int filasEq = psEq.executeUpdate();

                if (filasProf + filasEq > 0) {
                    conexion.commit();
                    actualizado = true;
                } else {
                    // no existía el CI en ninguna tabla: revertir
                    conexion.rollback();
                    actualizado = false;
                }
            } catch (SQLException e) {
                // en caso de error, revertimos
                try { conexion.rollback(); } catch (SQLException ex) { /* log si querés */ }
                throw e;
            }
        } finally {
            // restaurar autoCommit al estado original
            try { conexion.setAutoCommit(originalAutoCommit); } catch (SQLException ex) { /* log */ }
        }

        return actualizado;
    }
    // Método para actualizar contraseña
    
    // Método auxiliar para verificar rol específico
    private boolean esDeRol(String ci, String tabla) throws SQLException {
        String sql = String.format("SELECT 1 FROM %s WHERE CI = ? LIMIT 1", tabla);
        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            return stmt.executeQuery().next();
        }
    }
    
    public boolean existeCiCorreo(String ci, String correo) throws SQLException {
    String sql = "SELECT 1 FROM profesor WHERE CI = ? AND email = ? " +
                 "UNION ALL " +
                 "SELECT 1 FROM equipo_tecnico WHERE CI = ? AND email = ? LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setString(2, correo);
            stmt.setString(3, ci);
            stmt.setString(4, correo);

            // executeQuery().next() devolverá true si al menos un registro existe, 
            // ya sea en profesor, equipo_tecnico, o en ambos sin problemas.
            return stmt.executeQuery().next();
        }
    }
    public boolean insertarUsuarioCompletoTransaccional(String ci, String nombre, String apellido, String correo, String password) 
                                                   throws SQLException {
        try {
            conexion.setAutoCommit(false); // Iniciamos transacción

            boolean existeEnProfesor = esDeRol(ci, "profesor");
            boolean existeEnEquipoTecnico = esDeRol(ci, "equipo_tecnico");

            if (!existeEnProfesor && !existeEnEquipoTecnico) {
                return false;
            }

            boolean exitoProfesor = false;
            boolean exitoEquipoTecnico = false;

            if (existeEnProfesor) {
                String sqlProfesor = "UPDATE profesor SET nombre = ?, apellido = ?, email = ?, password = ? WHERE CI = ?";
                try (PreparedStatement stmt = conexion.prepareStatement(sqlProfesor)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, apellido);
                    
                    stmt.setString(3, correo);
                    stmt.setString(4, password);
                    stmt.setString(5, ci);
                    exitoProfesor = stmt.executeUpdate() > 0;
                }
            }

            if (existeEnEquipoTecnico) {
                String sqlEquipo = "UPDATE equipo_tecnico SET nombre = ?, apellido = ?, email = ?, password = ? WHERE CI = ?";
                try (PreparedStatement stmt = conexion.prepareStatement(sqlEquipo)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, apellido);
                    
                    stmt.setString(3, correo);
                    stmt.setString(4, password);
                    stmt.setString(5, ci);
                    exitoEquipoTecnico = stmt.executeUpdate() > 0;
                }
            }

            if (exitoProfesor || exitoEquipoTecnico) {
                conexion.commit(); // Confirmamos los cambios
                return true;
            } else {
                conexion.rollback(); // Deshacemos los cambios
                return false;
            }
        } catch (SQLException e) {
            conexion.rollback(); // En caso de error, deshacemos
            throw e;
        } finally {
            conexion.setAutoCommit(true); // Restauramos el modo auto-commit
        }
    }
    


    // Método para actualizar datos básicos (funciona para ambos roles)
    public boolean actualizarDatosBasicos(String ci, String nuevoNombre, String nuevoApellido) throws SQLException {
    boolean actualizado = false;

    String sqlProfesor = "UPDATE profesor SET nombre = ?, apellido = ?, WHERE CI = ?";
    String sqlEquipo = "UPDATE equipo_tecnico SET nombre = ?, apellido = ?, WHERE CI = ?";

    try (
        PreparedStatement stmtProfesor = conexion.prepareStatement(sqlProfesor);
        PreparedStatement stmtEquipo = conexion.prepareStatement(sqlEquipo)
    ) {
        // Parámetros para profesor
        stmtProfesor.setString(1, nuevoNombre);
        stmtProfesor.setString(2, nuevoApellido);
        stmtProfesor.setString(3, ci);
        int filasProfesor = stmtProfesor.executeUpdate();

        // Parámetros para equipo_tecnico
        stmtEquipo.setString(1, nuevoNombre);
        stmtEquipo.setString(2, nuevoApellido);
        
        stmtEquipo.setString(3, ci);
        int filasEquipo = stmtEquipo.executeUpdate();

        // Retorna true si se actualizó al menos una de las dos tablas
        actualizado = filasProfesor > 0 || filasEquipo > 0;
    }

    return actualizado;
}


   

    // Método para eliminar cuenta (establece campos a NULL excepto CI y email)
    public boolean desactivarCuenta(String ci) throws SQLException {
        String sqlProfesor = "UPDATE profesor SET nombre = NULL, apellido = NULL, password = '' WHERE CI = ?";
        String sqlEquipo = "UPDATE equipo_tecnico SET nombre = NULL, apellido = NULL, password = '' WHERE CI = ?";

        int filasProfesor = 0;
        int filasEquipo = 0;

        try (
            PreparedStatement stmtProfesor = conexion.prepareStatement(sqlProfesor);
            PreparedStatement stmtEquipo = conexion.prepareStatement(sqlEquipo)
        ) {
            stmtProfesor.setString(1, ci);
            filasProfesor = stmtProfesor.executeUpdate();

            stmtEquipo.setString(1, ci);
            filasEquipo = stmtEquipo.executeUpdate();
        }

        // Devuelve true si se afectó al menos una tabla
        return filasProfesor > 0 || filasEquipo > 0;
    }


    // Método para verificar en qué tablas existe el usuario
    public List<String> obtenerRolesUsuario(String ci) throws SQLException {
        List<String> roles = new ArrayList<>();
        String sqlProfesor = "SELECT 1 FROM profesor WHERE CI = ?";
        String sqlEquipo = "SELECT 1 FROM equipo_tecnico WHERE CI = ?";
        
        try (PreparedStatement stmtProf = conexion.prepareStatement(sqlProfesor);
             PreparedStatement stmtEquipo = conexion.prepareStatement(sqlEquipo)) {
            
            stmtProf.setString(1, ci);
            stmtEquipo.setString(1, ci);
            
            if (stmtProf.executeQuery().next()) roles.add("PROFESOR");
            if (stmtEquipo.executeQuery().next()) roles.add("EQUIPO_TECNICO");
        }
        return roles;
    }
    
    public UsuarioDatos obtenerDatosUsuario(String ci) throws SQLException {
        String sql = "SELECT nombre, apellido, email FROM profesor WHERE CI = ? " +
                     "UNION ALL " +
                     "SELECT nombre, apellido, email FROM equipo_tecnico WHERE CI = ? " +
                     "LIMIT 1";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, ci);
            stmt.setString(2, ci);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new UsuarioDatos(
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("email")
                );
            }
            return null;
        }
    }
}

