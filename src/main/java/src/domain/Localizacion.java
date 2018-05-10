package src.domain;

public class Localizacion {
    private String idIncidencia;
    private String idEspacio;
    private float x;
    private float y;
    private String planta;

  public Localizacion(String idIncidencia, String idEspacio, float x, float y, String planta) {
    this.idIncidencia = idIncidencia;
    this.idEspacio = idEspacio;
    this.x = x;
    this.y = y;
    this.planta = planta;
  }

  public String getIdIncidencia() { return idIncidencia; }

  public String getIdEspacio() {
    return idEspacio;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public String getPlanta() {
    return planta;
  }

}
