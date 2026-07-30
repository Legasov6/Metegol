package MenuJuego;

import Minijuego.EscenaMinijuego;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuPrincipal {

    public static StackPane crearInterfaz() {
        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());

        // 1. CARGA DEL FONDO
        try {
            var fondoView = FXGL.texture("fondoMenu.png", FXGL.getAppWidth(), FXGL.getAppHeight());
            panelFondo.getChildren().add(fondoView);
        } catch (Exception e) {
            System.err.println("Advertencia: No se encontró fondoMenu.png en assets/textures/");
        }
        
        // 2. TÍTULO
        Text titulo = new Text("METEGOL");
        titulo.setFont(Font.font("Impact", 80)); 
        titulo.setFill(Color.WHITE);
        titulo.setStroke(Color.BLACK); 
        titulo.setStrokeWidth(2);

        // =========================================================
        // ESTILOS NORMAL Y HOVER (Con escala forzada a 1 para evitar encogimiento)
        // =========================================================
        String estiloNormal = "-fx-background-color: linear-gradient(to bottom, #FFD700, #DAA520); " 
                           + "-fx-text-fill: #000000; "
                           + "-fx-font-weight: bold; "
                           + "-fx-background-radius: 15; "
                           + "-fx-border-color: #FFFFFF; "
                           + "-fx-border-radius: 15; "
                           + "-fx-cursor: hand; "
                           + "-fx-scale-x: 1; -fx-scale-y: 1;"; // Bloquea cualquier escala externa

        String estiloHover = "-fx-background-color: linear-gradient(to bottom, #FFFFFF, #FFD700); " 
                           + "-fx-text-fill: #000000; "
                           + "-fx-font-weight: bold; "
                           + "-fx-background-radius: 15; "
                           + "-fx-border-color: #FFFFFF; "
                           + "-fx-border-radius: 15; "
                           + "-fx-cursor: hand; "
                           + "-fx-scale-x: 1; -fx-scale-y: 1;"; // Bloquea cualquier escala externa

        // 3. BOTONES
        Button btnCrearJuego = new Button("Crear Juego");
        btnCrearJuego.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuLobby.crearInterfaz());
        });

        Button btnUnirse = new Button("Unirse a Juego");
        btnUnirse.setOnAction(e -> {
            // Usamos una ventana de diálogo nativa de JavaFX
            javafx.scene.control.TextInputDialog dialogo = new javafx.scene.control.TextInputDialog("127.0.0.1");
            dialogo.setTitle("Conectar a Partida");
            dialogo.setHeaderText("Ingresa la IP del Host (Jugador 1):");
            dialogo.setContentText("Dirección IP:");

            // Si el usuario presiona "Aceptar" y escribe una IP...
            dialogo.showAndWait().ifPresent(ip -> {
                
                // Nos registramos como Cliente
                Logica.GestorJuego.getInstance().setEsHost(false);
                Logica.ClienteRed cliente = new Logica.ClienteRed();
                Logica.GestorJuego.getInstance().setCliente(cliente);

                // Pasamos el "Gatillo": Si logra conectarse, cambiamos a MenuEquipos
                cliente.conectarAlServidor(ip, () -> {
                    FXGL.getGameScene().clearUINodes();
                    FXGL.getGameScene().addUINode(MenuEquipos.crearInterfaz());
                });
            });
        });

        Button btnEstadisticas = new Button("Estadísticas");
        btnEstadisticas.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuEstadisticas.crearInterfaz());
        });

        Button btnComoJugar = new Button("Cómo Jugar");
        btnComoJugar.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuComoJugar.crearInterfaz()); 
        });
        
        // NUEVO BOTÓN: Acerca De
        Button btnAcercaDe = new Button("Acerca de");
        btnAcercaDe.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuAcercaDe.crearInterfaz());
        });
        
        // =========================================================
        // APLICAR TAMAÑOS, ESTILOS Y EVENTOS HOVER CON UN BUCLE
        // =========================================================
        // Añadimos btnAcercaDe a la lista de botones
        Button[] botones = {btnCrearJuego, btnUnirse, btnEstadisticas, btnComoJugar, btnAcercaDe};
        for (Button btn : botones) {
            btn.setFont(Font.font("System", 18));
            btn.setPrefWidth(240);
            btn.setStyle(estiloNormal);
            
            btn.setOnMouseEntered(e -> btn.setStyle(estiloHover));
            btn.setOnMouseExited(e -> btn.setStyle(estiloNormal));
        }

        // 4. AGRUPADOR CENTRAL
        VBox contenedorCentral = new VBox(20); // Reduje un poco el espaciado a 20 para que quepan todos cómodamente
        contenedorCentral.setAlignment(Pos.CENTER); 
        // Añadimos btnAcercaDe al VBox
        contenedorCentral.getChildren().addAll(titulo, btnCrearJuego, btnUnirse, btnEstadisticas, btnComoJugar, btnAcercaDe);

        // 5. ENSAMBLAJE FINAL
        panelFondo.getChildren().add(contenedorCentral);

        return panelFondo;
    }
}