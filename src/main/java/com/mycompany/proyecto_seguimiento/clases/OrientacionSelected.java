/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto_seguimiento.clases;

import com.mycompany.proyecto_seguimiento.modelo.OrientacionResumen;
import com.mycompany.proyecto_seguimiento.modelo.equipoTecnico;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author natha
 */
public class OrientacionSelected {
    private static OrientacionSelected instancia;
    private List<OrientacionResumen> orientaciones  = new ArrayList<>(); 
    
    
    private OrientacionSelected(){}; 
    
    public static OrientacionSelected getInstancia() {
        if (instancia == null) {
            instancia = new OrientacionSelected();
        }
        return instancia;
    }

    public List<OrientacionResumen> getOrientaciones() {
        return orientaciones;
    }

    public void setOrientaciones(List<OrientacionResumen> orientaciones) {
        this.orientaciones = orientaciones;
    }

   
}
