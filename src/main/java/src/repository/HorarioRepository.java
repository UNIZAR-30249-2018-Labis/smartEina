package src.repository;

import src.domain.EspacioEntity;
import src.domain.HorarioOV;

public interface HorarioRepository {

    Boolean addHorario(HorarioOV horario);

    Boolean deleteHorario(String idEspacio, String dia, Integer hora);

    HorarioOV[] viewHorario(String idEspacio, String dia);

    HorarioOV[] viewHorarioLibre(String idEspacio, String dia);

    Boolean estaEspacioLibre(String idEspacio, String dia, Integer hora);


}
