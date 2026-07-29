package Minijuego;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;

public class IAFutbolistaComponent extends Component {

    private boolean esAtacante;
    private boolean atacaHaciaArriba; // NUEVA VARIABLE

    public IAFutbolistaComponent(boolean esAtacante, boolean atacaHaciaArriba) {
        this.esAtacante = esAtacante;
        this.atacaHaciaArriba = atacaHaciaArriba;
    }

    @Override
    public void onUpdate(double tpf) {
        // ESCUDO MULTIJUGADOR: Si somos el Cliente, apagamos el cerebro local por completo.
        Logica.GestorJuego gestor = Logica.GestorJuego.getInstance();
        boolean esCliente = !gestor.isEsHost() && gestor.getCliente() != null;
        if (esCliente) {
            return;
        }

        // ESCUDO IA: No controlar a los jugadores manejados por humanos (Ni al Host ni al Cliente)
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
            
            // 1. Obtener TODAS las porterías de la cancha
            java.util.List<Entity> porterias = FXGL.getGameWorld().getEntitiesByType(TipoEntidad.PORTERIA);
            Entity porteriaObjetivo = null;

            if (!porterias.isEmpty()) {
                porteriaObjetivo = porterias.get(0);
                // 2. Filtrar cuál es la correcta según hacia dónde atacamos
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

            // 3. Trazar la diagonal hacia la meta
            if (miPosicion.distance(metaOfensiva) > 100) {
                Point2D direccion = metaOfensiva.subtract(miPosicion).normalize();
                fisicas.setLinearVelocity(direccion.multiply(velocidadIA));
            } else {
                fisicas.setLinearVelocity(0, 0);
            }
        }
    }
}