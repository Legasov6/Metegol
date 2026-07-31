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
import javafx.scene.text.TextAlignment;

public class MenuAcercaDe {

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
        filtroOscuro.setFill(Color.rgb(0, 0, 0, 0.90)); // Opacidad alta para leer bien los créditos
        panelFondo.getChildren().add(filtroOscuro);

        // Titulo
        Text titulo = new Text("ACERCA DE METEGOL");
        titulo.setFont(Font.font("Impact", 50));
        titulo.setFill(Color.GOLD);

        // Texto de la información
        Text informacion = new Text(
            "=== DATOS INSTITUCIONALES ===\n" +
            "Universidad Nacional Experimental de Guayana.\n" +
            "Vicerrectorado Académico | Coordinación General de Pregrado.\n" +
            "P.F.G: Ingeniería en Informática.\n" +
            "Asignatura: Técnicas de Programación 3.\n\n" +
            
            "=== ESPECIFICACIONES TÉCNICAS ===\n" +
            "• Entorno de Desarrollo: NetBeans IDE.\n" +
            "• Versión de Java utilizada: Java SE 17 / 21.\n" +
            "• Herramienta de diseño UML: Gaphor.\n" +
            "• Framework principal para la gestión de escenas y motor del juego: FXGL (FX Game Library).\n" +
            "• Interfaz de usuario: JavaFX.\n" +
            "• Arquitectura de red Cliente-Servidor para multijugador: Java Sockets (java.net / java.io).\n\n" +
            
            "=== RESEÑAS DE LOS DESARROLLADORES ===\n" +
            "• Gabriel Tremaria: Arquitectura general y creación del minijuego 2D con físicas Box2D.\n" +
            "• Desarrollador 2: Lógica del mercado de fichajes y simulación del motor de juego.\n" +
            "• Desarrollador 3: Lógica del mercado de fichajes y gestión de entidades.\n" +
            "• Desarrollador 4: Conectividad por red (Serialización y Sockets).\n" +
            "• Frank Farias: Diseño e integración de la interfaz gráfica."
        );
        informacion.setFont(Font.font("Consolas", 16));
        informacion.setFill(Color.WHITE);
        informacion.setTextAlignment(TextAlignment.CENTER);
        informacion.setLineSpacing(3);

        // Botón Volver
        Button btnVolver = new Button("Volver al Menú");
        btnVolver.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnVolver.setPrefSize(250, 50);
        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz());
        });

        // Montaje
        VBox contenedorCentral = new VBox(25, titulo, informacion, btnVolver);
        contenedorCentral.setAlignment(Pos.CENTER);
        contenedorCentral.setPadding(new Insets(20));

        panelFondo.getChildren().add(contenedorCentral);

        return panelFondo;
    }
}