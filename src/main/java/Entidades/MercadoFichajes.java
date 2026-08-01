package Entidades;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Administra el inventario de jugadores disponibles,
 * extrayendo la información desde una base de datos local en
 * formato CSV
 * @author FrankFarias
 */
public class MercadoFichajes {
    
    /// El Banco Comun que guarda todos los jugadores
    private List<Futbolista> bancoComun;

    public MercadoFichajes() {
        this.bancoComun = new ArrayList<>();
    }

    /**
     * Lee el archivo mediante BufferedReader, separa los datos por
     * comas y utiliza un switch para instanciar subclases (ej.
     * Delantero, Portero) manejando excepciones IOException y NumberFormatException.
     * @param rutaArchivo es la ruta relativa del CSV
     */
    public void cargarMercadoDesdeCSV(String rutaArchivo) {
        String linea = "";
        String separador = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            
            // Salta la primera linea pq son los cabezales de cada columna
            br.readLine();

            // Iterar línea por línea hasta el final del archivo
            while ((linea = br.readLine()) != null) {
                
                // Usa la coma como separador
                String[] datos = linea.split(separador);

                // Pasa el texto a variables
                String nombre = datos[0];
                String pais = datos[1];
                String posicion = datos[2];
                int velocidad = Integer.parseInt(datos[3]);
                int disparo = Integer.parseInt(datos[4]);
                int pase = Integer.parseInt(datos[5]);
                int defensa = Integer.parseInt(datos[6]);
                // La validación para saber si la atajada se asigna o no
                int atajada = 0;
                if (!datos[7].equals("-")){
                    atajada = Integer.parseInt(datos[7]);
                }
                
                int precio = Integer.parseInt(datos[8]);

                Futbolista nuevoJugador = null;

                // Lee el texto de posicion para llamar al constructor de cada tipo de futbolista
                switch (posicion.toLowerCase()) {
                    case "delantero":
                        nuevoJugador = new Delantero(nombre, pais, velocidad, disparo, pase, defensa, precio);
                        break;
                    case "mediocampista":
                        nuevoJugador = new Mediocampista(nombre, pais, velocidad, disparo, pase, defensa, precio);
                        break;
                    case "defensor":
                        nuevoJugador = new Defensor(nombre, pais, velocidad, disparo, pase, defensa, precio);
                        break;
                    case "portero":
                        // El portero es el único al que le pasamos el atributo atajada
                        nuevoJugador = new Portero(nombre, pais, velocidad, disparo, pase, defensa, atajada, precio);
                        break;
                    default:
                        System.out.println("Posición desconocida para el jugador: " + nombre);
                }

                // Si el futbolista se creó con éxito, se añade a la lista general
                if (nuevoJugador != null) {
                    bancoComun.add(nuevoJugador);
                }
            }
            
            System.out.println("Éxito: Se cargaron " + bancoComun.size() + " jugadores al banco común.");

        } catch (IOException e) {
            // Excepción contra errores de lectura como si el archivo no existe
            System.err.println("Error crítico al leer el archivo CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            // Por si en el CSV alguien puso una letra donde iba un número
            System.err.println("Error de formato numérico en el archivo de datos: " + e.getMessage());
        }
    }

    /**
     * Retorna la lista de jugadores disponibles.
     */
    public List<Futbolista> getBancoComun() {
        return bancoComun;
    }
}