/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

/**
 *
 * @author legasov
 */
public class Portero extends Futbolista{
    private int nivelAtajada;

    public Portero(String nombre, String pais, int velocidad, int disparo, int pase, int defensa, int nivelAtajada, int precio) {
        super(nombre, pais, velocidad, disparo, pase, defensa, precio);
        this.nivelAtajada = nivelAtajada;
    }

  
    }

 
