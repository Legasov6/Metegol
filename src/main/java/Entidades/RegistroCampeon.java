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
        
        // El sistema captura la fecha y hora exacta del sistema automáticamente
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.fechaHora = dtf.format(LocalDateTime.now());
    }

    // Getters para que tu compañero pueda leer los datos al armar su tabla
    public String getNombreDT() { return nombreDT; }
    public String getSeleccion() { return seleccion; }
    public String getMarcadorFinal() { return marcadorFinal; }
    public String getFechaHora() { return fechaHora; }
    
    // Formato CSV rápido por si tu compañero lo necesita
    public String toCSV() {
        return nombreDT + "," + seleccion + "," + marcadorFinal + "," + fechaHora;
    }
    // Añade este método al final de tu clase RegistroCampeon
    public void setFechaHora(String fechaHoraHistorica) {
        this.fechaHora = fechaHoraHistorica;
    }
}