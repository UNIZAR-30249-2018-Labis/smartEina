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

    @Override
    public boolean equals(Object o) {

        if (o instanceof CeldaHorario) {
            CeldaHorario c = (CeldaHorario) o;
            if (this.idEspacio.equals(c.idEspacio) && this.dia.equals(c.dia
            ) && this.horaDeInicio == c.horaDeInicio && this.actividad.equals(c.actividad
            )) return true;
            else return false;
        }
        return false;

    }
}
