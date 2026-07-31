// @author Frank Farias

package MenuJuego;

import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuComoJugar {

    public static StackPane crearInterfaz() {
        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        // Fondo con filtro oscuro
        try {
            var fondoView = FXGL.texture("fondoMenu.png", FXGL.getAppWidth(), FXGL.getAppHeight());
            panelFondo.getChildren().add(fondoView);
        } catch (Exception e) {
            panelFondo.setStyle("-fx-background-color: #1a2a3a;");
        }
        
        Rectangle filtroOscuro = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        filtroOscuro.setFill(Color.rgb(0, 0, 0, 0.85)); // 85% de opacidad para que el texto resalte
        panelFondo.getChildren().add(filtroOscuro);

        // Titulo
        Text titulo = new Text("MANUAL DE JUEGO");
        titulo.setFont(Font.font("Impact", 60));
        titulo.setFill(Color.GOLD);

        // Manual de juego
        Text instrucciones = new Text(
            "⚽ FASE DE GESTIÓN:\n" +
            "Arma tu equipo comprando jugadores en base a sus atributos y cuidando tu presupuesto. " +
            "Luego, observa la simulación del partido. Cuando haya una jugada de peligro, ¡entrarás al Minijuego!\n\n" +
            "⚔️ CONTROLES DE ATAQUE:\n" +
            "• WASD: Mover a tu jugador con el balón.\n" +
            "• Clic Izquierdo: Dar un pase a un compañero o disparar a portería.\n\n" +
            "🛡️ CONTROLES DE DEFENSA:\n" +
            "• WASD: Mover al defensor seleccionado.\n" +
            "• Choque Físico: Toca al atacante rival para robar la pelota y despejar el peligro.\n" +
            "• Tecla Q: Cambiar el control a otro defensor de campo.\n" +
            "• Barra Espaciadora: Tomar el control manual del Portero.\n" +
            "• Clic Izquierdo (Como Portero): Desplegar un muro defensivo rápido para atajar tiros."
        );
        instrucciones.setFont(Font.font("System", 22));
        instrucciones.setFill(Color.WHITE);
        instrucciones.setWrappingWidth(FXGL.getAppWidth() - 150); // Evita que el texto se salga de la pantalla
        instrucciones.setLineSpacing(5);

        // Botón Volver
        Button btnVolver = new Button("Volver al Menú");
        btnVolver.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnVolver.setPrefSize(250, 50);
        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz());
        });

        // Montaje
        VBox contenedorCentral = new VBox(40, titulo, instrucciones, btnVolver);
        contenedorCentral.setAlignment(Pos.CENTER);
        contenedorCentral.setPadding(new Insets(30));

        panelFondo.getChildren().add(contenedorCentral);

        return panelFondo;
    }
}