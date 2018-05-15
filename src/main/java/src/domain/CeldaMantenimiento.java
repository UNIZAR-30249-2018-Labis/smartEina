package src.domain;

public class CeldaMantenimiento {
    private String idEspacio;
    private String idTrabajador;
    private String idIncidencia;

    private String dia;
    private int hora;

    public CeldaMantenimiento(String idEspacio, String idTrabajador, String idIncidencia, String dia, int hora) {
        this.idEspacio = idEspacio;
        this.idTrabajador = idTrabajador;
        this.idIncidencia = idIncidencia;
        this.dia = dia;
        this.hora = hora;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public String getIdTrabajador() {
        return idTrabajador;
    }

    public String getIdIncidencia() {
        return idIncidencia;
    }

    public String getDia() {
        return dia;
    }

    public int getHora() {
        return hora;
    }
}
