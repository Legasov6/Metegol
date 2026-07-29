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

public class FabricaMinijuego implements EntityFactory {

    // 1. LA PELOTA
    @Spawns("balon")
    public Entity newBalon(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
       physics.setBodyType(BodyType.DYNAMIC);
        
        // Físicas reales para el balón: poco peso, buen rebote (restitution) y fricción
        FixtureDef fd = new FixtureDef().density(0.3f).restitution(0.6f).friction(0.4f);
        physics.setFixtureDef(fd);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.BALON)
                .bbox(new HitBox(BoundingShape.circle(10))) // Radio de 10px
                .view(new Circle(10, Color.WHITE))
                .with(physics)
                .with(new CollidableComponent(true)) // Permite detectar colisiones
                .build();
    }

  @Spawns("futbolista")
    public Entity newFutbolista(SpawnData data) {
        // Extraemos los datos lógicos de la clase correcta
        Futbolista stats = data.get("datos");
        boolean esAtacante = data.get("esAtacante");
         
        // Obtenemos la dirección de ataque (Por defecto asumimos que ataca hacia arriba para evitar nulls)
        boolean atacaHaciaArriba = data.hasKey("atacaHaciaArriba") ? data.get("atacaHaciaArriba") : true;
        
        // 1. Determinar el color según el país
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

        // 2. Crear los gráficos (Círculo + Texto)
        javafx.scene.shape.Circle circulo = new javafx.scene.shape.Circle(15, colorEquipo);
        circulo.setStroke(javafx.scene.paint.Color.BLACK);
        circulo.setStrokeWidth(2);

        javafx.scene.text.Text textoNombre = new javafx.scene.text.Text(stats.getNombre());
        textoNombre.setFill(javafx.scene.paint.Color.WHITE); 
        textoNombre.setTranslateX(-10);   
        textoNombre.setTranslateY(-20);   

        // 3. Físicas Sólidas (Dynamic para que choquen y se bloqueen)
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


    
    // ==========================================
    // CAJAS DE COLISIÓN DE TILED
    // ==========================================

    @Spawns("limite")
    public Entity newLimite(SpawnData data) {
        // Al NO ponerle un PhysicsComponent, Box2D ignorará este objeto para los rebotes físicos,
        // pero FXGL seguirá usando el HitBox para disparar nuestro evento de colisión lógica.
        return FXGL.entityBuilder(data)
                .type(TipoEntidad.LIMITE_CANCHA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new com.almasb.fxgl.entity.components.CollidableComponent(true))
                .build();
    }

    @Spawns("porteria")
    public Entity newPorteria(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC);

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.PORTERIA)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(physics)
                .with(new com.almasb.fxgl.entity.components.CollidableComponent(true)) // <-- Y AQUÍ TAMBIÉN
                .build();
    }
    
    @Spawns("palo")
    public Entity newPalo(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.STATIC); 
        // Nota: Al ser STATIC y tener colisiones, la pelota rebotará naturalmente contra él
        // gracias al motor Box2D de FXGL, exactamente como si fuera una pared.

        return FXGL.entityBuilder(data)
                .type(TipoEntidad.PALO)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(physics)
                .build();
    }
}