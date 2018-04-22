package src.domain;

import java.util.ArrayList;

public class Espacio extends Entity {
    private String nombre;
    private String edificio;
    private String tipoDeUso;
    private Boolean exterior;
    private int planta;
    private Horario horario;

    public String getId() {return super.getId();}

    public Horario getHorario() {
        return this.horario;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getEdificio() {
        return this.edificio;
    }

    public String getTipoDeUso() { return this.tipoDeUso; }

    public Boolean getExterior() { return this.exterior; }

    public int getPlanta() { return planta; }

    public Espacio(String id, String nombre, String edificio, String tipoDeUso, Boolean exterior, int planta, Horario horario) {
        super(id);
        this.nombre = nombre;
        this.edificio = edificio;
        this.tipoDeUso = tipoDeUso;
        this.exterior = exterior;
        this.planta = planta;
        this.horario = horario;
    }
}
