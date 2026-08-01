package Metegol;

import Entidades.Defensor;
import Entidades.Delantero;
import Entidades.Equipo;
import Entidades.Mediocampista;
import Logica.MotorSimulacion;
import javax.swing.JOptionPane; // Importamos la ventanita estándar de Java

public class TestMotor {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== INICIANDO LABORATORIO DE PRUEBAS ===");

        Equipo equipoAlemania = new Equipo("Alemania");
        Equipo equipoItalia = new Equipo("Italia");

        // Ataque total
        equipoAlemania.agregarFutbolista(new Delantero("Muller", "Alemania", 90, 85, 80, 30, 100));
        equipoAlemania.agregarFutbolista(new Delantero("Gnabry", "Alemania", 95, 80, 75, 20, 100));
        equipoAlemania.agregarFutbolista(new Mediocampista("Kroos", "Alemania", 60, 80, 95, 60, 100));
        equipoAlemania.agregarFutbolista(new Mediocampista("Kimmich", "Alemania", 75, 75, 90, 70, 100));
        equipoAlemania.agregarFutbolista(new Defensor("Hummels", "Alemania", 40, 50, 70, 80, 100));

        // Defensa y contraataque
        equipoItalia.agregarFutbolista(new Delantero("Chiesa", "Italia", 85, 80, 70, 40, 100));
        equipoItalia.agregarFutbolista(new Mediocampista("Barella", "Italia", 80, 75, 85, 80, 100));
        equipoItalia.agregarFutbolista(new Mediocampista("Verratti", "Italia", 70, 70, 90, 85, 100));
        equipoItalia.agregarFutbolista(new Defensor("Chiellini", "Italia", 50, 40, 60, 95, 100));
        equipoItalia.agregarFutbolista(new Defensor("Bonucci", "Italia", 55, 50, 75, 90, 100));

        MotorSimulacion simulador = new MotorSimulacion(equipoAlemania, equipoItalia);

        System.out.println("Pitazo inicial. ¡Comienza el partido!\n");

        while (simulador.getMinuto() < 90) {
            simulador.simularMinuto();
            
            // EL MOCK DEL MINIJUEGO
            if (simulador.isEnMinijuego()) {
                
                String equipoAtacando = simulador.getEquipoAtacante().getNombrePais();
                String mensaje = "¡Ocasión de peligro para " + equipoAtacando + "!\n¿Lograste meter el gol?";
                String[] opciones = {"¡GOLAZO!", "Fallé / La atajaron"};
                
                int respuesta = JOptionPane.showOptionDialog(null,
                        mensaje,
                        "Simulador de Minijuego",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opciones,
                        opciones[0]);

                boolean huboGol = (respuesta == 0);
                
                if (huboGol) {
                    simulador.registrarGol(simulador.getEquipoAtacante());
                }
                simulador.finalizarMinijuego(2, huboGol);
            }            
            Thread.sleep(600); 
        }
        
        System.out.println("\n=== FIN DE LA SIMULACIÓN ===");
    }
}