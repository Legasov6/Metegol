package Metegol;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent; // Motor de movimiento automático
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton; // Para detectar el clic del ratón
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

    // Simulamos las estadísticas de tu objeto Futbolista para esta prueba 
    private int atributoDisparo = 70; // 70/100. Cambia este valor para probar la dispersión
    private boolean tieneElBalon = true; // Bandera para saber si podemos disparar

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

        // Movimiento básico con WASD
        FXGL.onKey(KeyCode.W, () -> jugador.translateY(-velocidad));
        FXGL.onKey(KeyCode.S, () -> jugador.translateY(velocidad));
        FXGL.onKey(KeyCode.A, () -> jugador.translateX(-velocidad));
        FXGL.onKey(KeyCode.D, () -> jugador.translateX(velocidad));

        // MECÁNICA DE DISPARO: Se activa solo una vez al hacer clic derecho
        FXGL.onBtnDown(MouseButton.SECONDARY, () -> {
            if (tieneElBalon) {
                efectuarDisparo();
            }
        });
    }

    @Override
    protected void initGame() {
      // 1. La fábrica lee las colisiones
        FXGL.getGameWorld().addEntityFactory(new Cancha());

        // 2. EL MOTOR DIBUJA TODO SOLO
        // FXGL leerá el archivo .tmx, dibujará el césped usando el Tileset
        // y creará los objetos de colisión automáticamente.
        FXGL.setLevelFromMap("cancha.tmx");

        // 3. Jugador
        jugador = FXGL.entityBuilder()
                .type(TipoEntidad.JUGADOR)
                .at(640, 384)
                .viewWithBBox(new Circle(15, Color.BLUE))
                .collidable()
                .buildAndAttach();
    }
    
    @Override
    protected void initPhysics() {
        // Colisión: Jugador vs Banda Lateral
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.BANDA) {
            @Override
            protected void onCollisionBegin(Entity jugador, Entity banda) {
                System.out.println("¡FUERA! El jugador pisó la banda lateral.");
                
                // Opcional: Hacer rebotar al jugador un poco para que no se salga de la pantalla
                // jugador.translateX(10); 
            }
        });

        // Colisión: Jugador vs Fondo (Líneas de Córner/Saque de meta)
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.FONDO) {
            @Override
            protected void onCollisionBegin(Entity jugador, Entity fondo) {
                System.out.println("¡FONDO! El jugador pisó la línea trasera.");
            }
        });
        
        // Colisión: Jugador vs Red (El muro físico)
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(TipoEntidad.JUGADOR, TipoEntidad.RED) {
            @Override
            protected void onCollision(Entity jugador, Entity red) {
                // Usamos onCollision (continuo) en lugar de onCollisionBegin (una vez)
                // para imprimir esto mientras esté empujando la red
                System.out.println("Chocando contra la red...");
            }
        });
    }
    /**
     * Lógica matemática para calcular el vector, la desviación y la velocidad.
     */
    private void efectuarDisparo() {
        // 1. Obtener coordenadas actuales
        Point2D posicionJugador = jugador.getPosition();
        // FXGL nos da la posición exacta del cursor en la pantalla
        Point2D posicionRaton = FXGL.getInput().getMousePositionWorld(); 

        // 2. Calcular el Vector de Dirección base (hacia donde apuntaste)
        // Se resta el destino menos el origen
        Point2D direccionBase = posicionRaton.subtract(posicionJugador);

        // 3. Sistema de Dispersión (El cono de error)
        // Si tienes 100 de disparo, el error es 0. Si tienes 50, el error base es 50.
        double factorError = 100 - atributoDisparo; 
        
        // Multiplicamos por 0.5 para que el ángulo máximo de desviación no sea absurdamente injugable.
        // Si el disparo es 60, el factor es 40. 40 * 0.5 = 20 grados máximos de desviación.
        double gradosMaximos = factorError * 0.5; 
        
        // FXGL randomiza un número entre negativo y positivo para desviar a izquierda o derecha
        double anguloDesviacion = FXGL.random(-gradosMaximos, gradosMaximos);

        // Rotamos el vector perfecto usando un poco de trigonometría interna de Point2D
        // Math.cos y Math.sin requieren radianes, así que convertimos los grados.
        double radianes = Math.toRadians(anguloDesviacion);
        double cos = Math.cos(radianes);
        double sin = Math.sin(radianes);

        // Fórmula estándar de rotación de vectores 2D
        double nuevoX = direccionBase.getX() * cos - direccionBase.getY() * sin;
        double nuevoY = direccionBase.getX() * sin + direccionBase.getY() * cos;
        Point2D direccionFinal = new Point2D(nuevoX, nuevoY);

        // 4. Calcular Velocidad (Potencia del balonazo)
        // Convertimos el atributo (0-100) a píxeles por segundo (ej. 100 -> 1000px/s)
        double velocidadBalon = atributoDisparo * 10.0;

        // 5. Instanciar y disparar la pelota
        // La "spawneamos" justo donde está el jugador
        pelota = FXGL.entityBuilder()
                .type(TipoEntidad.PELOTA)
                .at(posicionJugador) 
                .viewWithBBox(new Circle(8, Color.WHITE))
                .collidable()
                // Aquí conectamos el vector que calculamos con el motor de FXGL
                .with(new ProjectileComponent(direccionFinal, velocidadBalon))
                .buildAndAttach();

        tieneElBalon = false; // Ya soltó el balón
        
        System.out.println("Disparo efectuado. Ángulo de desviación: " + String.format("%.2f", anguloDesviacion) + " grados.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}