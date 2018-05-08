package src.domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Horario {
    private String idEspacio;
    private ArrayList<CeldaHorario> horasLunes;
    private ArrayList<CeldaHorario> horasMartes;
    private ArrayList<CeldaHorario> horasMiercoles;
    private ArrayList<CeldaHorario> horasJueves;
    private ArrayList<CeldaHorario> horasViernes;

    public Horario(String idEspacio, ArrayList<CeldaHorario> horasLunes, ArrayList<CeldaHorario> horasMartes, ArrayList<CeldaHorario> horasMiercoles, ArrayList<CeldaHorario> horasJueves, ArrayList<CeldaHorario> horasViernes) {
        this.idEspacio = idEspacio;
        this.horasLunes = horasLunes;
        this.horasMartes = horasMartes;
        this.horasMiercoles = horasMiercoles;
        this.horasJueves = horasJueves;
        this.horasViernes = horasViernes;
    }

    public String getIdEspacio() {
        return idEspacio;
    }

    public ArrayList<CeldaHorario> getHorasLunes() {
        return horasLunes;
    }

    public ArrayList<CeldaHorario> getHorasMartes() {
        return horasMartes;
    }

    public ArrayList<CeldaHorario> getHorasMiercoles() {
        return horasMiercoles;
    }

    public ArrayList<CeldaHorario> getHorasJueves() {
        return horasJueves;
    }

    public ArrayList<CeldaHorario> getHorasViernes() {
        return horasViernes;
    }

    public String toJson(){

        return null;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof Horario) {
            Horario c = (Horario) o;
            if (this.idEspacio.equals(c.idEspacio) && this.horasJueves.equals(c.horasJueves) &&
                this.horasLunes.equals(c.horasLunes) && this.horasMartes.equals(c.horasMartes) &&
                this.horasMiercoles.equals(c.horasMiercoles) && this.horasViernes.equals(c.horasViernes)
                ) return true;
            else return false;
        }
        return false;

    }
}
