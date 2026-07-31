// @author Gabriel Tremaria

package Minijuego;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

public class IAFutbolistaComponent extends Component {

    private boolean esAtacante;
    private boolean atacaHaciaArriba; //Para determinar hacia dónde correrán los atacantes

    public IAFutbolistaComponent(boolean esAtacante, boolean atacaHaciaArriba) {
        this.esAtacante = esAtacante;
        this.atacaHaciaArriba = atacaHaciaArriba;
    }

    @Override
    public void onUpdate(double tpf) {
        // Si somos el Cliente, apagamos el gestor local para que el host sea quien lo maneje
        Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
        boolean esCliente = !gestor.isEsHost() && gestor.getCliente() != null;
        if (esCliente) {
            return;
        }

        // Para que la IA no controle a los jugadores manejados por humanos
        if (entity == EscenaMinijuego.getJugadorActivo() || entity == EscenaMinijuego.getJugadorEnemigoActivo()) {
            return;
        }

        PhysicsComponent fisicas = entity.getComponent(PhysicsComponent.class);

        if (!esAtacante) {
            fisicas.setLinearVelocity(0, 0);
            return;
        } else {
            double velocidadIA = entity.getComponent(AtributosFutbolistaComponent.class).getVelocidadFXGL() * 0.6;
            Point2D miPosicion = entity.getCenter();
            
            // Obtener las porterías de la cancha
            java.util.List<Entity> porterias = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.PORTERIA);
            Entity porteriaObjetivo = null;

            if (!porterias.isEmpty()) {
                porteriaObjetivo = porterias.get(0);
                // Filtrar cuál es la correcta según hacia dónde atacamos
                for (Entity p : porterias) {
                    if (atacaHaciaArriba && p.getY() < porteriaObjetivo.getY()) {
                        porteriaObjetivo = p; 
                    } else if (!atacaHaciaArriba && p.getY() > porteriaObjetivo.getY()) {
                        porteriaObjetivo = p; 
                    }
                }
            }
            
            Point2D metaOfensiva;
            if (porteriaObjetivo != null) {
                metaOfensiva = porteriaObjetivo.getCenter();
            } else {
                double yObjetivo = atacaHaciaArriba ? 50 : FXGL.getAppHeight() - 50;
                metaOfensiva = new Point2D(FXGL.getAppWidth() / 2.0, yObjetivo);
            }

            // Trazar la diagonal hacia la meta
            if (miPosicion.distance(metaOfensiva) > 100) {
                Point2D direccion = metaOfensiva.subtract(miPosicion).normalize();
                fisicas.setLinearVelocity(direccion.multiply(velocidadIA));
            } else {
                fisicas.setLinearVelocity(0, 0);
            }
        }
    }
}