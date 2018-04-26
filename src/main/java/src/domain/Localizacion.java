package src.domain;

public class Localizacion {
    private String idEspacio;
    private float x;
    private float y;
    private int planta;

  public Localizacion(String idEspacio, float x, float y, int planta) {
    this.idEspacio = idEspacio;
    this.x = x;
    this.y = y;
    this.planta = planta;
  }

  public String getIdEspacio() {
    return idEspacio;
  }

  public float getX() {
    return x;
  }

  public float getY() {
    return y;
  }

  public int getPlanta() {
    return planta;
  }

}
