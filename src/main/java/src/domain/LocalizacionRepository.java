package src.domain;

public interface LocalizacionRepository {

  Localizacion findLocalizacion(String idEspacio);

  boolean  addLocalizacion(Localizacion l);

  int getIDofLocalizacion(Localizacion l);

  boolean deleteLocalizacionByID(Localizacion l);

}
