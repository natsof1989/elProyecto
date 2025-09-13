/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author natha
 */
public class Orientacion {
    private static Orientacion instancia;
    
    private String orientacion; 
    private String avance; 
    private String fxmlAnterior; 
    private Timestamp fecha; 
    
    
    
    
    
    private Orientacion(){}; 
    
     public static Orientacion getInstancia() {
        if (instancia == null) {
            instancia = new Orientacion();
        }
        return instancia;
    }

    public String getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(String orientacion) {
        this.orientacion = orientacion;
    }

    public String getAvance() {
        return avance;
    }

    public void setAvance(String avance) {
        this.avance = avance;
    }

    public String getFxmlAnterior() {
        return fxmlAnterior;
    }

    public void setFxmlAnterior(String fxmlAnterior) {
        this.fxmlAnterior = fxmlAnterior;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    
    
    
     
     
}
