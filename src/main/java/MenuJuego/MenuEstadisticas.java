package MenuJuego;

import Entidades.RegistroCampeon;
import Logica.GestorEstadisticas;
import com.almasb.fxgl.dsl.FXGL;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.List;

public class MenuEstadisticas {

    public static StackPane crearInterfaz() {
        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        panelFondo.setStyle("-fx-background-color: #1a1a1a;");

        Text titulo = new Text("SALÓN DE LA FAMA");
        titulo.setFont(Font.font("Impact", 50));
        titulo.setFill(Color.GOLD);

        // =========================================================
        // CREACIÓN DE LA TABLA
        // =========================================================
        TableView<RegistroCampeon> tabla = new TableView<>();
        tabla.setPrefSize(800, 400);
        tabla.setMaxSize(800, 400);
        tabla.setStyle("-fx-font-size: 16px; -fx-background-color: #333333;");

        // Columna 1: Director Técnico
        TableColumn<RegistroCampeon, String> colDT = new TableColumn<>("Director Técnico");
        colDT.setPrefWidth(200);
        colDT.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombreDT()));

        // Columna 2: Selección
        TableColumn<RegistroCampeon, String> colSeleccion = new TableColumn<>("Selección");
        colSeleccion.setPrefWidth(180);
        colSeleccion.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSeleccion()));

        // Columna 3: Marcador Final
        TableColumn<RegistroCampeon, String> colMarcador = new TableColumn<>("Marcador");
        colMarcador.setPrefWidth(150);
        colMarcador.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMarcadorFinal()));

        // Columna 4: Fecha y Hora
        TableColumn<RegistroCampeon, String> colFecha = new TableColumn<>("Fecha de Consagración");
        colFecha.setPrefWidth(250);
        colFecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFechaHora()));

        tabla.getColumns().addAll(colDT, colSeleccion, colMarcador, colFecha);

        // Alimentamos la tabla con el archivo de texto
        List<RegistroCampeon> historial = GestorEstadisticas.obtenerHistorial();
        tabla.getItems().addAll(historial);
        // =========================================================

        Button btnVolver = new Button("Volver al Menú Principal");
        btnVolver.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnVolver.setPrefSize(250, 50);
        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz()); 
        });

        VBox contenedor = new VBox(30, titulo, tabla, btnVolver);
        contenedor.setAlignment(Pos.CENTER);
        
        panelFondo.getChildren().add(contenedor);
        return panelFondo;
    }
}