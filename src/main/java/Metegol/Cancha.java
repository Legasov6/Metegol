/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

// 1. LAS BANDAS (Sensores)
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

    // 2. EL FONDO (Sensores para Córner)
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

    // 3. LA LÍNEA DE GOL (Sensor de anotación)
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

    // 4. LA RED FÍSICA (Muro sólido)
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