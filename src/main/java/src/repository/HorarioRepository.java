package src.repository;

import src.domain.EspacioEntity;
import src.domain.HorarioOV;

import java.util.List;

public interface HorarioRepository {

    Boolean addHorario(HorarioOV horario);

    Boolean deleteHorario(String idEspacio, String dia, int hora);

    List<HorarioOV> viewHorario(String idEspacio, String dia);

    List<Integer> viewHorarioLibre(String idEspacio, String dia);

    Boolean estaEspacioLibre(String idEspacio, String dia, int hora);


}
