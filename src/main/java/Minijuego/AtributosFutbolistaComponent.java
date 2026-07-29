package Minijuego;

import Entidades.Futbolista;
import com.almasb.fxgl.entity.component.Component;

public class AtributosFutbolistaComponent extends Component {
    private Futbolista datosLogicos;

    public AtributosFutbolistaComponent(Futbolista datosLogicos) {
        this.datosLogicos = datosLogicos;
    }

    public Futbolista getDatos() {
        return datosLogicos;
    }
    
    // Método de conveniencia para la velocidad (escalada para FXGL)
    public double getVelocidadFXGL() {
        return datosLogicos.getVelocidad() * 30.0; 
    }
    
    // Método para la potencia de tiro
    public double getFuerzaTiroFXGL() {
        return datosLogicos.getTiro() * 150.0; 
    }
}