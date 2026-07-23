package Entidades;

import java.io.Serializable;

public class EstadoPartido implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int minuto;
    private int golesEquipo1;
    private int golesEquipo2;
    private String narracion;
    private boolean finDePartido;

    public EstadoPartido(int minuto, int golesEquipo1, int golesEquipo2, String narracion, boolean finDePartido) {
        this.minuto = minuto;
        this.golesEquipo1 = golesEquipo1;
        this.golesEquipo2 = golesEquipo2;
        this.narracion = narracion;
        this.finDePartido = finDePartido;
    }

    public int getMinuto() { return minuto; }
    public int getGolesEquipo1() { return golesEquipo1; }
    public int getGolesEquipo2() { return golesEquipo2; }
    public String getNarracion() { return narracion; }
    public boolean isFinDePartido() { return finDePartido; }
}