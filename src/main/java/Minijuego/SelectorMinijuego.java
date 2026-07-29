package Minijuego;

import Entidades.Futbolista;
import Entidades.Portero;
import Entidades.Defensor;
import Entidades.Delantero;
import Entidades.Mediocampista;
import java.util.List;
import java.util.stream.Collectors;

public class SelectorMinijuego {

    // ==========================================
    // SELECCIÓN DE ATACANTES
    // ==========================================
    public static List<Futbolista> seleccionarAtacantes(List<Futbolista> equipoCompleto) {
        return equipoCompleto.stream()
                // 1. Filtramos: NO pueden ser Porteros ni Defensores
                .filter(f -> !(f instanceof Portero) && !(f instanceof Defensor))
                
                // 2. Ordenamos por la tasa Tiro / Velocidad (de mayor a menor)
                .sorted((f1, f2) -> {
                    // Evitamos división por cero asignando un mínimo de 1 a la velocidad
                    double tasaF1 = (double) f1.getTiro() / Math.max(f1.getVelocidad(), 1);
                    double tasaF2 = (double) f2.getTiro() / Math.max(f2.getVelocidad(), 1);
                    
                    return Double.compare(tasaF2, tasaF1); // Invertido para que el mayor quede primero
                })
                
                // 3. Nos quedamos solo con los 2 mejores
                .limit(2)
                .collect(Collectors.toList());
    }

    // ==========================================
    // SELECCIÓN DE DEFENSORES
    // ==========================================
    public static List<Futbolista> seleccionarDefensores(List<Futbolista> equipoCompleto) {
        // 1. Aislar al portero (asumimos que siempre hay uno en la lista de titulares)
        Futbolista portero = equipoCompleto.stream()
                .filter(f -> f instanceof Portero)
                .findFirst()
                .orElse(null);

        // 2. Buscar EXCLUSIVAMENTE a 3 defensores puros
        List<Futbolista> defensores = equipoCompleto.stream()
                .filter(f -> f instanceof Defensor) // <-- TU SOLUCIÓN MÁS DIRECTA
                .limit(3)
                .collect(Collectors.toList());

        // 3. Agregar el portero al inicio de la lista para tener a los 4 elegidos
        if (portero != null) {
            defensores.add(0, portero); 
        }

        return defensores;
    }
}