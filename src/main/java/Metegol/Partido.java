package Metegol;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent; 
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton; 
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import com.almasb.fxgl.physics.CollisionHandler;

public class Partido extends GameApplication {

    public enum TipoEntidad {
        JUGADOR, PELOTA, BANDA, FONDO, GOL, RED
    }

    private Entity jugador;
    private Entity pelota;

    
    private int atributoDisparo = 70;
    private boolean tieneElBalon = true; 

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Champs 2026 - Prueba de Disparo");
        settings.setVersion("Sprint Libre");
        settings.setWidth(1280);
        settings.setHeight(768);
        settings.setDeveloperMenuEnabled(true);
        
        settings.setApplicationMode(com.almasb.fxgl.app.ApplicationMode.DEVELOPER);
    }

    @Override
    protected void initInput() {
        double velocidad = 5.0;

        FXGL.onKey(KeyCode.W, () -> jugador.translateY(-velocidad));
        FXGL.onKey(KeyCode.S, () -> jugador.translateY(velocidad));
        FXGL.onKey(KeyCode.A, () -> jugador.translateX(-velocidad));
        FXGL.onKey(KeyCode.D, () -> jugador.translateX(velocidad));

        FXGL.onBtnDown(MouseButton.SECONDARY, () -> {
            if (tieneElBalon) {
                efectuarDisparo();
            }
        });
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntityFactory(new Cancha());
        FXGL.setLevelFromMap("cancha.tmx");
        jugador = FXGL.entityBuilder()
                .type(TipoEntidad.JUGADOR)
                .at(640, 384)
                .viewWithBBox(new Circle(15, Color.BLUE))
                .collidable()
                .buildAndAttach();
    }
    
    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.BANDA) {
            @Override
            protected void onCollisionBegin(Entity jugador, Entity banda) {
                System.out.println("¡FUERA! El jugador pisó la banda lateral.");
            }
        });

        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.FONDO) {
            @Override
            protected void onCollisionBegin(Entity jugador, Entity fondo) {
                System.out.println("¡FONDO! El jugador pisó la línea trasera.");
            }
        });
        
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.RED) {
            @Override
            protected void onCollision(Entity jugador, Entity red) {
                System.out.println("Chocando contra la red...");
            }
        });
    }
    /**
     * Lógica matemática para calcular el vector, la desviación y la velocidad.
     */
    private void efectuarDisparo() {
        Point2D posicionJugador = jugador.getPosition();
        Point2D posicionRaton = FXGL.getInput().getMousePositionWorld(); 
        Point2D direccionBase = posicionRaton.subtract(posicionJugador);

        double factorError = 100 - atributoDisparo; 
        double gradosMaximos = factorError * 0.5; 
        double anguloDesviacion = FXGL.random(-gradosMaximos, gradosMaximos);
        double radianes = Math.toRadians(anguloDesviacion);
        double cos = Math.cos(radianes);
        double sin = Math.sin(radianes);

        double nuevoX = direccionBase.getX() * cos - direccionBase.getY() * sin;
        double nuevoY = direccionBase.getX() * sin + direccionBase.getY() * cos;
        Point2D direccionFinal = new Point2D(nuevoX, nuevoY);
        double velocidadBalon = atributoDisparo * 10.0;

        pelota = FXGL.entityBuilder()
                .type(TipoEntidad.PELOTA)
                .at(posicionJugador) 
                .viewWithBBox(new Circle(8, Color.WHITE))
                .collidable()
                .with(new ProjectileComponent(direccionFinal, velocidadBalon))
                .buildAndAttach();

        tieneElBalon = false; // soltó el balón
        
        System.out.println("Disparo efectuado. Ángulo de desviación: " + String.format("%.2f", anguloDesviacion) + " grados.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}