package MenuJuego;

import Logica.GestorJuego;
import Logica.ServidorLocal;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
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

        Text titulo = new Text("SALA DE ESPERA");
        titulo.setFont(Font.font("Impact", 60));
        titulo.setFill(Color.GOLD);

        Text subtitulo = new Text("Esperando a que se conecte el Jugador 2...\n(El juego avanzará automáticamente)");
        subtitulo.setFont(Font.font("System", 24));
        subtitulo.setFill(Color.WHITE);
        subtitulo.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button btnVolver = new Button("Cancelar y Volver");
        btnVolver.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 18px;");
        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz());
        });

        VBox contenedor = new VBox(40, titulo, subtitulo, btnVolver);
        contenedor.setAlignment(Pos.CENTER);
        panelFondo.getChildren().add(contenedor);

        // =========================================================
        // LÓGICA DE RED: Iniciamos el Servidor al entrar aquí
        // =========================================================
        GestorJuego.getInstance().setEsHost(true);
        ServidorLocal servidor = new ServidorLocal();
        GestorJuego.getInstance().setServidor(servidor);

        // Pasamos el "Gatillo": Cuando alguien se conecte, cambiamos a MenuEquipos
        servidor.iniciarServidor(() -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuEquipos.crearInterfaz());
        });

        return panelFondo;
    }
}