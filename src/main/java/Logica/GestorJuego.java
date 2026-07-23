package Logica;

import Entidades.Defensor;
import Entidades.Delantero;
import Entidades.Portero;
import Entidades.Equipo;
import Entidades.Mediocampista;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class GestorJuego {

    // 1. LA INSTANCIA ÚNICA
    private static GestorJuego instance;

    // 2. ATRIBUTOS GLOBALES
    private Entidades.Jugador dtLocal; 
    private Entidades.Jugador dtRival; // <-- NUEVO: Para guardar al manager enemigo
    private Equipo equipoLocal;
    private Equipo equipoRival;
    private MotorSimulacion motorActivo;
    
    // Variables de red
    private boolean esHost;
    private ServidorLocal servidor;
    private ClienteRed cliente;

    private GestorJuego() {}

    public static GestorJuego getInstance() {
        if (instance == null) {
            instance = new GestorJuego();
        }
        return instance;
    }

    // ==========================================================
    // GETTERS Y SETTERS
    // ==========================================================
    public void setEquipoLocal(Equipo equipoLocal) { this.equipoLocal = equipoLocal; }
    public Equipo getEquipoLocal() { return equipoLocal; }

    public void setEquipoRival(Equipo equipoRival) { this.equipoRival = equipoRival; }
    public Equipo getEquipoRival() { return equipoRival; }

    public MotorSimulacion getMotorActivo() { return motorActivo; }
    
    public Entidades.Jugador getDtLocal() { return dtLocal; }
    public void setDtLocal(Entidades.Jugador dtLocal) { this.dtLocal = dtLocal; }

    public Entidades.Jugador getDtRival() { return dtRival; }
    public void setDtRival(Entidades.Jugador dtRival) { this.dtRival = dtRival; }

    public boolean isEsHost() { return esHost; }
    public void setEsHost(boolean esHost) { this.esHost = esHost; }

    public ServidorLocal getServidor() { return servidor; }
    public void setServidor(ServidorLocal servidor) { this.servidor = servidor; }

    public ClienteRed getCliente() { return cliente; }
    public void setCliente(ClienteRed cliente) { this.cliente = cliente; }

    // ==========================================================
    // LÓGICA DE PARTIDO LOCAL (Offline - Se mantiene intacta)
    // ==========================================================
    public void iniciarPartidoContraBot() {
        if (this.equipoLocal == null) {
            System.out.println("Error: No puedes iniciar el partido, el equipo local está vacío.");
            return;
        }
        System.out.println("\n[GestorJuego] Preparando partido offline...");
        generarRivalBot();
        this.motorActivo = new MotorSimulacion(this.equipoLocal, this.equipoRival);
    }

    private void generarRivalBot() {
        this.equipoRival = new Entidades.Equipo("Bot FC (CPU)");
        this.equipoRival.agregarFutbolista(new Entidades.Portero("Bot Arquero", "CPU", 2, 1, 2, 4, 0, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Defensor("Bot Central 1", "CPU", 2, 1, 2, 5, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Defensor("Bot Central 2", "CPU", 3, 2, 2, 4, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Defensor("Bot Lateral 1", "CPU", 4, 2, 3, 3, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Defensor("Bot Lateral 2", "CPU", 4, 1, 3, 3, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Mediocampista("Bot Pivote", "CPU", 3, 2, 4, 4, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Mediocampista("Bot Interior 1", "CPU", 4, 3, 5, 2, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Mediocampista("Bot Interior 2", "CPU", 3, 4, 4, 3, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Mediocampista("Bot Extremo", "CPU", 5, 3, 4, 1, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Delantero("Bot Ariete", "CPU", 4, 5, 2, 1, 0));
        this.equipoRival.agregarFutbolista(new Entidades.Delantero("Bot Falso 9", "CPU", 4, 4, 4, 1, 0));
        System.out.println("[GestorJuego] Equipo rival generado con éxito.");
    }

    // ==========================================================
    // LÓGICA DE PARTIDO ONLINE (NUEVO)
    // ==========================================================
    public void intercambiarPlantillasOnline(Runnable alTerminar) {
        // Envolvemos esto en un Thread para que, si el otro jugador tarda
        // en armar su plantilla, nuestro juego no se quede "congelado" esperando.
        new Thread(() -> {
            try {
                ObjectOutputStream out;
                ObjectInputStream in;

                // Determinamos de dónde sacar los cables dependiendo de si somos Host o Cliente
                if (esHost) {
                    out = servidor.getOut();
                    in = servidor.getIn();
                } else {
                    out = cliente.getOut();
                    in = cliente.getIn();
                }

                System.out.println("[RED] Enviando plantilla local al rival...");
                // 1. Enviamos nuestro objeto Jugador entero
                out.writeObject(this.dtLocal);
                out.flush();
                
                System.out.println("[RED] Esperando a que el rival envíe su plantilla...");
                // 2. Nos pausamos aquí hasta recibir el del rival
                Entidades.Jugador rivalRecibido = (Entidades.Jugador) in.readObject();
                
                // 3. Guardamos los datos recibidos
                this.dtRival = rivalRecibido;
                this.equipoRival = rivalRecibido.getEquipoAsignado();
                System.out.println("[RED] ¡Plantilla rival recibida con éxito! DT Rival: " + this.dtRival.getNombreDT());

                // 4. Instanciamos el motor real con ambos equipos humanos
                this.motorActivo = new MotorSimulacion(this.equipoLocal, this.equipoRival);

                // 5. Disparamos el cambio de pantalla en JavaFX
                javafx.application.Platform.runLater(alTerminar);

            } catch (Exception e) {
                System.err.println("[RED] Error al intercambiar plantillas: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}