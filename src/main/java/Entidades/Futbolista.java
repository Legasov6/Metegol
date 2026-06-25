/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author legasov
 */
public class Futbolista {
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

    public int getPrecio() {
        return precio;
    }
    
    public int getVelocidad(){
        return velocidad;
    }
    
    
}
