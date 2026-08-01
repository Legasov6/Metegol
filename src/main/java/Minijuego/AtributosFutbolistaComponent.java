package Minijuego;

import Entidades.Futbolista;
import com.almasb.fxgl.entity.component.Component;

/**
 * Funcionar como un puente (Componente acoplable de FXGL)
 * que adhiere los datos lógicos de un objeto Futbolista a su
 * correspondiente entidad gráfica, permitiendo escalar sus
 * estadísticas matemáticas para el motor físico.
 * @author GabrielTremaria
 */
public class AtributosFutbolistaComponent extends Component {
    private Futbolista datosLogicos;

    public AtributosFutbolistaComponent(Futbolista datosLogicos) {
        this.datosLogicos = datosLogicos;
    }

    public Futbolista getDatos() {
        return datosLogicos;
    }
    
    public double getVelocidadFXGL() {
        return datosLogicos.getVelocidad() * 30.0; // Lo escalamos para FXGL
    }
    
    // Método para la potencia de tiro
    public double getFuerzaTiroFXGL() { //No se usó la potencia adaptable
        return datosLogicos.getTiro() * 150.0; 
    }
}