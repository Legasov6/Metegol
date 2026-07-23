package Metegol; // Asegúrate de que coincida con tu paquete

import Entidades.MercadoFichajes;
import Entidades.Jugador;
import Entidades.Equipo;
import Entidades.Futbolista;
import java.util.List;
import java.util.Scanner;

public class PruebaMercado {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 1. Inicializar y cargar el mercado global
        MercadoFichajes mercado = new MercadoFichajes();
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
                    List<Futbolista> disponibles = mercado.getBancoComun();
                    for (int i = 0; i < disponibles.size(); i++) {
                        Futbolista f = disponibles.get(i);
                        // Imprime el índice entre corchetes antes del nombre
                        System.out.println("[" + i + "] " + f.getNombre() + " (" + f.getClass().getSimpleName() + ") | Costo: " + f.getPrecio());
                    }
                    break;

                case "2":
                    if (cantidadJugadores >= 11) {
                        System.out.println("\n[!] Tu plantilla ya está llena (11/11). Devuelve a un jugador primero.");
                        break;
                    }
                    
                    System.out.print("\nIngresa el NÚMERO del jugador a fichar (Ej: 0, 1, 2...): ");
                    try {
                        int indiceFichaje = Integer.parseInt(scanner.nextLine());
                        List<Futbolista> banco = mercado.getBancoComun();
                        
                        // Validar que el número ingresado exista en la lista actual
                        if (indiceFichaje >= 0 && indiceFichaje < banco.size()) {
                            Futbolista fichaje = banco.get(indiceFichaje);
                            dt.comprarFutbolista(fichaje, mercado);
                        } else {
                            System.out.println("[!] Número fuera de rango. Revisa la lista del mercado (Opción 1).");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Entrada inválida. Debes ingresar un número, no texto.");
                    }
                    break;

                case "3":
                    if (cantidadJugadores == 0) {
                        System.out.println("\n[!] Tu plantilla está vacía. No tienes a quién devolver.");
                        break;
                    }
                    
                    System.out.println("\n--- TU PLANTILLA ACTUAL ---");
                    List<Futbolista> misTitularesParaDevolver = dt.getEquipoAsignado().getTitulares();
                    for (int i = 0; i < misTitularesParaDevolver.size(); i++) {
                        System.out.println("[" + i + "] " + misTitularesParaDevolver.get(i).getNombre());
                    }
                    
                    System.out.print("\nIngresa el NÚMERO del jugador a devolver: ");
                    try {
                        int indiceDevolucion = Integer.parseInt(scanner.nextLine());
                        
                        if (indiceDevolucion >= 0 && indiceDevolucion < misTitularesParaDevolver.size()) {
                            Futbolista devolucion = misTitularesParaDevolver.get(indiceDevolucion);
                            dt.devolverFutbolista(devolucion, mercado);
                        } else {
                            System.out.println("[!] Número fuera de rango. Revisa tu plantilla.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[!] Entrada inválida. Debes ingresar un número.");
                    }
                    break;

                case "4":
                    System.out.println("\n--- PLANTILLA ACTUAL DE " + dt.getEquipoAsignado().getNombrePais().toUpperCase() + " ---");
                    if (cantidadJugadores == 0) {
                        System.out.println("No tienes jugadores todavía.");
                    } else {
                        List<Futbolista> misTitulares = dt.getEquipoAsignado().getTitulares();
                        for (int i = 0; i < misTitulares.size(); i++) {
                            System.out.println("[" + i + "] " + misTitulares.get(i).getNombre() + " (" + misTitulares.get(i).getClass().getSimpleName() + ")");
                        }
                    }
                    //System.out.println("Estadística de Pase del Equipo: " + dt.getEquipoAsignado().calcularPaseTotal());
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
}