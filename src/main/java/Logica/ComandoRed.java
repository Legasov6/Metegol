package Logica;

import java.io.Serializable;

/**
 * Servir como un DTO (Data Transfer Object) que encapsula las
 * pulsaciones del teclado y el uso del ratón del usuario,
 * enviándolas en un paquete liviano al contrincante a través de
 * la red. Implementa Serializable.
 */

public class ComandoRed implements Serializable {
    private static final long serialVersionUID = 1L;

    // Movimiento (Teclas WASD)
    public boolean arriba;
    public boolean abajo;
    public boolean izquierda;
    public boolean derecha;

    // Acciones de Cambio
    public boolean cambiarDefensor; // Tecla Q
    public boolean controlarPortero; // Barra Espaciadora

    // Acciones de Habilidad (Patear y el muro)
    public boolean clickIzquierdo;
    public double mouseX; // Para saber hacia dónde apuntó el cliente al disparar
    public double mouseY;

    public ComandoRed() {
        // Todo arranca en false por defecto en Java
    }
}