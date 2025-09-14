/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 *
 * @author natha
 */
public class CasoResumen {
    private int id_caso; 
    private LocalDateTime fecha;
    private String estudiante;
    private String especialidad;
    private String curso;
    private boolean activo; 
    private ArrayList<String> departamentos = new ArrayList<>(); 


    public CasoResumen() {
    }

    public CasoResumen(int id_caso, LocalDateTime fecha, String estudiante, String especialidad, String curso, boolean activo) {
        this.id_caso = id_caso;
        this.fecha = fecha;
        this.estudiante = estudiante;
        this.especialidad = especialidad;
        this.curso = curso;
        this.activo = activo;
    }
    
    

    public CasoResumen(int id_caso, LocalDateTime fecha, String estudiante, String especialidad, String curso) {
        this.id_caso = id_caso;
        this.fecha = fecha;
        this.estudiante = estudiante;
        this.especialidad = especialidad;
        this.curso = curso;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    

    public int getId_caso() {
        return id_caso;
    }

    public void setId_caso(int id_caso) {
        this.id_caso = id_caso;
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

    public ArrayList<String> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(ArrayList<String> departamentos) {
        this.departamentos = departamentos;
    }

   

    public void setCurso(String curso) {
        this.curso = curso;
    }
    
    

}
