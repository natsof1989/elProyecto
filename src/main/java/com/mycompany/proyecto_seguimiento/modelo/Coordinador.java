/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.modelo;

/**
 *
 * @author natha
 */
public class Coordinador {
    private int ci; 
    private String nombreCompleto; 
    private int id_espe; 
    private String espe; 

    public Coordinador(int ci, String nombreCompleto, int id_espe, String espe) {
        this.ci = ci;
        this.nombreCompleto = nombreCompleto;
        this.id_espe = id_espe;
        this.espe = espe;
    }

    public Coordinador() {
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public int getId_espe() {
        return id_espe;
    }

    public void setId_espe(int id_espe) {
        this.id_espe = id_espe;
    }

    public String getEspe() {
        return espe;
    }

    public void setEspe(String espe) {
        this.espe = espe;
    }
    
    
    
}
