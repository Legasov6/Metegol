package Logica;

import java.io.Serializable;

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

    // Acciones de Habilidad (Patear / Muro)
    public boolean clickIzquierdo;
    public double mouseX; // Vital para saber hacia dónde apuntó el cliente al disparar
    public double mouseY;

    public ComandoRed() {
        // Todo arranca en false por defecto en Java
    }
}