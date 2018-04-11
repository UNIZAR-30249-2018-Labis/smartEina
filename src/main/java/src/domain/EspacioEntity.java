package src.domain;

import java.util.ArrayList;

public class EspacioEntity {
    private String id;
    private String nombre;
    private String edificio;
    private String tipoDeUso;
    private Boolean exterior;
    private int planta;

    public ArrayList<HorarioOV> getHorario() {
        return horario;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEdificio() {
        return edificio;
    }

    public String getTipoDeUso() {
        return tipoDeUso;
    }

    public int getPlanta() {
        return planta;
    }

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
