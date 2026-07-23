package Logica;

import Entidades.Equipo;
import java.util.Random;

public class MotorSimulacion {

    public enum EstadoPartido {
        NEUTRAL,
        DOMINIO,
        CONTRAATAQUE
    }

    // ATRIBUTOS DEL MOTOR MATEMÁTICO
    private Equipo equipo1;
    private Equipo equipo2;
    private EstadoPartido estadoActual;
    private Equipo equipoAtacante; 
    private Equipo equipoDefensor;
    private int minuto;
    private Random dadoVirtual;

    // NUEVOS ATRIBUTOS: ÁRBITRO Y CONTROL
    private int golesEquipo1;
    private int golesEquipo2;
    private boolean enMinijuego; // Actúa como el "freno de mano"

    public MotorSimulacion(Equipo equipo1, Equipo equipo2) {
        this.equipo1 = equipo1;
        this.equipo2 = equipo2;
        this.minuto = 0;
        this.estadoActual = EstadoPartido.NEUTRAL;
        this.equipoAtacante = null; 
        this.equipoDefensor = null;
        this.dadoVirtual = new Random();
        
        // Inicializamos el marcador y el estado de control
        this.golesEquipo1 = 0;
        this.golesEquipo2 = 0;
        this.enMinijuego = false;
    }

    // ========================================================================
    // BUCLE PRINCIPAL (Ahora devuelve un String para el Narrador Visual)
    // ========================================================================
    public String simularMinuto() {
        if (minuto >= 90) {
            return "¡Pitazo final! Marcador: " + equipo1.getNombrePais() + " [" + golesEquipo1 + " - " + golesEquipo2 + "] " + equipo2.getNombrePais();
        }

        // Si estamos en la pantalla del minijuego, el bucle no avanza el tiempo
        if (enMinijuego) {
            return ""; 
        }

        minuto++;
        
        StringBuilder narracion = new StringBuilder();
        narracion.append("Minuto ").append(minuto).append(" [").append(estadoActual).append("]\n");

        switch (estadoActual) {
            case NEUTRAL:
                narracion.append(resolverNeutral());
                break;
            case DOMINIO:
                narracion.append(resolverDominio());
                break;
            case CONTRAATAQUE:
                narracion.append(resolverContraataque());
                break;
        }
        
        return narracion.toString();
    }

    // ========================================================================
    // PUENTES DE COMUNICACIÓN CON LA INTERFAZ FXGL
    // ========================================================================
    
    // Este método lo llamará FXGL cuando el balón cruce la línea de meta
    public void registrarGol(Equipo equipoAnotador) {
        if (equipoAnotador == equipo1) {
            golesEquipo1++;
        } else if (equipoAnotador == equipo2) {
            golesEquipo2++;
        }
        // Se pueden dejar los println aquí como logs de consola para ti (el desarrollador)
        System.out.println("¡GOOOOOOOOOOL DE " + equipoAnotador.getNombrePais() + "!");
        System.out.println("Marcador actualizado: " + equipo1.getNombrePais() + " " + golesEquipo1 + " - " + golesEquipo2 + " " + equipo2.getNombrePais());
    }

    // Este método lo llamará FXGL cuando el minijuego termine (ya sea por gol, atajada o balón fuera)
    public void finalizarMinijuego(int minutosConsumidos, boolean huboGol) {
        System.out.println("-> Devolviendo el control al motor. El tiempo avanza " + minutosConsumidos + " minutos reales.");
        this.minuto += minutosConsumidos; 
        this.enMinijuego = false; // Quitamos el freno de mano
        
        if (huboGol) {
            // Si metieron gol, sacan del medio. Todo se reinicia a Neutral.
            this.estadoActual = EstadoPartido.NEUTRAL;
            this.equipoAtacante = null;
            this.equipoDefensor = null;
        } else {
            // LÓGICA DE CASTIGO SI FALLAN EL GOL
            System.out.println("-> La jugada no terminó en gol...");
            
            if (this.estadoActual == EstadoPartido.DOMINIO) {
                // Si falló un ataque de Dominio, se viene la contra
                System.out.println("-> ¡El equipo rival sale al Contraataque!");
                this.estadoActual = EstadoPartido.CONTRAATAQUE;
                Equipo temp = this.equipoAtacante;
                this.equipoAtacante = this.equipoDefensor;
                this.equipoDefensor = temp;
                
            } else if (this.estadoActual == EstadoPartido.CONTRAATAQUE) {
                // Si falló en un contraataque, la jugada simplemente se diluye
                System.out.println("-> La defensa logra despejar y reordenarse. Balón al centro.");
                this.estadoActual = EstadoPartido.NEUTRAL;
                this.equipoAtacante = null;
                this.equipoDefensor = null;
            }
        }
    }

    // ========================================================================
    // RESOLUCIÓN DE ESTADOS MATEMÁTICOS (Devolviendo textos)
    // ========================================================================
    private String resolverNeutral() {
        StringBuilder sb = new StringBuilder();
        sb.append("Balón dividido en el mediocampo entre ").append(equipo1.getNombrePais()).append(" y ").append(equipo2.getNombrePais()).append("...\n");
        
        int poderEq1 = (equipo1.getPaseMediocampistas() + equipo1.getVelocidadMediocampistas() + equipo1.getDefensaMediocampistas()) + equipo1.getPaseDelanteros();
        int poderEq2 = (equipo2.getPaseMediocampistas() + equipo2.getVelocidadMediocampistas() + equipo2.getDefensaMediocampistas()) + equipo2.getPaseDelanteros();
        
        if (poderEq1 <= 0) poderEq1 = 1; 
        if (poderEq2 <= 0) poderEq2 = 1;

        int pozoTotal = poderEq1 + poderEq2;
        int tirada = dadoVirtual.nextInt(pozoTotal) + 1;
        
        if (tirada <= poderEq1) {
            sb.append("-> ¡").append(equipo1.getNombrePais()).append(" gana la lucha física y controla el balón!");
            this.equipoAtacante = equipo1;
            this.equipoDefensor = equipo2;
        } else {
            sb.append("-> ¡").append(equipo2.getNombrePais()).append(" gana la lucha física y controla el balón!");
            this.equipoAtacante = equipo2;
            this.equipoDefensor = equipo1;
        }
        this.estadoActual = EstadoPartido.DOMINIO;
        
        return sb.toString();
    }

    private String resolverDominio() {
        StringBuilder sb = new StringBuilder();
        sb.append(equipoAtacante.getNombrePais()).append(" arma la jugada contra la defensa de ").append(equipoDefensor.getNombrePais()).append("...\n");
        
        int poderAtacante = (equipoAtacante.getVelocidadMediocampistas() + equipoAtacante.getPaseMediocampistas()) + (equipoAtacante.getVelocidadDelanteros() + equipoAtacante.getPaseDelanteros());
        int poderDefensor = (equipoDefensor.getVelocidadMediocampistas() + equipoDefensor.getDefensaMediocampistas()) + (equipoDefensor.getVelocidadDefensores() + equipoDefensor.getDefensaDefensores());

        if (poderAtacante <= 0) poderAtacante = 1;
        if (poderDefensor <= 0) poderDefensor = 1;

        int pozoTotal = poderAtacante + poderDefensor;
        int tirada = dadoVirtual.nextInt(pozoTotal) + 1;

        if (tirada <= poderAtacante) {
            sb.append("-> ¡Pase filtrado con éxito! ").append(equipoAtacante.getNombrePais()).append(" rompe la línea.\n");
            sb.append("-> [PAUSA DEL MOTOR] Abriendo interfaz de Minijuego...");
            this.enMinijuego = true; // Aplicamos el freno de mano
            
        } else {
            sb.append("-> La defensa de ").append(equipoDefensor.getNombrePais()).append(" corta el pase.\n");
            int checkRobo = dadoVirtual.nextInt(100) + 1; 
            
            if (checkRobo <= 20) {
                sb.append("-> ¡Robo quirúrgico! Peligro de contragolpe.");
                this.estadoActual = EstadoPartido.CONTRAATAQUE;
                Equipo temp = this.equipoAtacante;
                this.equipoAtacante = this.equipoDefensor;
                this.equipoDefensor = temp;
            } else {
                sb.append("-> Balón reventado hacia el mediocampo.");
                this.estadoActual = EstadoPartido.NEUTRAL;
                this.equipoAtacante = null;
                this.equipoDefensor = null;
            }
        }
        
        return sb.toString();
    }

    private String resolverContraataque() {
        StringBuilder sb = new StringBuilder();
        sb.append("¡").append(equipoAtacante.getNombrePais()).append(" sale a toda velocidad al contragolpe!\n");
        
        int poderAtacante = (equipoAtacante.getVelocidadDelanteros() + equipoAtacante.getPaseDelanteros()) + (equipoAtacante.getVelocidadMediocampistas() + equipoAtacante.getPaseMediocampistas()) + equipoAtacante.getPaseDefensores();
        int poderDefensorBruto = (equipoDefensor.getVelocidadMediocampistas() + equipoDefensor.getDefensaMediocampistas()) + (equipoDefensor.getVelocidadDefensores() + equipoDefensor.getDefensaDefensores() + equipoDefensor.getPaseDefensores());
                               
        double modificadorDebuff = 0.6; 
        int poderDefensor = (int) (poderDefensorBruto * modificadorDebuff);

        if (poderAtacante <= 0) poderAtacante = 1;
        if (poderDefensor <= 0) poderDefensor = 1;

        int pozoTotal = poderAtacante + poderDefensor;
        int tirada = dadoVirtual.nextInt(pozoTotal) + 1;

        if (tirada <= poderAtacante) {
            sb.append("-> ¡La contra es letal! Llegan al área con superioridad numérica.\n");
            sb.append("-> [PAUSA DEL MOTOR] Abriendo interfaz de Minijuego...");
            this.enMinijuego = true; // Aplicamos el freno de mano
            
        } else {
            sb.append("-> ¡Milagro defensivo! La defensa logra replegarse.");
            this.estadoActual = EstadoPartido.NEUTRAL;
            this.equipoAtacante = null;
            this.equipoDefensor = null;
        }
        
        return sb.toString();
    }

    // Getters
    public int getMinuto() { return minuto; }
    public EstadoPartido getEstadoActual() { return estadoActual; }
    public boolean isEnMinijuego() { return enMinijuego; }
    public int getGolesEquipo1() { return golesEquipo1; }
    public int getGolesEquipo2() { return golesEquipo2; }
    public Equipo getEquipoAtacante() { return equipoAtacante; }
}