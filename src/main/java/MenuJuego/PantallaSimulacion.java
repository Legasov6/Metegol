package MenuJuego;

import Logica.GestorJuego;
import Logica.MotorSimulacion;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.time.TimerAction;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javax.swing.JOptionPane;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PantallaSimulacion {

    private static TimerAction timerPartido; 

    public static StackPane crearInterfaz() {
        StackPane panelFondo = new StackPane();
        panelFondo.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        panelFondo.setMinSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        panelFondo.setMaxSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        
        try {
            var fondoView = FXGL.texture("FondoSimulacion.png", FXGL.getAppWidth(), FXGL.getAppHeight());
            panelFondo.getChildren().add(fondoView);
        } catch (Exception e) {
            panelFondo.setStyle("-fx-background-color: #111111;");
        }

        Rectangle filtroOscuro = new Rectangle(FXGL.getAppWidth(), FXGL.getAppHeight());
        filtroOscuro.setFill(Color.rgb(0, 0, 0, 0.45)); 
        filtroOscuro.setMouseTransparent(true);
        panelFondo.getChildren().add(filtroOscuro);

        // Nombres en el HUD
        String nombreE1 = GestorJuego.getInstance().getEquipoLocal().getNombrePais();
        String nombreE2 = GestorJuego.getInstance().getEquipoRival().getNombrePais();

        // 1. COMPONENTES VISUALES
        Text txtEquipo1 = new Text(nombreE1);
        txtEquipo1.setFont(Font.font("Impact", 45));
        txtEquipo1.setFill(Color.WHITE);

        Text txtMarcadorGoles = new Text("0 - 0");
        txtMarcadorGoles.setFont(Font.font("Impact", 60));
        txtMarcadorGoles.setFill(Color.YELLOW);

        Text txtEquipo2 = new Text(nombreE2);
        txtEquipo2.setFont(Font.font("Impact", 45));
        txtEquipo2.setFill(Color.WHITE);

        HBox marcador = new HBox(30, txtEquipo1, txtMarcadorGoles, txtEquipo2);
        marcador.setAlignment(Pos.CENTER);

        Text txtReloj = new Text("00'");
        txtReloj.setFont(Font.font("Impact", 36));
        txtReloj.setFill(Color.CYAN);

        Text txtNarrador = new Text("¡Los equipos están en la cancha! Esperando el pitazo inicial...");
        txtNarrador.setFont(Font.font("Consolas", 28));
        txtNarrador.setFill(Color.WHITE);
        txtNarrador.setTextAlignment(TextAlignment.CENTER);

        Button btnVolver = new Button("Volver al Menú");
        btnVolver.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black; -fx-font-size: 20px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnVolver.setPrefSize(250, 50);
        btnVolver.setVisible(false); 

        btnVolver.setOnAction(e -> {
            FXGL.getGameScene().clearUINodes();
            FXGL.getGameScene().addUINode(MenuPrincipal.crearInterfaz()); 
        });

        // OVERLAY
        BorderPane overlay = new BorderPane();
        overlay.setPrefSize(FXGL.getAppWidth(), FXGL.getAppHeight());
        overlay.setPickOnBounds(false);

        HBox marcadorWrapper = new HBox(marcador);
        marcadorWrapper.setAlignment(Pos.CENTER);
        marcadorWrapper.setPadding(new Insets(25, 0, 0, 0));
        overlay.setTop(marcadorWrapper);

        VBox centroWrapper = new VBox(30, txtNarrador, btnVolver);
        centroWrapper.setAlignment(Pos.CENTER);
        overlay.setCenter(centroWrapper);

        HBox relojWrapper = new HBox(txtReloj);
        relojWrapper.setAlignment(Pos.CENTER);
        relojWrapper.setPadding(new Insets(0, 0, 30, 0));
        overlay.setBottom(relojWrapper);

        panelFondo.getChildren().add(overlay);

        // =========================================================
        // BIFURCACIÓN DE LÓGICA: ¿CLIENTE O HOST/OFFLINE?
        // =========================================================
        boolean esClienteOnline = !GestorJuego.getInstance().isEsHost() && GestorJuego.getInstance().getCliente() != null;

        if (esClienteOnline) {
            
            // EL CLIENTE SOLO ESCUCHA (Es un espectador del Host)
            new Thread(() -> {
                try {
                    ObjectInputStream in = GestorJuego.getInstance().getCliente().getIn();
                    while (true) {
                        Entidades.EstadoPartido estado = (Entidades.EstadoPartido) in.readObject();
                        
                        // Platform.runLater obliga a los gráficos a actualizarse de forma segura
                        javafx.application.Platform.runLater(() -> {
                            txtReloj.setText(estado.getMinuto() + "'");
                            
                            // Invertimos los goles porque el Motor del Host tiene al Cliente como "Equipo 2"
                            txtMarcadorGoles.setText(estado.getGolesEquipo2() + " - " + estado.getGolesEquipo1());
                            txtNarrador.setText(estado.getNarracion());
                            
                            if (estado.getNarracion().contains("GOOOL") || estado.getNarracion().contains("FINAL")) {
                                txtNarrador.setFill(Color.YELLOW);
                            } else {
                                txtNarrador.setFill(Color.WHITE);
                            }
                            
                            if (estado.isFinDePartido()) {
                                btnVolver.setVisible(true);
                                
                                // Guardado de estadísticas para el Cliente
                                if (estado.getGolesEquipo2() > estado.getGolesEquipo1()) { // Si el Cliente ganó
                                    String marcadorFinal = estado.getGolesEquipo2() + " - " + estado.getGolesEquipo1();
                                    String nombreDT = GestorJuego.getInstance().getDtLocal().getNombreDT();
                                    Logica.GestorEstadisticas.guardarCampeon(new Entidades.RegistroCampeon(nombreDT, nombreE1, marcadorFinal));
                                } else if (estado.getGolesEquipo1() > estado.getGolesEquipo2()) { // Si el Host ganó
                                    String marcadorFinal = estado.getGolesEquipo1() + " - " + estado.getGolesEquipo2();
                                    String nombreGanador = GestorJuego.getInstance().getDtRival().getNombreDT();
                                    Logica.GestorEstadisticas.guardarCampeon(new Entidades.RegistroCampeon(nombreGanador, nombreE2, marcadorFinal));
                                }
                            }
                        });
                        
                        if (estado.isFinDePartido()) break; // Apaga el hilo si terminó el juego
                    }
                } catch (Exception ex) {
                    System.err.println("Error escuchando al Host: " + ex.getMessage());
                }
            }).start();

        } else {
            
            // EL HOST O MODO OFFLINE (EL ÁRBITRO DEL JUEGO)
            MotorSimulacion motor = GestorJuego.getInstance().getMotorActivo();
            
            timerPartido = FXGL.getGameTimer().runAtInterval(() -> {
                
                if (motor.getMinuto() >= 90) {
                    txtReloj.setText("90'");
                    txtNarrador.setText("¡FINAL DEL PARTIDO!\n" + nombreE1 + " " + motor.getGolesEquipo1() + " - " + motor.getGolesEquipo2() + " " + nombreE2);
                    txtNarrador.setFill(Color.YELLOW);
                    btnVolver.setVisible(true);
                    timerPartido.expire();
                    
                    // Guardado de estadísticas para el Host
                    if (motor.getGolesEquipo1() > motor.getGolesEquipo2()) {
                        String marcadorFinal = motor.getGolesEquipo1() + " - " + motor.getGolesEquipo2();
                        String nombreRealDT = GestorJuego.getInstance().getDtLocal().getNombreDT(); 
                        Logica.GestorEstadisticas.guardarCampeon(new Entidades.RegistroCampeon(nombreRealDT, nombreE1, marcadorFinal));
                        
                    } else if (motor.getGolesEquipo2() > motor.getGolesEquipo1()) {
                        String marcadorFinal = motor.getGolesEquipo2() + " - " + motor.getGolesEquipo1();
                        
                        // Extraemos el nombre del rival humano, o ponemos "CPU" si estamos offline
                        String nombreGanador = "CPU (Bot)"; 
                        if (GestorJuego.getInstance().getDtRival() != null) {
                            nombreGanador = GestorJuego.getInstance().getDtRival().getNombreDT();
                        }
                        
                        Logica.GestorEstadisticas.guardarCampeon(new Entidades.RegistroCampeon(nombreGanador, nombreE2, marcadorFinal));
                    }
                    
                    transmitirEstadoAlCliente(motor, txtNarrador.getText(), true);
                    return;
                }

                // Intercepción del Minijuego (Por ahora solo interactúa el Host localmente)
                if (motor.isEnMinijuego()) {
                    timerPartido.pause(); 
                    String equipoPeligro = motor.getEquipoAtacante().getNombrePais();
                    txtNarrador.setText("¡PREPÁRENSE PARA EL ATAQUE DE " + equipoPeligro.toUpperCase() + "!");
                    txtNarrador.setFill(Color.RED);
                    
                    transmitirEstadoAlCliente(motor, txtNarrador.getText(), false);

                    FXGL.runOnce(() -> {
                        String[] opciones = {"¡GOLAZO!", "Fallé / La atajaron"};
                        int respuesta = JOptionPane.showOptionDialog(null, 
                                "Jugada clave para " + equipoPeligro + "...\n¿Termina en gol?", 
                                "Mock Minijuego", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);

                        boolean huboGol = (respuesta == 0);
                        if (huboGol) {
                            motor.registrarGol(motor.getEquipoAtacante());
                            txtMarcadorGoles.setText(motor.getGolesEquipo1() + " - " + motor.getGolesEquipo2());
                            txtNarrador.setText("¡GOOOOOOOOOOL DE " + equipoPeligro.toUpperCase() + "!");
                            txtNarrador.setFill(Color.LIGHTGREEN);
                        } else {
                            txtNarrador.setText("¡Ocasión desperdiciada por " + equipoPeligro + "!");
                            txtNarrador.setFill(Color.ORANGE);
                        }

                        motor.finalizarMinijuego(2, huboGol);
                        transmitirEstadoAlCliente(motor, txtNarrador.getText(), false);
                        timerPartido.resume(); 
                    }, Duration.seconds(1.0)); 
                    
                    return;
                }

                // Minuto normal
                String narracionDelMinuto = motor.simularMinuto();
                txtReloj.setText(motor.getMinuto() + "'");
                
                if (!narracionDelMinuto.isEmpty()) {
                    txtNarrador.setText(narracionDelMinuto);
                    txtNarrador.setFill(Color.WHITE); 
                }
                
                transmitirEstadoAlCliente(motor, txtNarrador.getText(), false);

            }, Duration.seconds(1.5)); 
        }

        return panelFondo;
    }

    // Método auxiliar para no repetir código de red
    private static void transmitirEstadoAlCliente(MotorSimulacion motor, String texto, boolean esFin) {
        if (GestorJuego.getInstance().isEsHost() && GestorJuego.getInstance().getServidor() != null) {
            try {
                Entidades.EstadoPartido estadoActual = new Entidades.EstadoPartido(
                    motor.getMinuto(), 
                    motor.getGolesEquipo1(), 
                    motor.getGolesEquipo2(), 
                    texto, 
                    esFin
                );
                ObjectOutputStream out = GestorJuego.getInstance().getServidor().getOut();
                out.writeObject(estadoActual);
                out.reset(); // VITAL: Resetea el caché del canal para enviar datos frescos
            } catch (Exception ex) {
                System.err.println("Error transmitiendo estado: " + ex.getMessage());
            }
        }
    }
}