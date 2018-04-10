package src.domain;

import java.util.ArrayList;

public class EspacioEntity {
    private String id;
    private String nombre;
    private String edificio;
    private String tipoDeUso;
    private Boolean exterior;
    private int planta;
    private ArrayList<HorarioOV> horario;

    public EspacioEntity (String id, String nombre, String edificio, String tipoDeUso, Boolean exterior, int planta, ArrayList<HorarioOV> horario) {
        this.id = id;
        this.nombre = nombre;
        this.edificio = edificio;
        this.tipoDeUso = tipoDeUso;
        this.exterior = exterior;
        this.planta = planta;
        this.horario = horario;
    }

    public ArrayList<HorarioOV> devolverHorario() {
        return horario;
    }
}
