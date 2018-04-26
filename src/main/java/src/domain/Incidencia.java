package src.domain;

public class Incidencia extends Entity {
  private String titulo;
  private String desc;
  private String estado;
  private Localizacion localizacion;

  public Incidencia(String id,String titulo,String desc,String estado,Localizacion localizacion) {
    super(id);
    this.titulo = titulo;
    this.desc = desc;
    this.estado = estado;
    this.localizacion = localizacion;
  }

  public String getId() {return super.getId();}

  public String getTitulo() {
    return titulo;
  }

  public String getDesc() {
    return desc;
  }

  public String getEstado() {
    return estado;
  }

  public Localizacion getLocalizacion() {
    return localizacion;
  }
}
