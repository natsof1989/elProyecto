/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author natha
 */


public class CasoSeleccionado {
    private static CasoSeleccionado instancia;

    // Datos básicos ya mostrados en la tabla
    private int idCaso;
    private LocalDateTime fecha;
    private String estudiante;     // nombre completo
    private String especialidad;
    private String curso;

    // Datos del profesor y detalle del caso
    private String ciProfesor;
    private String nombreProfesor;
    private String descripcion;
    private byte[] archivo;        // puede ser null si no hay archivo

    
    
    private String fxmlAnterior;
    private String extension; 
    private List<Integer> asignados;
    
    private boolean activo; 

    public List<Integer> getAsignados() {
        return asignados;
    }

    public void setAsignados(List<Integer> asignados) {
        this.asignados = asignados;
    }

    

    
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
    
    
    private CasoSeleccionado() {}

    public String getFxmlAnterior() {
        return fxmlAnterior;
    }

    public void setFxmlAnterior(String fxmlAnterior) {
        this.fxmlAnterior = fxmlAnterior;
    }

    public static CasoSeleccionado getInstancia() {
        if (instancia == null) {
            instancia = new CasoSeleccionado();
        }
        return instancia;
    }

    // -------- Getters y Setters --------
    public int getIdCaso() {
        return idCaso;
    }

    public void setIdCaso(int idCaso) {
        this.idCaso = idCaso;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getCiProfesor() {
        return ciProfesor;
    }

    public void setCiProfesor(String ciProfesor) {
        this.ciProfesor = ciProfesor;
    }

    public String getNombreProfesor() {
        return nombreProfesor;
    }

    public void setNombreProfesor(String nombreProfesor) {
        this.nombreProfesor = nombreProfesor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.archivo = archivo;
    }
    
    public void resetAsignados() {
        if (asignados != null) {
            asignados.clear(); // limpia la lista
        }
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    // Método de conveniencia para resetear la instancia si es necesario
    public static void reset() {
        instancia = null;
    }
}
