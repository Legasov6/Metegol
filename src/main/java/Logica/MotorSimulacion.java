package Logica;

import Entidades.Equipo;
import java.util.Random;

/**
 * Motor matemático que procesa la lógica del partido de forma agnóstica a la interfaz.
 * Utiliza una máquina de estados para calcular la posesión, dominio y goles 
 * basándose en la suma de estadísticas de los futbolistas y probabilidad. 
 * @author Gabriel Tremaria
 */
public class MotorSimulacion {

    public enum EstadoPartido {
        NEUTRAL,
        DOMINIO,
        CONTRAATAQUE
    }

    // Atributos del motor matemático
    private Equipo equipo1;
    private Equipo equipo2;
    private EstadoPartido estadoActual;
    private Equipo equipoAtacante; 
    private Equipo equipoDefensor;
    private int minuto;
    private Random dadoVirtual;

    // Variables árbitro
    private int golesEquipo1;
    private int golesEquipo2;
    private boolean enMinijuego; // Actúa como el "freno de mano"
    /**
     * Inicializa el motor matemático del partido estableciendo las estadísticas 
     * en cero y colocando el estado inicial en NEUTRAL. 
     *
     * @param equipo1 El primer equipo que participará en el encuentro (Local).
     * @param equipo2 El segundo equipo que participará en el encuentro (Visitante).
     */
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

    /**
     * Avanza el reloj del partido en un minuto y evalúa el estado actual de la posesión.
     * Dependiendo de si el estado es NEUTRAL, DOMINIO o CONTRAATAQUE, ejecuta 
     * los cálculos matemáticos correspondientes. Si hay una jugada de peligro, 
     * el bucle se detiene temporalmente (enMinijuego = true).
     *
     * @return Un String con la narración cronológica del evento ocurrido en este minuto.
     */
    public String simularMinuto() {
        //Cuando llega al minuto 90 acaba el partido
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

    /**
     * Actualiza el marcador oficial del partido sumando un tanto.
     * Este método está diseñado para ser invocado desde la EscenaMinijuego 
     * cuando el hitbox del balón cruza físicamente la línea de meta.
     *
     * @param equipoAnotador El equipo que acaba de marcar el gol en el entorno 2D.
     */
    public void registrarGol(Equipo equipoAnotador) {
        if (equipoAnotador == equipo1) {
            golesEquipo1++;
        } else if (equipoAnotador == equipo2) {
            golesEquipo2++;
        }
        System.out.println("¡GOOOOOOOOOOL DE " + equipoAnotador.getNombrePais() + "!");
        System.out.println("Marcador actualizado: " + equipo1.getNombrePais() + " " + golesEquipo1 + " - " + golesEquipo2 + " " + equipo2.getNombrePais());
    }

    /**
     * Devuelve el control al motor matemático tras finalizar una secuencia de físicas en Box2D.
     * Adelanta el reloj según el tiempo gastado en la jugada y recalcula el estado 
     * del partido (saque de centro si hubo gol, o contraataque si la jugada falló).
     *
     * @param minutosConsumidos La cantidad de minutos simulados que duró la jugada 2D.
     * @param huboGol Indica si la jugada terminó en anotación (true) o si fue fallada/atajada (false).
     */
    public void finalizarMinijuego(int minutosConsumidos, boolean huboGol) {
        System.out.println("-> Devolviendo el control al motor. El tiempo avanza " + minutosConsumidos + " minutos reales.");
        this.minuto += minutosConsumidos; 
        this.enMinijuego = false; 
        if (huboGol) {
            // Si metieron gol, sacan del medio. Todo se reinicia a Neutral.
            this.estadoActual = EstadoPartido.NEUTRAL;
            this.equipoAtacante = null;
            this.equipoDefensor = null;
        } else {
            // Si el equipo atacante falla el gol, habrá contraataque
            System.out.println("-> La jugada no terminó en gol...");
            
            if (this.estadoActual == EstadoPartido.DOMINIO) {
                // Si falló un check de Dominio, se viene la contra
                System.out.println("-> ¡El equipo rival sale al Contraataque!");
                this.estadoActual = EstadoPartido.CONTRAATAQUE;
                Equipo temp = this.equipoAtacante;
                this.equipoAtacante = this.equipoDefensor;
                this.equipoDefensor = temp;
                
            } else if (this.estadoActual == EstadoPartido.CONTRAATAQUE) {
                // Si falló en un contraataque, el partido se calma y volvemos a neutral
                System.out.println("-> La defensa logra despejar y reordenarse. Balón al centro.");
                this.estadoActual = EstadoPartido.NEUTRAL;
                this.equipoAtacante = null;
                this.equipoDefensor = null;
            }
        }
    }


    // Cálculos matemáticos
    /**
     * Resuelve una disputa por el balón suelto en el mediocampo. Suma los
     * atributos de pase, velocidad y defensa de ambos equipos para calcular un
     * pozo de probabilidades y decide quién toma la posesión mediante un dado
     * virtual.
     *
     * @return Un String con la narración de quién ganó la posesión.
     */
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
    
    /**
     * Calcula la probabilidad de éxito de una jugada ofensiva estándar.
     * Compara el poder de ataque contra el poder de defensa rival. Puede desencadenar 
     * una transición al minijuego 2D por pase filtrado, o un robo de balón.
     *
     * @return Un String con la narración del resultado del ataque.
     */
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
            sb.append("-> ¡Pase filtrado! ").append(equipoAtacante.getNombrePais()).append(" rompe la línea.\n");
            sb.append("-> Abriendo interfaz de Minijuego...");
            this.enMinijuego = true; 
            
        } else {
            sb.append("-> La defensa de ").append(equipoDefensor.getNombrePais()).append(" corta el pase.\n");
            int checkRobo = dadoVirtual.nextInt(100) + 1; 
            
            if (checkRobo <= 20) {
                sb.append("-> ¡Robo de balón! Peligro de contragolpe.");
                this.estadoActual = EstadoPartido.CONTRAATAQUE;
                Equipo temp = this.equipoAtacante;
                this.equipoAtacante = this.equipoDefensor;
                this.equipoDefensor = temp;
            } else {
                sb.append("-> Balón despejado hacia el mediocampo.");
                this.estadoActual = EstadoPartido.NEUTRAL;
                this.equipoAtacante = null;
                this.equipoDefensor = null;
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Ejecuta los cálculos para una jugada rápida en transición ofensiva.
     * El equipo defensor recibe un debuff temporal (penalización) en sus atributos 
     * por estar descolocado. Si la contra tiene éxito, se abre el minijuego 2D.
     *
     * @return Un String detallando si la contra fue letal o si la defensa logró replegarse.
     */
    private String resolverContraataque() {
        StringBuilder sb = new StringBuilder();
        sb.append("¡").append(equipoAtacante.getNombrePais()).append(" sale a toda velocidad a la contra!\n");
        
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
            sb.append("-> Abriendo interfaz de Minijuego...");
            this.enMinijuego = true; // Aplicamos el freno de mano
            
        } else {
            sb.append("-> ¡Tremenda defensa! La defensa logra replegarse.");
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
    public Equipo getEquipoDefensor() { 
        return equipoDefensor; 
    }
}