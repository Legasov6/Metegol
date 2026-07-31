// @author Gabriel Tremaria

package Minijuego;

import Entidades.Futbolista;
import Entidades.Portero;
import Entidades.Defensor;
import Entidades.Delantero;
import Entidades.Mediocampista;
import java.util.List;
import java.util.stream.Collectors;

public class SelectorMinijuego {

    // Selecciona los atacantes
    public static List<Futbolista> seleccionarAtacantes(List<Futbolista> equipoCompleto) {
        return equipoCompleto.stream()
                // No pueden ser Porteros ni Defensores
                .filter(f -> !(f instanceof Portero) && !(f instanceof Defensor))
                
                // Ordenamos por la tasa Tiro / Velocidad (de mayor a menor)
                .sorted((f1, f2) -> {
                    double tasaF1 = (double) f1.getTiro() / Math.max(f1.getVelocidad(), 1);
                    double tasaF2 = (double) f2.getTiro() / Math.max(f2.getVelocidad(), 1);
                    
                    return Double.compare(tasaF2, tasaF1); // Invertido para que el mayor quede primero
                })
                
                // Nos quedamos solo con los 2 mejores
                .limit(2)
                .collect(Collectors.toList());
    }


    // Selección de defensores
    public static List<Futbolista> seleccionarDefensores(List<Futbolista> equipoCompleto) {
        // Aislamos al portero
        Futbolista portero = equipoCompleto.stream()
                .filter(f -> f instanceof Portero)
                .findFirst()
                .orElse(null);

        // Buscamos exclusivamente a 3 defensores puros
        List<Futbolista> defensores = equipoCompleto.stream()
                .filter(f -> f instanceof Defensor) // <-- TU SOLUCIÓN MÁS DIRECTA
                .limit(3)
                .collect(Collectors.toList());

        // Agregar el portero al inicio de la lista para tener a los 4 elegidos
        if (portero != null) {
            defensores.add(0, portero); 
        }
        return defensores;
    }
}