package Logica;

import Entidades.RegistroCampeon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GestorEstadisticas {
    
    // Ruta del archivo persistente
    private static final String RUTA_ARCHIVO = "recursos/campeones.csv";

    public static void guardarCampeon(RegistroCampeon nuevoCampeon) {
        // El 'true' en FileWriter indica que vamos a añadir texto al final (Append), no a sobrescribir
        try (FileWriter fw = new FileWriter(RUTA_ARCHIVO, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println(nuevoCampeon.toCSV());
            System.out.println("Historial actualizado correctamente.");
            
        } catch (IOException e) {
            System.err.println("Error al guardar el campeón: " + e.getMessage());
        }
    }

    public static List<RegistroCampeon> obtenerHistorial() {
        List<RegistroCampeon> historial = new ArrayList<>();
        File archivo = new File(RUTA_ARCHIVO);

        // Si es la primera vez que se juega y el archivo no existe, devolvemos la lista vacía
        if (!archivo.exists()) {
            return historial;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                // Validamos que la línea tenga exactamente los 4 datos (DT, País, Marcador, Fecha)
                if (datos.length == 4) {
                    RegistroCampeon rc = new RegistroCampeon(datos[0], datos[1], datos[2]);
                    rc.setFechaHora(datos[3]); // Le inyectamos la fecha guardada
                    historial.add(rc);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el historial: " + e.getMessage());
        }
        
        return historial;
    }
}