/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author legasov
 */
public class Equipo {
    private String nombrePais;
    private ArrayList<Futbolista> titulares;

    public Equipo(String nombrePais) {
        this.nombrePais = nombrePais;
        this.titulares = new ArrayList<>();
    }

    public String getNombrePais() {
        return nombrePais;
    }
    
    public void agregarFutbolista(Futbolista f){
        titulares.add(f);
        
    }
    
    public void removerFutbolista(){
        
    }
    
    public void calcularPromedioEquipo(){
        
    }
    
    public List<Futbolista> getTitulares(){
        return titulares;
    }
    
}
