package Minijuego;

import Entidades.Futbolista;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Es una fábrica de objetos proporcionada por FXGL. Implementar la interfaz nativa 
 * EntityFactory del framework. Facilita la creación de todos los objetos en el 
 * entorno 2D, asignándoles gráficas y formas de colisión (también llamadas hitboxes)
 * @author GabrielTremaria
 */
public class FabricaMinijuego implements EntityFactory {

    /**
     * Crea el objeto pelota con física dinámica, rebotabilidad y densidad.
     * @param data
     */
    @Spawns("balon")
    public Entity newBalon(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
       physics.setBodyType(BodyType.DYNAMIC);
        
        FixtureDef fd = new FixtureDef().density(0.3f).restitution(0.6f).friction(0.4f); //Atributos que FXGL pide para el balón
        physics.setFixtureDef(fd);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.BALON)
                .bbox(new HitBox(BoundingShape.circle(10))) // El tamaño del hitbox
                .view(new Circle(10, Color.WHITE))
                .with(physics)
                .with(new CollidableComponent(true)) // Permite detectar colisiones
                .build();
    }
    
    /**
     * Genera un jugador inyectándole su color de país, texto con su nombre y el
     * componente de Inteligencia Artificial.
     * @param data 
     */
  @Spawns("futbolista")
    public Entity newFutbolista(SpawnData data) {
        // Extraemos los datos lógicos de la clase correcta
        Futbolista stats = data.get("datos");
        boolean esAtacante = data.get("esAtacante");
         
        // Obtenemos la dirección de ataque
        boolean atacaHaciaArriba = data.hasKey("atacaHaciaArriba") ? data.get("atacaHaciaArriba") : true;
        
        // 1. Determinar el color del futbolista según el país
        javafx.scene.paint.Color colorEquipo = javafx.scene.paint.Color.GRAY; 
        String nombrePais = stats.getPais(); 
        
        if (nombrePais != null) {
            switch (nombrePais) {
                case "Argentina": colorEquipo = javafx.scene.paint.Color.LIGHTBLUE; break;
                case "Alemania":  colorEquipo = javafx.scene.paint.Color.WHITE; break;
                case "Francia":   colorEquipo = javafx.scene.paint.Color.NAVY; break;
                case "España":    colorEquipo = javafx.scene.paint.Color.RED; break;
            }
        }

        // 2. Crear los gráficos del futbolista
        javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(15, colorEquipo);
        circulo.setStroke(javafx.scene.paint.Color.BLACK);
        circulo.setStrokeWidth(2);

        javafx.scene.text.Text textoNombre = new javafx.scene.text.Text(stats.getNombre());
        textoNombre.setFill(javafx.scene.paint.Color.WHITE); 
        textoNombre.setTranslateX(-10);   
        textoNombre.setTranslateY(-20);   

        // 3. Físicas sólidas (Dynamic para que choquen y se bloqueen)
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(com.almasb.fxgl.physics.box2d.dynamics.BodyType.DYNAMIC);

        return FXGL.entityBuilder(data)
                .type(esAtacante ? TipoEntidad.JUGADOR_ATACANTE : TipoEntidad.JUGADOR_DEFENSOR)
                .viewWithBBox(circulo) 
                .view(textoNombre)     
                .with(physics)
                .with(new AtributosFutbolistaComponent(stats)) 
                .with(new CollidableComponent(true)) 
                .with(new IAFutbolistaComponent(esAtacante, atacaHaciaArriba)) // <-- ACTUALIZADO
                .build();
    }


    /**
     * Construye la entidad que representa las líneas de "fuera" del mapa .tmx, 
     * permitiendo delimitar la zona de juego.
     */
    @Spawns("limite")
    public Entity newLimite(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(TipoEntidad.LIMITE_CANCHA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new com.almasb.fxgl.entity.components.CollidableComponent(true))
                .build();
    }

    /**
     * Construye la entidad que representa la línea de gol.
     * @param data 
     */
    @Spawns("porteria")
    public Entity newPorteria(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.PORTERIA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(physics)
                .with(new com.almasb.fxgl.entity.components.CollidableComponent(true))
                .build();
    }
    /**
     * Construye la entidad que representa el palo de la portería
     * @param data
     */
    @Spawns("palo") //La pelota rebota de este
    public Entity newPalo(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC); 
        return FXGL.entityBuilder(data)
                .type(TipoEntidad.PALO)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(physics)
                .build();
    }
}