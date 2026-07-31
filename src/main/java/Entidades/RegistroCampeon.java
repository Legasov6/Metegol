// @author Frank Farias

package Entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    
    // Formato CSV
    public String toCSV() {
        return nombreDT + "," + seleccion + "," + marcadorFinal + "," + fechaHora;
    }
    
    public void setFechaHora(String fechaHoraHistorica) {
        this.fechaHora = fechaHoraHistorica;
    }
}