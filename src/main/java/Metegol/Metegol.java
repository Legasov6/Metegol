package Metegol;

import MenuJuego.MenuPrincipal; 

import Entidades.Equipo;
import Entidades.Futbolista;
import Entidades.Jugador;
import Entidades.MercadoFichajes;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Metegol extends GameApplication { 

    private Entity pelota;

    @Override
    protected void initSettings(GameSettings settings) {
       settings.setWidth(1280);
       settings.setHeight(720);
       settings.setTitle("Metegol - Champs 2026");
    }

    @Override
    protected void initGame() {
        entityBuilder()
                .at(0, 0)
                .view(new Rectangle(1280, 720, Color.web("#2e8b57"))) 
                .buildAndAttach();

        pelota = entityBuilder()
                .at(640, 360) // Centro de la pantalla HD
                .view(new Circle(15, Color.WHITE))
                .buildAndAttach();
    }

    @Override
    protected void initInput() {
        onBtnDown(MouseButton.PRIMARY, () -> {
            pelota.setPosition(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        });
    }

    @Override
    protected void initUI() {
        getGameScene().clearUINodes();
        var menu = MenuPrincipal.crearInterfaz();
        getGameScene().addUINode(menu);
    }

    public static void main(String[] args) {        
        launch(args); 
    }
}