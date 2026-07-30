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

    // NUEVO: Memoria Caché exclusiva para el Cliente
    public static String ultimoMarcadorCliente = "0 - 0";
    public static String ultimoRelojCliente = "00'";
    public static String ultimaNarracionCliente = "¡Los equipos están en la cancha! Esperando el pitazo inicial...";
    
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

        // 1. COMPONENTES VISUALES Y LECTURA DE MEMORIA
        boolean esClienteOnline = !GestorJuego.getInstance().isEsHost() && GestorJuego.getInstance().getCliente() != null;
        String golesTexto, relojTexto, narradorTexto;

        if (esClienteOnline) {
            // El Cliente saca los datos de la memoria caché visual
            golesTexto = ultimoMarcadorCliente;
            relojTexto = ultimoRelojCliente;
            narradorTexto = ultimaNarracionCliente;
        } else {
            // El Host (o modo offline) lee directamente del Motor matemático
            MotorSimulacion motorInit = GestorJuego.getInstance().getMotorActivo();
            golesTexto = (motorInit != null) ? motorInit.getGolesEquipo1() + " - " + motorInit.getGolesEquipo2() : "0 - 0";
            relojTexto = (motorInit != null) ? motorInit.getMinuto() + "'" : "00'";
            narradorTexto = (motorInit != null && motorInit.getMinuto() > 0) ? "El partido se reanuda desde el medio campo..." : "¡Los equipos están en la cancha! Esperando el pitazo inicial...";
        }

        Text txtEquipo1 = new Text(nombreE1);
        txtEquipo1.setFont(Font.font("Impact", 45));
        txtEquipo1.setFill(Color.WHITE);

        Text txtMarcadorGoles = new Text(golesTexto); // ACTUALIZADO
        txtMarcadorGoles.setFont(Font.font("Impact", 60));
        txtMarcadorGoles.setFill(Color.YELLOW);

        Text txtEquipo2 = new Text(nombreE2);
        txtEquipo2.setFont(Font.font("Impact", 45));
        txtEquipo2.setFill(Color.WHITE);

        HBox marcador = new HBox(30, txtEquipo1, txtMarcadorGoles, txtEquipo2);
        marcador.setAlignment(Pos.CENTER);

        Text txtReloj = new Text(relojTexto); // ACTUALIZADO
        txtReloj.setFont(Font.font("Impact", 36));
        txtReloj.setFill(Color.CYAN);

        Text txtNarrador = new Text(narradorTexto); // ACTUALIZADO
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
        // BORRAMOS la línea "boolean esClienteOnline = ..." que estaba aquí

        if (esClienteOnline) {
           // EL CLIENTE SOLO ESCUCHA (Es un espectador del Host)
            new Thread(() -> {
                try {
                    ObjectInputStream in = GestorJuego.getInstance().getCliente().getIn();
                    while (true) {
                        
                        // 1. ESCUDO ANTI-BASURA: Leemos de forma genérica
                        Object paqueteRecibido = in.readObject();
                        
                        // Si nos llega un paquete del minijuego rezagado por el lag, lo tiramos a la basura
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
                                // LÓGICA NORMAL Y ACTUALIZACIÓN DE CACHÉ
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
                                    
                                    // El Cliente NO guarda estadísticas en el archivo local.
                                    // Dejamos que el Host sea el único administrador de la base de datos
                                    // para evitar registros duplicados al jugar en localhost.
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

               // Intercepción del Minijuego (Host)
                if (motor.isEnMinijuego()) {
                    timerPartido.expire(); 
                    
                    String equipoPeligro = motor.getEquipoAtacante().getNombrePais();
                    txtNarrador.setText("¡PREPÁRENSE PARA EL ATAQUE DE " + equipoPeligro.toUpperCase() + "!");
                    txtNarrador.setFill(Color.RED);
                    
                    // ¿Quién ataca? Identificamos si es el equipo del Host
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
    
    // Este método reconstruye el HUD tras el minijuego
    public static void reanudarDesdeMinijuego() {
        FXGL.getGameScene().addUINode(crearInterfaz());
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