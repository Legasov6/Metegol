package MenuJuego;

import Entidades.Equipo;
import Entidades.Jugador;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuEquipos {

    private static String equipoSeleccionado = ""; 

    public static StackPane crearInterfaz() {
        equipoSeleccionado = ""; 

        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        panelFondo.setStyle("-fx-background-color: #1a2a3a;"); 

        Text titulo = new Text("SELECCIONA TU SELECCIÓN");
        titulo.setFont(Font.font("Impact", 48));
        titulo.setFill(Color.WHITE);

        Text subtitulo = new Text("Equipo actual: Ninguno");
        subtitulo.setFont(Font.font("System", 24));
        subtitulo.setFill(Color.YELLOW);

        String estiloBase = "-fx-background-color: #333333; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;";
        
        Button btnArgentina = new Button("Argentina");
        Button btnAlemania = new Button("Alemania");
        Button btnEspana = new Button("España");
        Button btnFrancia = new Button("Francia");

        Button[] botonesEquipos = {btnArgentina, btnAlemania, btnEspana, btnFrancia};

        for (Button btn : botonesEquipos) {
            btn.setStyle(estiloBase);
            btn.setPrefSize(150, 50);
            btn.setOnAction(e -> {
                equipoSeleccionado = btn.getText();
                subtitulo.setText("Equipo actual: " + equipoSeleccionado);
            });
        }

        HBox filaEquipos = new HBox(20, btnArgentina, btnAlemania, btnEspana, btnFrancia);
        filaEquipos.setAlignment(Pos.CENTER);
        
        // =========================================================
        // NUEVO: CAMPO DE TEXTO PARA EL NOMBRE DEL DT
        // =========================================================
        TextField inputNombre = new TextField();
        inputNombre.setPromptText("Ingresa tu nombre de DT...");
        inputNombre.setMaxWidth(300);
        inputNombre.setFont(Font.font("System", 18));
        inputNombre.setStyle("-fx-alignment: center;"); // Centra el texto al escribir

        // BOTÓN CONFIRMAR
        Button btnConfirmar = new Button("Confirmar Equipo");
        btnConfirmar.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnConfirmar.setPrefSize(250, 60);
        
        btnConfirmar.setOnAction(e -> {
            String nombreDT = inputNombre.getText().trim();

            if (equipoSeleccionado.isEmpty()) {
                subtitulo.setText("¡DEBES SELECCIONAR UN EQUIPO PRIMERO!");
                subtitulo.setFill(Color.RED);
            } else if (nombreDT.isEmpty()) {
                subtitulo.setText("¡DEBES INGRESAR TU NOMBRE DE DT!");
                subtitulo.setFill(Color.RED);
            } else {
                // NACE EL EQUIPO EN MEMORIA
                Equipo nuevoEquipo = new Equipo(equipoSeleccionado);
                
                // NACE EL MÁNAGER USANDO EL TEXTO INGRESADO
                Jugador dtActual = new Jugador(nombreDT, 1000, nuevoEquipo); 

                FXGL.getGameScene().clearUINodes();
                FXGL.getGameScene().addUINode(MenuPlantilla.crearInterfaz(dtActual));
            }
        });

        // Ensamblamos todo, añadiendo el inputNombre al VBox
        VBox contenedor = new VBox(40, titulo, inputNombre, subtitulo, filaEquipos, btnConfirmar);
        contenedor.setAlignment(Pos.CENTER);
        panelFondo.getChildren().add(contenedor);

        return panelFondo;
    }
}