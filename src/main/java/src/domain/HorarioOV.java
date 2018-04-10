package src.domain;

public class HorarioOV {
    private String idEspacio;
    private String dia;
    private int horaInicio;
    private String actividad;

    public HorarioOV (String idEspacio, String dia, int horaInicio, String actividad) {
        this.idEspacio = idEspacio;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.actividad = actividad;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public String getDia() {
        return dia;
    }

    public int getHoraInicio() {
        return horaInicio;
    }

    public String getActividad() {
        return actividad;
    }
}
