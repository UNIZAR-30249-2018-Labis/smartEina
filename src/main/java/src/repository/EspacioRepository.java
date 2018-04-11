package src.repository;

import src.domain.EspacioEntity;

public interface EspacioRepository {
  //nombre,edificio,planta y horarios
  EspacioEntity getInfoByID(String id);

  EspacioEntity getInfoByCoordinates(float x,float y,int planta);
}
