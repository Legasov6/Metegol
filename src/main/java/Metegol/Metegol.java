package Metegol;

// Importa el nuevo paquete del menú que creamos
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

// 1. DESCOMENTAMOS LA HERENCIA. Esto es vital para que FXGL funcione.
public class Metegol extends GameApplication { 

    private Entity pelota;

    @Override
    protected void initSettings(GameSettings settings) {
       settings.setWidth(1280);
       settings.setHeight(720);
       settings.setTitle("Metegol - Champs 2026");
       // settings.setFullScreenAllowed(true); 
    }

    @Override
    protected void initGame() {
        // 2. Ajustamos el césped al nuevo tamaño de la ventana (1280x720)
        // FXGL dibujará esto, pero quedará "oculto" detrás del panel del menú principal
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

    // 3. ¡EL PUENTE CON LA INTERFAZ!
    // Este método se ejecuta al arrancar y pone tu menú por encima de todo
    @Override
    protected void initUI() {
        getGameScene().clearUINodes();
        // Llamamos a la clase MenuPrincipal que debe estar en el paquete MenuJuego
        var menu = MenuPrincipal.crearInterfaz();
        getGameScene().addUINode(menu);
    }

    public static void main(String[] args) {
        // 4. LIMPIEZA DEL MAIN
        // Toda la lógica de consola (System.out.println, comprarFutbolista, etc.) 
        // debe quedarse en tu clase "PruebaMercado.java". 
        // El main de la aplicación real solo debe hacer una cosa: arrancar el motor.
        
        launch(args); 
    }
}