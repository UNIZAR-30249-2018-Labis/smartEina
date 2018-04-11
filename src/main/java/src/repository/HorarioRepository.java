package src.repository;

import src.domain.EspacioEntity;
import src.domain.HorarioOV;

public interface HorarioRepository {

    Boolean addHorario(EspacioEntity espacio, String dia, Integer hora, String actividad);

    Boolean deleteHorario(EspacioEntity espacio, String dia, Integer hora);

    HorarioOV[] viewHorario(EspacioEntity espacio, String dia);

    HorarioOV[] viewHorarioLibre(EspacioEntity espacio, String dia);

    Boolean estaEspacioLibre(EspacioEntity espacio, String dia, Integer hora);


}
