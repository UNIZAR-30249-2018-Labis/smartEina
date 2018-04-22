package src.domain;

public class CeldaHorario {
    private String idEspacio;
    private String dia;
    private int horaDeInicio;
    private String actividad;

    public CeldaHorario(String idEspacio, String dia, int horaDeInicio, String actividad) {
        this.idEspacio = idEspacio;
        this.dia = dia;
        this.horaDeInicio = horaDeInicio;
        this.actividad = actividad;
    }

    public int getHoraDeInicio() {
        return this.horaDeInicio;
    }

    public String getActividad() {
        return this.actividad;
    }
}
