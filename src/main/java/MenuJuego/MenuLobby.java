// @author Frank Farias

package MenuJuego;

import Logica.GestorJuego;
import Logica.ServidorLocal;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuLobby {

    public static StackPane crearInterfaz() {
        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        panelFondo.setStyle("-fx-background-color: #1a2a3a;"); 

        Text titulo = new Text("CONFIGURAR PARTIDA");
        titulo.setFont(Font.font("Impact", 60));
        titulo.setFill(Color.GOLD);

        Text subtitulo = new Text("Elige el modo de juego:");
        subtitulo.setFont(Font.font("System", 24));
        subtitulo.setFill(Color.WHITE);
        subtitulo.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Botón 1: Modo CPU (Pasa directo a armar el equipo) SOLO PARA PRUEBAS
        Button btnOffline = new Button("Jugar contra CPU");
        btnOffline.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnOffline.setPrefSize(250, 50);
        btnOffline.setOnAction(e -> {
            // Limpiamos variables de red para asegurar que el motor entienda que es Offline
            GestorJuego.getInstance().setEsHost(false); 
            GestorJuego.getInstance().setServidor(null);
            GestorJuego.getInstance().setCliente(null);
            
            // Saltamos directo a la selección de equipos
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuEquipos.crearInterfaz());
        });

        // Botón 2: Modo online (Inicia el servidor y espera)
        Button btnOnline = new Button("Crear Sala Online");
        btnOnline.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnOnline.setPrefSize(250, 50);

        HBox cajaBotones = new HBox(30, btnOffline, btnOnline);
        cajaBotones.setAlignment(Pos.CENTER);

        Button btnVolver = new Button("Volver al Menú");
        btnVolver.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;");
        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz());
        });

        VBox contenedor = new VBox(40, titulo, subtitulo, cajaBotones, btnVolver);
        contenedor.setAlignment(Pos.CENTER);
        panelFondo.getChildren().add(contenedor);

        // Acción del modo online
        btnOnline.setOnAction(e -> {
            // Cambiamos los textos para indicar que estamos esperando
            titulo.setText("SALA DE ESPERA");
            subtitulo.setText("Esperando a que se conecte el Jugador 2...\n(El juego avanzará automáticamente)");
            cajaBotones.setVisible(false); // Ocultamos los botones de elección
            
            // Configuramos la red
            GestorJuego.getInstance().setEsHost(true);
            ServidorLocal servidor = new ServidorLocal();
            GestorJuego.getInstance().setServidor(servidor);

            // Cuando alguien se conecte, cambiamos a MenuEquipos
            servidor.iniciarServidor(() -> {
                FXGL.getGameScene().clearUINodes();
                FXGL.getGameScene().addUINode(MenuEquipos.crearInterfaz());
            });
        });

        return panelFondo;
    }
}