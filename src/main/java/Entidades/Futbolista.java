package Entidades;
import java.io.Serializable;

/**
 * Esta clase representa a un futbolista genérico en el mercado. Implementa la
 * interfaz Serializable, necesaria para transmitir sus datos en paquetes a través
 * de Java Sockets.
 * @author FrankFarias
 */
public class Futbolista implements Serializable{
    
    private static final long serialVersionUID = 1L; //Identificador único para el envio de paquetes para el online
    private String nombre;
    private String pais;
    private int velocidad;
    private int disparo;
    private int pase;
    private int defensa;
    private int precio;
    private int estadoDeForma;

    public Futbolista(String nombre, String pais,int velocidad, int disparo, int pase, int defensa, int precio) {
        this.nombre = nombre;
        this.pais = pais;
        this.velocidad = velocidad;
        this.disparo = disparo;
        this.pase = pase;
        this.defensa = defensa;
        this.precio = precio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPais() {
        return pais;
    }

    public int getVelocidad() {
        return velocidad;
    }
    

    public int getTiro() {
        return disparo;
    }

    public int getPase() {
        return pase;
    }

    public int getDefensa() {
        return defensa;
    }

    public int getPrecio() {
        return precio;
    }

    public int getEstadoDeForma() {
        return estadoDeForma;
    }

    
    
    
}
