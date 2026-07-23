/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;
import java.io.Serializable;
/**
 *
 * @author legasov
 */
public class Jugador implements Serializable{
    private String nombreDT;
    private int presupuesto;
    private Equipo equipoAsignado;
    private static final long serialVersionUID = 1L;

    public Jugador(String nombreDT, int presupuesto, Equipo equipoAsignado) {
        this.nombreDT = nombreDT;
        this.presupuesto = presupuesto;
        this.equipoAsignado = equipoAsignado;
    }
    //Método para comprar un futbolista. Devuelve true si la compra es exitosa
    public boolean comprarFutbolista(Futbolista fichaje, MercadoFichajes mercado){
        // Verifica que haya espacio en el equipo
        if(this.equipoAsignado.getTitulares().size()>=11){
            System.out.println("Equipo lleno. Debes devolver a un jugador antes de comprar otro");
            return false;
        }
        //Validacion
        if(fichaje == null){
            System.out.println("Error: El futbolista no existe");
            return false;
        }
        
        //Verifica si hay fondos
        if(this.presupuesto >= fichaje.getPrecio()){
            //Si hay dinero, resta el precio del presupuesto
            this.presupuesto -= fichaje.getPrecio();
            //Añade el jugador al equipo del DT
            this.equipoAsignado.agregarFutbolista(fichaje);
            //Quitar al jugador del banco común para que no pueda tener repetidos
            mercado.getBancoComun().remove(fichaje);
            
            System.out.println("Fichaje exitoso!" + fichaje.getNombre() + "está enlistado en la selección de " + equipoAsignado.getNombrePais());
            System.out.println("Presupuesto restante de " + this.nombreDT + ": " + this.presupuesto + "monedas");
            return true;
        } else { //Si no hay fondos
            System.out.println("Fondos insuficientes");
            return false;
        }
    }
    
    // Método para devolver un futbolista al mercado y reembolsarle al jugador el dinero invertido
    public boolean devolverFutbolista(Futbolista jugadorDevuelto, MercadoFichajes mercado){
        //Verifica si el jugador esta en el equipo
        if(this.equipoAsignado.getTitulares().contains(jugadorDevuelto)){
            this.equipoAsignado.removerFutbolista(jugadorDevuelto); //Quita al jugador del equipo del DT
            this.presupuesto += jugadorDevuelto.getPrecio(); //Reembolsar dinero
            mercado.getBancoComun().add(jugadorDevuelto); //Devolver el jugador al banco común
            
            System.out.println("Devolución exitosa: " + jugadorDevuelto.getNombre() + " devuelto al mercado");
            System.out.println("Presupuesto actual: " + nombreDT + ": "+ this.presupuesto);
            return true;
        } else {
            System.out.println("Error: " + jugadorDevuelto.getNombre() + " no pertenece a tu equipo");
            return false;
        }
    }
    
    public int getPresupuesto(){
        return presupuesto;
    }

    public Equipo getEquipoAsignado() {
        return equipoAsignado;
    }

    public String getNombreDT() {
        return nombreDT;
    }
    
    
    
}
