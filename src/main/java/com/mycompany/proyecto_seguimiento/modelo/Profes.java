/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.modelo;

/**
 *
 * @author natha
 */
public class Profes {
    private int ci; 
    private String email; 
    private int id_espe; 
    private String nombreEspe; 

    public Profes(int ci, String email, int id_espe) {
        this.ci = ci;
        this.email = email;
        this.id_espe = id_espe;
    }

    public Profes(int ci, String email, int id_espe, String nombreEspe) {
        this.ci = ci;
        this.email = email;
        this.id_espe = id_espe;
        this.nombreEspe = nombreEspe;
    }

    public Profes() {
    }

    public int getCi() {
        return ci;
    }

    public void setCi(int ci) {
        this.ci = ci;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId_espe() {
        return id_espe;
    }

    public void setId_espe(int id_espe) {
        this.id_espe = id_espe;
    }

    public String getNombreEspe() {
        return nombreEspe;
    }

    public void setNombreEspe(String nombreEspe) {
        this.nombreEspe = nombreEspe;
    }
    
    
    
    
}
