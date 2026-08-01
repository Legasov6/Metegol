package Metegol;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;

public class Cancha implements EntityFactory {

    @Spawns("BANDA")
    public Entity crearBanda(SpawnData data) {
        double width = Double.parseDouble(data.get("width").toString());
        double height = Double.parseDouble(data.get("height").toString());

        return FXGL.entityBuilder(data)
                .type(Partido.TipoEntidad.BANDA)
                .bbox(new HitBox(BoundingShape.box(width, height)))
                .collidable()
                .build();
    }

    @Spawns("FONDO")
    public Entity crearFondo(SpawnData data) {
        double width = Double.parseDouble(data.get("width").toString());
        double height = Double.parseDouble(data.get("height").toString());

        return FXGL.entityBuilder(data)
                .type(Partido.TipoEntidad.FONDO)
                .bbox(new HitBox(BoundingShape.box(width, height)))
                .collidable()
                .build();
    }

    @Spawns("GOL")
    public Entity crearGol(SpawnData data) {
        double width = Double.parseDouble(data.get("width").toString());
        double height = Double.parseDouble(data.get("height").toString());

        return FXGL.entityBuilder(data)
                .type(Partido.TipoEntidad.GOL)
                .bbox(new HitBox(BoundingShape.box(width, height)))
                .collidable()
                .build();
    }

    @Spawns("RED")
    public Entity crearRed(SpawnData data) {
        double width = Double.parseDouble(data.get("width").toString());
        double height = Double.parseDouble(data.get("height").toString());

        PhysicsComponent fisicas = new PhysicsComponent();
        fisicas.setBodyType(BodyType.STATIC);

        return FXGL.entityBuilder(data)
                .type(Partido.TipoEntidad.RED)
                .bbox(new HitBox(BoundingShape.box(width, height)))
                .with(fisicas)
                .collidable()
                .build();
    }}