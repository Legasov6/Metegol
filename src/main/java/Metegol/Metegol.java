package Metegol;



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

public class Metegol {//extends GameApplication {

    /*private Entity pelota;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("Champs 2026 - Prueba de Motor FXGL");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGame() {
        // 1. Crear el fondo (Césped)
        entityBuilder()
                .at(0, 0)
                .view(new Rectangle(800, 600, Color.web("#2e8b57"))) // Verde oscuro
                .buildAndAttach();

        // 2. Crear una entidad básica (La pelota) en el centro de la pantalla
        pelota = entityBuilder()
                .at(400, 300)
                .view(new Circle(15, Color.WHITE))
                .buildAndAttach();
    }

    @Override
    protected void initInput() {
        // 3. Registrar el evento de clic izquierdo del ratón
        onBtnDown(MouseButton.PRIMARY, () -> {
            // Mover la pelota a las coordenadas exactas del ratón
            pelota.setPosition(getInput().getMouseXWorld(), getInput().getMouseYWorld());
        });
    }
*/
    public static void main(String[] args) {
        //launch(args);
        System.out.println("Probando");
        MercadoFichajes mercado = new MercadoFichajes();
        // Asegúrate de que la ruta apunte correctamente a donde guardaste el archivo
        mercado.cargarMercadoDesdeCSV("recursos/Plantilla.csv");

        /* // Imprimir para verificar que funcionó el polimorfismo
        for (Futbolista f : mercado.getBancoComun()) {
            System.out.println(f.getNombre() + " juega para " + f.getPais() + " y cuesta " + f.getPrecio() + " monedas.");
        }
        */
        System.out.println("-------------------------");
        
        Equipo francia = new Equipo("Francia");
        Jugador dtUsuario = new Jugador("Gabriel", 200, francia);
        
        Futbolista mbappe = mercado.getBancoComun().get(0); 
        Futbolista kante = mercado.getBancoComun().get(10);
        
        System.out.println("Mercado de Fichajes");
        
        dtUsuario.comprarFutbolista(mbappe, mercado);
        dtUsuario.comprarFutbolista(kante, mercado);
        
        
       System.out.println("Plantilla actual de" + francia.getNombrePais() + ": ");
       for (Futbolista f: francia.getTitulares()){
           System.out.println("- " + f.getNombre());
       }
    }
}