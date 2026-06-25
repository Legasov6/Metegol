package Entidades;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MercadoFichajes {
    
    // Este es tu "Banco Común"
    private List<Futbolista> bancoComun;

    public MercadoFichajes() {
        this.bancoComun = new ArrayList<>();
    }

    /**
     * Lee el archivo CSV y carga los objetos polimórficos al banco común.
     * @param rutaArchivo La ruta donde se encuentra jugadores.csv
     */
    public void cargarMercadoDesdeCSV(String rutaArchivo) {
        String linea = "";
        String separador = ",";

        // El try-with-resources cierra automáticamente el BufferedReader al terminar
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            
            // 1. Leer y descartar la primera línea porque son las cabeceras del CSV
            br.readLine();

            // 2. Iterar línea por línea hasta el final del archivo
            while ((linea = br.readLine()) != null) {
                
                // Dividir la línea de texto en un arreglo usando la coma como separador
                String[] datos = linea.split(separador);

                // Mapear los datos a variables (parseando los números)
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

                // 3. Evaluar la posición para aplicar Polimorfismo e instanciar la clase correcta
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
                        // El portero es el único al que le pasamos el atributo 'atajada'
                        nuevoJugador = new Portero(nombre, pais, velocidad, disparo, pase, defensa, atajada, precio);
                        break;
                    default:
                        System.out.println("Posición desconocida para el jugador: " + nombre);
                }

                // 4. Si el objeto se creó con éxito, se añade a la lista general
                if (nuevoJugador != null) {
                    bancoComun.add(nuevoJugador);
                }
            }
            
            System.out.println("Éxito: Se cargaron " + bancoComun.size() + " jugadores al banco común.");

        } catch (IOException e) {
            // Blindaje contra errores de lectura (ej. el archivo no existe)
            System.err.println("Error crítico al leer el archivo CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            // Blindaje por si en el CSV alguien puso una letra donde iba un número
            System.err.println("Error de formato numérico en el archivo de datos: " + e.getMessage());
        }
    }

    // Método para obtener la lista
    public List<Futbolista> getBancoComun() {
        return bancoComun;
    }
}