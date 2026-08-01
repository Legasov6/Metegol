package Entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase que estructura los datos de una victoria para su posterior
 * persistencia en el historial (Salón de la Fama).
 * @author FrankFarias
 */
public class RegistroCampeon {
    private String nombreDT;
    private String seleccion;
    private String marcadorFinal;
    private String fechaHora;

    public RegistroCampeon(String nombreDT, String seleccion, String marcadorFinal) {
        this.nombreDT = nombreDT;
        this.seleccion = seleccion;
        this.marcadorFinal = marcadorFinal;
        
        // Hora y fecha del sistema
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.fechaHora = dtf.format(LocalDateTime.now());
    }

    public String getNombreDT() { return nombreDT; }
    public String getSeleccion() { return seleccion; }
    public String getMarcadorFinal() { return marcadorFinal; }
    public String getFechaHora() { return fechaHora; }
    
    /**
     * Concatena los atributos separados por comas para su escritura
     * en archivo de texto.
     */
    public String toCSV() {
        return nombreDT + "," + seleccion + "," + marcadorFinal + "," + fechaHora;
    }
    /**
     * Permite inyectar una fecha leída desde el disco duro.
     * @param fechaHoraHistorica 
     */
    public void setFechaHora(String fechaHoraHistorica) {
        this.fechaHora = fechaHoraHistorica;
    }
}