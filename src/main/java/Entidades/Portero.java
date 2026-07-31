// @author Frank Farias

package Entidades;

public class Portero extends Futbolista{
    private int nivelAtajada;

    public Portero(String nombre, String pais, int velocidad, int disparo, int pase, int defensa, int nivelAtajada, int precio) {
        super(nombre, pais, velocidad, disparo, pase, defensa, precio);
        this.nivelAtajada = nivelAtajada;
    }

    public int getNivelAtajada() {
        return nivelAtajada;
    }
    }

 
