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

/**
 * Muestra la información del partido  durante el transcurso matemático 
 * del encuentro. Actúa de puente entre el motor del juego y la pantalla del 
 * jugador, orquestando las interrupciones para lanzar el minijuego 2D.
 * @author FrankFarias
 */
public class PantallaSimulacion {

    private static TimerAction timerPartido; 

    // Memoria caché exclusiva para el Cliente
    public static String ultimoMarcadorCliente = "0 - 0";
    public static String ultimoRelojCliente = "00'";
    public static String ultimaNarracionCliente = "¡Los equipos están en la cancha! Esperando el pitazo inicial...";
    
    /**
     * Si la computadora es Host, ejecuta el bucle iterativo de 1.5s que lee del
     * MotorSimulacion. Si es Cliente, inicia un hilo pasivo que escucha y 
     * actualiza el texto según los paquetes recibidos en el Socket.
     * @return 
     */
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

        // Nombres en la interfaz
        String nombreE1 = GestorJuego.getInstance().getEquipoLocal().getNombrePais();
        String nombreE2 = GestorJuego.getInstance().getEquipoRival().getNombrePais();

        // Componentes visuales y lectura de memoria
        boolean esClienteOnline = !GestorJuego.getInstance().isEsHost() && GestorJuego.getInstance().getCliente() != null;
        String golesTexto, relojTexto, narradorTexto;

        if (esClienteOnline) {
            // El cliente saca los datos de la memoria caché visual
            golesTexto = ultimoMarcadorCliente;
            relojTexto = ultimoRelojCliente;
            narradorTexto = ultimaNarracionCliente;
        } else {
            // El Host lee directamente del motorSimulacion
            MotorSimulacion motorInit = GestorJuego.getInstance().getMotorActivo();
            golesTexto = (motorInit != null) ? motorInit.getGolesEquipo1() + " - " + motorInit.getGolesEquipo2() : "0 - 0";
            relojTexto = (motorInit != null) ? motorInit.getMinuto() + "'" : "00'";
            narradorTexto = (motorInit != null && motorInit.getMinuto() > 0) ? "El partido se reanuda desde el medio campo..." : "¡Los equipos están en la cancha! Esperando el pitazo inicial...";
        }

        Text txtEquipo1 = new Text(nombreE1);
        txtEquipo1.setFont(Font.font("Impact", 45));
        txtEquipo1.setFill(Color.WHITE);

        Text txtMarcadorGoles = new Text(golesTexto);
        txtMarcadorGoles.setFont(Font.font("Impact", 60));
        txtMarcadorGoles.setFill(Color.YELLOW);

        Text txtEquipo2 = new Text(nombreE2);
        txtEquipo2.setFont(Font.font("Impact", 45));
        txtEquipo2.setFill(Color.WHITE);

        HBox marcador = new HBox(30, txtEquipo1, txtMarcadorGoles, txtEquipo2);
        marcador.setAlignment(Pos.CENTER);

        Text txtReloj = new Text(relojTexto);
        txtReloj.setFont(Font.font("Impact", 36));
        txtReloj.setFill(Color.CYAN);

        Text txtNarrador = new Text(narradorTexto); 
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

        // El overlay (las cajitas encima del fondo)
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

        // Si cliente online
        if (esClienteOnline) {
           // El cliente solo hace de listener
            new Thread(() -> {
                try {
                    ObjectInputStream in = GestorJuego.getInstance().getCliente().getIn();
                    while (true) {
                        
                        // Cliente solo lee
                        Object paqueteRecibido = in.readObject();
                        
                        // Si un paquete vino con retraso, a la basura
                        if (!(paqueteRecibido instanceof Entidades.EstadoPartido)) {
                            continue;
                        }
                        
                        Entidades.EstadoPartido estado = (Entidades.EstadoPartido) paqueteRecibido;
                        
                        javafx.application.Platform.runLater(() -> {
                            if (estado.getNarracion().startsWith(">>>INICIAR_MINIJUEGO")) {
                                txtNarrador.setText("¡ENTRANDO AL MINIJUEGO!");
                                txtNarrador.setFill(Color.RED);
                                
                                boolean hostAtaca = estado.getNarracion().contains("HOST");
                                boolean clienteAtaca = !hostAtaca; 
                                
                                FXGL.getGameTimer().runOnceAfter(() -> {
                                    FXGL.getGameScene().clearUINodes();
                                    Minijuego.EscenaMinijuego.iniciarMinijuego(clienteAtaca);
                                }, Duration.seconds(2.0));
                                
                            } else {
                                // Lógica normal
                                txtReloj.setText(estado.getMinuto() + "'");
                                txtMarcadorGoles.setText(estado.getGolesEquipo2() + " - " + estado.getGolesEquipo1());
                                txtNarrador.setText(estado.getNarracion());
                                
                                // Guardamos en memoria por si el juego cambia a la pantalla de Minijuego y vuelve
                                ultimoRelojCliente = txtReloj.getText();
                                ultimoMarcadorCliente = txtMarcadorGoles.getText();
                                ultimaNarracionCliente = txtNarrador.getText();
                                
                                if (estado.getNarracion().contains("GOOOL") || estado.getNarracion().contains("FINAL")) {
                                    txtNarrador.setFill(Color.YELLOW);
                                } else {
                                    txtNarrador.setFill(Color.WHITE);
                                }
                                
                                if (estado.isFinDePartido()) {
                                    btnVolver.setVisible(true);
                                    
                                }
                            }
                        });
                        
                        if (estado.isFinDePartido()) break;
                        if (estado.getNarracion().startsWith(">>>INICIAR_MINIJUEGO")) break;
                    }
                } catch (Exception ex) {
                    System.err.println("Error escuchando al Host en Simulación: " + ex.getMessage());
                }
            }).start();

        } else {
            
            // El host es quien dicta lo que muestra la interfaz
            MotorSimulacion motor = GestorJuego.getInstance().getMotorActivo();
            
            timerPartido = FXGL.getGameTimer().runAtInterval(() -> {
                
                if (motor.getMinuto() >= 90) {
                    txtReloj.setText("90'");
                    txtNarrador.setText("¡FINAL DEL PARTIDO!\n" + nombreE1 + " " + motor.getGolesEquipo1() + " - " + motor.getGolesEquipo2() + " " + nombreE2);
                    txtNarrador.setFill(Color.YELLOW);
                    btnVolver.setVisible(true);
                    timerPartido.expire();
                    
                    // Guardado de estadísticas para el host
                    if (motor.getGolesEquipo1() > motor.getGolesEquipo2()) {
                        String marcadorFinal = motor.getGolesEquipo1() + " - " + motor.getGolesEquipo2();
                        String nombreRealDT = GestorJuego.getInstance().getDtLocal().getNombreDT(); 
                        Logica.GestorEstadisticas.guardarCampeon(new Entidades.RegistroCampeon(nombreRealDT, nombreE1, marcadorFinal));
                        
                    } else if (motor.getGolesEquipo2() > motor.getGolesEquipo1()) {
                        String marcadorFinal = motor.getGolesEquipo2() + " - " + motor.getGolesEquipo1();
                        
                        // Conseguir el nombre del rival humano, o pone "CPU" si es offline
                        String nombreGanador = "CPU (Bot)"; 
                        if (GestorJuego.getInstance().getDtRival() != null) {
                            nombreGanador = GestorJuego.getInstance().getDtRival().getNombreDT();
                        }
                        
                        Logica.GestorEstadisticas.guardarCampeon(new Entidades.RegistroCampeon(nombreGanador, nombreE2, marcadorFinal));
                    }
                    
                    transmitirEstadoAlCliente(motor, txtNarrador.getText(), true);
                    return;
                }

               // El momento de pausa antes de activar el minijuego
                if (motor.isEnMinijuego()) {
                    timerPartido.expire(); 
                    
                    String equipoPeligro = motor.getEquipoAtacante().getNombrePais();
                    txtNarrador.setText("¡PREPÁRENSE PARA EL ATAQUE DE " + equipoPeligro.toUpperCase() + "!");
                    txtNarrador.setFill(Color.RED);
                    
                    // Quien ataca
                    boolean hostAtaca = (motor.getEquipoAtacante() == Logica.GestorJuego.getInstance().getEquipoLocal());
                    
                    // Enviamos la señal específica por red
                    String senal = hostAtaca ? ">>>INICIAR_MINIJUEGO_HOST<<<" : ">>>INICIAR_MINIJUEGO_RIVAL<<<";
                    transmitirEstadoAlCliente(motor, senal, false);

                    FXGL.getGameTimer().runOnceAfter(() -> {
                        FXGL.getGameScene().clearUINodes();
                        Minijuego.EscenaMinijuego.iniciarMinijuego(hostAtaca);
                    }, Duration.seconds(2.0)); 
                    
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
    
    /**
     * Vuelve a dibujar el estadio y los marcadores tras finalizar el minijuego 
     * 2D.
     */
    public static void reanudarDesdeMinijuego() {
        FXGL.getGameScene().addUINode(crearInterfaz());
    }
    
    /**
     * Empaqueta el estado actual de la simulación en un Data Transfer Object (DTO) 
     * y lo transmite a través de la red mediante Sockets TCP. Este método solo 
     * se ejecuta si la computadora local está actuando como Servidor (Host). Además, aplica un 
     * reset() al canal de salida para evitar que Java serialice datos en caché obsoletos.
     * @param motor La instancia actual del motor matemático, utilizada para extraer 
     * el minuto actual y el marcador de goles.
     * @param texto La narración del evento o jugada que se mostrará en el HUD del Cliente.
     * @param esFin Bandera booleana que indica al Cliente si el partido ha finalizado.
     */
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
                out.reset(); // Resetea el caché del canal para enviar datos frescos
            } catch (Exception ex) {
                System.err.println("Error transmitiendo estado: " + ex.getMessage());
            }
        }
    }
}