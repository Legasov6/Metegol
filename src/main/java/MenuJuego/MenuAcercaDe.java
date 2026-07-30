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

        // 1. FONDO CON FILTRO OSCURO
        try {
            var fondoView = FXGL.texture("fondoMenu.png", FXGL.getAppWidth(), FXGL.getAppHeight());
            panelFondo.getChildren().add(fondoView);
        } catch (Exception e) {
            panelFondo.setStyle("-fx-background-color: #1a2a3a;");
        }
        
        Rectangle filtroOscuro = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        filtroOscuro.setFill(Color.rgb(0, 0, 0, 0.90)); // Opacidad alta para leer bien los créditos
        panelFondo.getChildren().add(filtroOscuro);

        // 2. TÍTULO
        Text titulo = new Text("ACERCA DE METEGOL");
        titulo.setFont(Font.font("Impact", 50));
        titulo.setFill(Color.GOLD);

        // 3. TEXTO DE INFORMACIÓN (Corregido y actualizado)
        Text informacion = new Text(
            "=== DATOS INSTITUCIONALES ===\n" +
            "Universidad Nacional Experimental de Guayana.\n" +
            "Vicerrectorado Académico | Coordinación General de Pregrado.\n" +
            "P.F.G: Ingeniería en Informática.\n" +
            "Asignatura: Técnicas de Programación 3.\n\n" +
            
            "=== ESPECIFICACIONES TÉCNICAS ===\n" +
            "• Entorno de Desarrollo: NetBeans IDE sobre Linux (Fedora / KDE Plasma).\n" +
            "• Versión de Java utilizada: Java SE 17 / 21.\n" +
            "• Herramienta de diseño UML: Enterprise Architect / Visual Paradigm.\n" +
            "• FXGL (FX Game Library): Framework principal para la gestión de escenas y motor del juego.\n" +
            "• JavaFX: Estructuración visual de la interfaz de usuario.\n" +
            "• Java Sockets (java.net / java.io): Arquitectura de red Cliente-Servidor multijugador.\n\n" +
            
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

        // 4. BOTÓN DE VOLVER
        Button btnVolver = new Button("Volver al Menú");
        btnVolver.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnVolver.setPrefSize(250, 50);
        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz());
        });

        // 5. ENSAMBLAJE
        VBox contenedorCentral = new VBox(25, titulo, informacion, btnVolver);
        contenedorCentral.setAlignment(Pos.CENTER);
        contenedorCentral.setPadding(new Insets(20));

        panelFondo.getChildren().add(contenedorCentral);

        return panelFondo;
    }
}