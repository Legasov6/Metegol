package Metegol; // Asegúrate de que coincida con tu paquete

import Entidades.MercadoFichajes;
import Entidades.Jugador;
import Entidades.Equipo;
import Entidades.Futbolista;
import java.util.Scanner;



public class PruebaMercado {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 1. Inicializar y cargar el mercado global
        MercadoFichajes mercado = new MercadoFichajes();
        // Asegúrate de que esta ruta coincida con la que definiste para tu CSV
        mercado.cargarMercadoDesdeCSV("recursos/Plantilla.csv"); 

        System.out.println("==================================================");
        System.out.println("      CHAMPS 2026 - CONFIGURACIÓN DE LA FINAL     ");
        System.out.println("==================================================\n");

        // 2. Registro de Usuarios (Sin Autenticación)
        System.out.println("--- REGISTRO DEL JUGADOR 1 ---");
        System.out.print("Ingrese su nombre (DT 1): ");
        String nombreDT1 = scanner.nextLine();
        System.out.print("Ingrese el país que desea dirigir: ");
        String pais1 = scanner.nextLine();
        // Asignamos 1000 monedas de presupuesto inicial
        Jugador dt1 = new Jugador(nombreDT1, 1000, new Equipo(pais1)); 

        System.out.println("\n--- REGISTRO DEL JUGADOR 2 ---");
        System.out.print("Ingrese su nombre (DT 2): ");
        String nombreDT2 = scanner.nextLine();
        System.out.print("Ingrese el país que desea dirigir: ");
        String pais2 = scanner.nextLine();
        Jugador dt2 = new Jugador(nombreDT2, 1000, new Equipo(pais2));

        // 3. Fase de Mercado (Turnos)
        System.out.println("\n¡Registro completado! Pasando a la fase de fichajes...");
        
        gestionarTurnoDT(dt1, mercado, scanner);
        gestionarTurnoDT(dt2, mercado, scanner);

        // 4. Cierre del Sprint 1
        System.out.println("==================================================");
        System.out.println(" ¡AMBAS PLANTILLAS CONFIRMADAS! EL PARTIDO VA A COMENZAR.");
        System.out.println("==================================================");
        
        scanner.close();
    }

    /**
     * Método auxiliar que despliega el menú interactivo para un DT específico.
     */
    private static void gestionarTurnoDT(Jugador dt, MercadoFichajes mercado, Scanner scanner) {
        boolean turnoTerminado = false;

        while (!turnoTerminado) {
            int cantidadJugadores = dt.getEquipoAsignado().getTitulares().size();
            
            System.out.println("\n==================================================");
            System.out.println(" TURNO DE FICHAJES: " + dt.getNombreDT().toUpperCase() + " (" + dt.getEquipoAsignado().getNombrePais() + ")");
            System.out.println(" Presupuesto: " + dt.getPresupuesto() + " monedas | Plantilla: " + cantidadJugadores + "/11");
            System.out.println("==================================================");
            System.out.println("1. Ver mercado de jugadores disponibles");
            System.out.println("2. Fichar un jugador");
            System.out.println("3. Devolver un jugador");
            System.out.println("4. Ver mi plantilla actual");
            System.out.println("5. Confirmar equipo y terminar mi turno");
            System.out.print("Elige una opción: ");
            
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    System.out.println("\n--- JUGADORES EN EL MERCADO ---");
                    for (Futbolista f : mercado.getBancoComun()) {
                        System.out.println("- " + f.getNombre() + " (" + f.getClass().getSimpleName() + ") | Costo: " + f.getPrecio());
                    }
                    break;

                case "2":
                    if (cantidadJugadores >= 11) {
                        System.out.println("\n[!] Tu plantilla ya está llena (11/11). Devuelve a un jugador primero.");
                        break;
                    }
                    System.out.print("\nEscribe el nombre exacto del jugador a fichar: ");
                    String nombreFichaje = scanner.nextLine();
                    Futbolista fichaje = buscarPorNombre(mercado.getBancoComun(), nombreFichaje);
                    
                    if (fichaje != null) {
                        dt.comprarFutbolista(fichaje, mercado);
                    } else {
                        System.out.println("[!] Jugador no encontrado en el mercado. Verifica que esté bien escrito.");
                    }
                    break;

                case "3":
                    if (cantidadJugadores == 0) {
                        System.out.println("\n[!] Tu plantilla está vacía. No tienes a quién devolver.");
                        break;
                    }
                    System.out.print("\nEscribe el nombre exacto del jugador a devolver: ");
                    String nombreDevolucion = scanner.nextLine();
                    Futbolista devolucion = buscarPorNombre(dt.getEquipoAsignado().getTitulares(), nombreDevolucion);
                    
                    if (devolucion != null) {
                        dt.devolverFutbolista(devolucion, mercado);
                    } else {
                        System.out.println("[!] Ese jugador no está en tu plantilla.");
                    }
                    break;

                case "4":
                    System.out.println("\n--- PLANTILLA ACTUAL DE " + dt.getEquipoAsignado().getNombrePais().toUpperCase() + " ---");
                    if (cantidadJugadores == 0) {
                        System.out.println("No tienes jugadores todavía.");
                    } else {
                        for (Futbolista f : dt.getEquipoAsignado().getTitulares()) {
                            System.out.println("- " + f.getNombre() + " (" + f.getClass().getSimpleName() + ")");
                        }
                    }
                    break;

                case "5":
                    if (cantidadJugadores < 11) {
                        System.out.println("\n[!] ADVERTENCIA: Aún no tienes 11 jugadores. Tienes " + cantidadJugadores + ".");
                        System.out.print("¿Estás seguro de que quieres terminar tu turno? (S/N): ");
                        String confirmacion = scanner.nextLine();
                        if (confirmacion.equalsIgnoreCase("S")) {
                            turnoTerminado = true;
                        }
                    } else {
                        turnoTerminado = true;
                    }
                    break;

                default:
                    System.out.println("\n[!] Opción no válida. Ingresa un número del 1 al 5.");
            }
        }
    }

    /**
     * Método auxiliar para buscar un futbolista por su nombre dentro de cualquier lista.
     */
    private static Futbolista buscarPorNombre(java.util.List<Futbolista> lista, String nombreBuscado) {
        for (Futbolista f : lista) {
            if (f.getNombre().equalsIgnoreCase(nombreBuscado)) {
                return f;
            }
        }
        return null;
    }
}