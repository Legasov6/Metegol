package Logica;

import java.io.Serializable;

public class EstadoMinijuego implements Serializable {
    // serialVersionUID asegura que Host y Cliente hablen exactamente la misma versión del objeto
    private static final long serialVersionUID = 1L;

    // Coordenadas del balón
    public double balonX;
    public double balonY;

    // Coordenadas de todos los jugadores en la cancha
    // Usamos arreglos primitivos de double porque son lo más rápido de serializar en Java
    public double[] jugadoresX;
    public double[] jugadoresY;

    // Banderas de control de tiempo
    public boolean minijuegoTerminado;
    public String mensajeFinal;

    public EstadoMinijuego(int cantidadJugadores) {
        jugadoresX = new double[cantidadJugadores];
        jugadoresY = new double[cantidadJugadores];
        minijuegoTerminado = false;
        mensajeFinal = "";
    }
}