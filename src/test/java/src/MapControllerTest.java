package src;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.application.Application;
import src.domain.CeldaHorario;
import src.domain.Espacio;
import src.domain.EspacioRepository;
import src.domain.Horario;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})

public class MapControllerTest {

  @Autowired
  protected EspacioRepository espacioRepositorio;

  @Test
  public void getInfoTest() {
    Espacio e = new Espacio("CRE.1065.00.020","PASILLO","TORRES QUEVEDO","PASILLO",false,0,null);
    Espacio buscado = espacioRepositorio.findEspacioByID(e.getId());
    assert(e.getId().equals(buscado.getId()));
  }

  @Test
  public void guardarHoraTest() {
    Espacio espacio = espacioRepositorio.findEspacioByID("CRE.1065.00.020");
    Horario horarioOld = espacio.getHorario();
    ArrayList<CeldaHorario> chArray = horarioOld.getHorasLunes();
    CeldaHorario chOld = null;
    for (CeldaHorario c : chArray) {
      if (c.getHoraDeInicio() == 8) {
        chOld = c;
      }
    }
    Boolean result = false;
    if (chOld == null) {
      result = espacioRepositorio.addActividadAlHorario(espacio.getId(), "Lunes", 8, "De prueba");
      assert(result == true);
      result = espacioRepositorio.deleteActividadDelHorario(espacio.getId(), "Lunes", 8);
      assert(result == true);
    } else {
      result = espacioRepositorio.deleteActividadDelHorario(espacio.getId(), "Lunes", 8);
      assert(result == true);
      result = espacioRepositorio.addActividadAlHorario(espacio.getId(), "Lunes", 8, "De prueba");
      assert(result == true);
      result = espacioRepositorio.deleteActividadDelHorario(espacio.getId(), "Lunes", 8);
      assert(result == true);
      result = espacioRepositorio.addActividadAlHorario(espacio.getId(), "Lunes", chOld.getHoraDeInicio(), chOld.getActividad());
      assert(result == true);
    }
  }
}