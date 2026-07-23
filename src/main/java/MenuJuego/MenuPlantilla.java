package MenuJuego;

import Entidades.Equipo;
import Logica.GestorJuego;
import MenuJuego.PantallaSimulacion;
import Entidades.Futbolista;
import Entidades.Jugador;
import Entidades.MercadoFichajes;
import com.almasb.fxgl.dsl.FXGL;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class MenuPlantilla {

    public static StackPane crearInterfaz(Jugador dtActual) {
        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        panelFondo.setStyle("-fx-background-color: #222222;");

        String pais = dtActual.getEquipoAsignado().getNombrePais();

        // 1. TÍTULO
        Text titulo = new Text("DRAFT: " + pais.toUpperCase());
        titulo.setFont(Font.font("Impact", 40));
        titulo.setFill(Color.WHITE);

        // 2. EL MENSAJE CENTRAL (Solo para errores o información del sistema)
        Text txtMensaje = new Text("Selecciona a tus 11 titulares");
        txtMensaje.setFont(Font.font("System", 20));
        txtMensaje.setFill(Color.LIGHTGRAY);

        // 3. LOS INDICADORES (Presupuesto y Jugadores)
        Text txtIndicadores = new Text("Presupuesto: " + dtActual.getPresupuesto() + "$ | Jugadores: 0/11");
        txtIndicadores.setFont(Font.font("System", 24));
        txtIndicadores.setFill(Color.YELLOW);
        
        // Magia del StackPane: Forzamos este elemento a la esquina superior derecha
        StackPane.setAlignment(txtIndicadores, Pos.TOP_RIGHT);
        // Le damos un margen para que no quede pegado a los bordes de la pantalla (Arriba, Derecha, Abajo, Izquierda)
        StackPane.setMargin(txtIndicadores, new Insets(20, 30, 0, 0));

        VBox listaDisponibles = new VBox(10);
        listaDisponibles.setPadding(new Insets(15));
        listaDisponibles.setStyle("-fx-background-color: #333333;");

        VBox listaConvocados = new VBox(10);
        listaConvocados.setPadding(new Insets(15));
        listaConvocados.setStyle("-fx-background-color: #1a3a1a;");

        MercadoFichajes mercado = new MercadoFichajes();
        mercado.cargarMercadoDesdeCSV("recursos/Plantilla.csv");

        for (Futbolista f : mercado.getBancoComun()) {
            if (f.getPais().equalsIgnoreCase(pais)) {
                // Pasamos AMBOS textos al botón para que actualice el dinero o tire el error
                Button btnJugador = crearBotonJugador(f, dtActual, mercado, listaDisponibles, listaConvocados, txtMensaje, txtIndicadores);
                listaDisponibles.getChildren().add(btnJugador);
            }
        }

        ScrollPane scrollIzq = new ScrollPane(listaDisponibles);
        scrollIzq.setPrefSize(500, 450);
        scrollIzq.setFitToWidth(true);
        scrollIzq.setStyle("-fx-background: #333333; -fx-border-color: #777777;");

        ScrollPane scrollDer = new ScrollPane(listaConvocados);
        scrollDer.setPrefSize(500, 450);
        scrollDer.setFitToWidth(true);
        scrollDer.setStyle("-fx-background: #1a3a1a; -fx-border-color: #28a745;");

        HBox contenedorListas = new HBox(40, scrollIzq, scrollDer);
        contenedorListas.setAlignment(Pos.CENTER);

        Button btnJugar = new Button("¡Confirmar y Jugar!");
        btnJugar.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnJugar.setPrefSize(300, 50);
        
        btnJugar.setOnAction(e -> {
            int cantidad = dtActual.getEquipoAsignado().getTitulares().size();
            
            if (cantidad == 11) {
                boolean tienePortero = dtActual.getEquipoAsignado().getTitulares().stream()
                                       .anyMatch(j -> j instanceof Entidades.Portero);
                
                if (tienePortero) {
                    System.out.println("¡Equipo legal en memoria! Procesando...");

                    // 1. Guardamos el DT y el Equipo globalmente (Esto se hace en ambos modos)
                    Logica.GestorJuego.getInstance().setDtLocal(dtActual);
                    Logica.GestorJuego.getInstance().setEquipoLocal(dtActual.getEquipoAsignado());
                    
                    // 2. Desactivamos el botón para evitar que el usuario haga doble clic
                    btnJugar.setDisable(true);

                    // =========================================================
                    // 3. BIFURCACIÓN: ¿ONLINE U OFFLINE?
                    // =========================================================
                    if (Logica.GestorJuego.getInstance().getServidor() != null || Logica.GestorJuego.getInstance().getCliente() != null) {
                        
                        // MODO ONLINE
                        txtMensaje.setText("Alineación lista. Esperando a que el rival confirme...");
                        txtMensaje.setFill(Color.YELLOW);
                        btnJugar.setText("Esperando al rival...");
                        
                        // Lanzamos el hilo de red. La pantalla no cambiará hasta que el rival envíe sus datos.
                        Logica.GestorJuego.getInstance().intercambiarPlantillasOnline(() -> {
                            FXGL.getGameScene().clearUINodes();
                            FXGL.getGameScene().addUINode(PantallaSimulacion.crearInterfaz());
                        });
                        
                    } else {
                        // MODO OFFLINE (El clásico contra el Bot)
                        txtMensaje.setText("¡Alineación confirmada! Simulando partido...");
                        txtMensaje.setFill(Color.LIGHTGREEN);
                        
                        Logica.GestorJuego.getInstance().iniciarPartidoContraBot();
                        FXGL.getGameScene().clearUINodes();
                        FXGL.getGameScene().addUINode(PantallaSimulacion.crearInterfaz());
                    }

                } else {
                    txtMensaje.setText("¡ALINEACIÓN INVÁLIDA! Necesitas fichar a un portero.");
                    txtMensaje.setFill(Color.RED);
                }
            } else {
                txtMensaje.setText("¡NECESITAS EXACTAMENTE 11 JUGADORES! (Tienes " + cantidad + ")");
                txtMensaje.setFill(Color.RED);
            }
        });

        // 4. ENSAMBLAJE FINAL
        // Metemos los elementos centrales en su VBox...
        VBox layoutPrincipal = new VBox(20, titulo, txtMensaje, contenedorListas, btnJugar);
        layoutPrincipal.setAlignment(Pos.CENTER);
        
        // ... Y luego añadimos TANTO el layout principal COMO los indicadores al StackPane
        panelFondo.getChildren().addAll(layoutPrincipal, txtIndicadores);

        return panelFondo;
    }

    private static Button crearBotonJugador(Futbolista f, Jugador dtActual, MercadoFichajes mercado, VBox disponibles, VBox convocadosBox, Text txtMensaje, Text txtIndicadores) {
        
        // Formateamos el texto en DOS líneas usando \n para que parezca una "tarjeta" pequeña
        String textoBoton = String.format("%s (%s)\nVel: %d | Dis: %d | Pas: %d | Def: %d | Precio: $%d", 
                                          f.getNombre(), 
                                          f.getClass().getSimpleName(),
                                          f.getVelocidad(),
                                          f.getDisparo(),
                                          f.getPase(),
                                          f.getDefensa(),
                                          f.getPrecio());
                                          
        Button btn = new Button(textoBoton);
        
        btn.setMaxWidth(Double.MAX_VALUE);
        // Añadimos padding para darle espacio a las dos líneas de texto
        btn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px;");

        btn.setOnAction(e -> {
            if (disponibles.getChildren().contains(btn)) {
                
                if (f instanceof Entidades.Portero) {
                    boolean yaTienePortero = dtActual.getEquipoAsignado().getTitulares().stream()
                                            .anyMatch(jugador -> jugador instanceof Entidades.Portero);
                    if (yaTienePortero) {
                        txtMensaje.setText("¡ACCIÓN DENEGADA! Ya tienes un portero titular.");
                        txtMensaje.setFill(Color.RED);
                        return;
                    }
                }

                if (dtActual.comprarFutbolista(f, mercado)) {
                    disponibles.getChildren().remove(btn);
                    convocadosBox.getChildren().add(btn);
                    // Mantenemos el padding en el estilo verde también
                    btn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px;");
                    
                    txtMensaje.setText("Fichaje exitoso.");
                    txtMensaje.setFill(Color.LIGHTGRAY);
                } else {
                    txtMensaje.setText("¡FONDOS INSUFICIENTES O EQUIPO LLENO!");
                    txtMensaje.setFill(Color.RED);
                    return; 
                }

            } else {
                if (dtActual.devolverFutbolista(f, mercado)) {
                    convocadosBox.getChildren().remove(btn);
                    disponibles.getChildren().add(btn);
                    // Devolvemos el padding en el estilo gris
                    btn.setStyle("-fx-background-color: #555555; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px;");
                    
                    txtMensaje.setText("Jugador devuelto al mercado.");
                    txtMensaje.setFill(Color.LIGHTGRAY);
                }
            }
            
            txtIndicadores.setText("Presupuesto: " + dtActual.getPresupuesto() + "$ | Jugadores: " + dtActual.getEquipoAsignado().getTitulares().size() + "/11");
        });

        return btn;
    }
}