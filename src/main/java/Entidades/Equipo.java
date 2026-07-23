/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;


import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 *
 * @author legasov
 */
public class Equipo implements Serializable {
    private static final long serialVersionUID = 1L; //Identificador único para el envio de paquetes para el online
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
    
    public void removerFutbolista(Futbolista f){ // POR REVISAR
        titulares.remove(f);
    }
    
    // ==========================================
    // MÉTODOS DE EXTRACCIÓN PARA DELANTEROS
    // ==========================================
    public int getPaseDelanteros() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Delantero) {
                total += f.getPase();
            }
        }
        return total;
    }

    public int getVelocidadDelanteros() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Delantero) {
                total += f.getVelocidad();
            }
        }
        return total;
    }

    // ==========================================
    // MÉTODOS DE EXTRACCIÓN PARA MEDIOCAMPISTAS
    // ==========================================
    public int getPaseMediocampistas() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Mediocampista) {
                total += f.getPase();
            }
        }
        return total;
    }

    public int getVelocidadMediocampistas() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Mediocampista) {
                total += f.getVelocidad();
            }
        }
        return total;
    }

    public int getDefensaMediocampistas() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Mediocampista) {
                total += f.getDefensa();
            }
        }
        return total;
    }

    // ==========================================
    // MÉTODOS DE EXTRACCIÓN PARA DEFENSORES
    // ==========================================
    public int getPaseDefensores() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Defensor) {
                total += f.getPase();
            }
        }
        return total;
    }

    public int getVelocidadDefensores() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Defensor) {
                total += f.getVelocidad();
            }
        }
        return total;
    }

    public int getDefensaDefensores() {
        int total = 0;
        for (Futbolista f : titulares) {
            if (f instanceof Defensor) {
                total += f.getDefensa();
            }
        }
        return total;
    }
    
    public List<Futbolista> getTitulares(){
        return titulares;
    }
    
}
