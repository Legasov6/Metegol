package Minijuego;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MagnetismoBalonComponent extends Component {
    
    private Entity balonFisico;
    private boolean tieneElBalon = false;
    private Circle balonFalso;
    private Point2D direccionActual = new Point2D(1, 0); 

    public MagnetismoBalonComponent(Entity balon) {
        this.balonFisico = balon;
        this.balonFalso = new Circle(10, Color.WHITE);
        this.balonFalso.setStroke(Color.BLACK);
    }

    public boolean isTieneElBalon() {
        return tieneElBalon;
    }

    public void setTieneElBalon(boolean nuevoEstado) {
        if (this.tieneElBalon == nuevoEstado) return;
        this.tieneElBalon = nuevoEstado;

        PhysicsComponent fisicasBalon = balonFisico.getComponent(PhysicsComponent.class);

        if (tieneElBalon) {
            balonFisico.getViewComponent().setVisible(false);
            
            // FRENAMOS TODO: Movimiento recto y rotación (giro)
            fisicasBalon.setLinearVelocity(0, 0);
            fisicasBalon.setAngularVelocity(0); 
            
            fisicasBalon.overwritePosition(new Point2D(-1000, -1000));
            entity.getViewComponent().addChild(balonFalso);
        } else {
            entity.getViewComponent().removeChild(balonFalso);
            double xReal = entity.getX() + balonFalso.getTranslateX();
            double yReal = entity.getY() + balonFalso.getTranslateY();
            
            fisicasBalon.overwritePosition(new Point2D(xReal, yReal));
            
            // Aseguramos que vuelva a la cancha sin girar a lo loco
            fisicasBalon.setAngularVelocity(0); 
            balonFisico.getViewComponent().setVisible(true);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (tieneElBalon) {
            PhysicsComponent misFisicas = entity.getComponent(PhysicsComponent.class);
            double velX = misFisicas.getVelocityX();
            double velY = misFisicas.getVelocityY();
            
            if (Math.abs(velX) > 1 || Math.abs(velY) > 1) {
                direccionActual = new Point2D(velX, velY).normalize();
            }

            double centroX = entity.getWidth() / 2.0;
            double centroY = entity.getHeight() / 2.0;
            
            // CORRECCIÓN FÍSICA: Radio Jugador (18) + Radio Balón (10) = 28px. 
            // Usamos 32px para dejar un margen seguro y que no nazcan solapados.
            double distanciaSegura = 32.0; 
            
            balonFalso.setTranslateX(centroX - 10 + (direccionActual.getX() * distanciaSegura));
            balonFalso.setTranslateY(centroY - 10 + (direccionActual.getY() * distanciaSegura));
        }
    }

    // ==========================================
    // LIMPIEZA AUTOMÁTICA
    // ==========================================
    @Override
    public void onRemoved() {
        // Cuando FXGL arranca este componente del pasador (Messi), 
        // nos aseguramos de borrar su balón falso para que no quede como fantasma.
        if (tieneElBalon && entity != null) {
            entity.getViewComponent().removeChild(balonFalso);
        }
    }
}