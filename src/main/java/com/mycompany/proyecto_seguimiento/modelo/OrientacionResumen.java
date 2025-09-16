/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.modelo;

import java.sql.Timestamp;

/**
 *
 * @author natha
 */
public class OrientacionResumen {
    private int cod_orientacion; 
    private String Autor; 
    private Timestamp fecha; 
    private String estudiante; 
    private String especialidad; 
    private String curso; 
    private int id_caso; 
    

    public OrientacionResumen() {
    }
    
    

    public OrientacionResumen(int cod_orientacion, String autor, Timestamp fecha, String estudiante, String especialidad, String curso, int id_caso) {
        this.cod_orientacion = cod_orientacion;
        this.Autor = autor; 
        this.fecha = fecha;
        this.estudiante = estudiante;
        this.especialidad = especialidad;
        this.curso = curso;
        this.id_caso = id_caso;
    }

    public int getCod_orientacion() {
        return cod_orientacion;
    }

    public void setCod_orientacion(int cod_orientacion) {
        this.cod_orientacion = cod_orientacion;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
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

    public int getId_caso() {
        return id_caso;
    }

    public void setId_caso(int id_caso) {
        this.id_caso = id_caso;
    }

    public String getAutor() {
        return Autor;
    }

    public void setAutor(String Autor) {
        this.Autor = Autor;
    }
    
    
    
    
}
