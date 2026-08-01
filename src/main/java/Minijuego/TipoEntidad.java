package Minijuego;

/**
 * Archivo genérico tipo enum requerido por el marco de trabajo FXGL para 
 * identificar estrictamente los tipos de colisionadores físicos (CollidableComponent) 
 * de Box2D.
 * @author GabrielTremaria
 */
public enum TipoEntidad {
    JUGADOR_ATACANTE, JUGADOR_DEFENSOR, BALON, LIMITE_CANCHA, PORTERIA, PALO
}