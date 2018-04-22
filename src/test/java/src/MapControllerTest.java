package src;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.Espacio;
import src.domain.EspacioRepository;

@RunWith(SpringRunner.class)
@SpringBootTest

public class MapControllerTest {

  @Autowired
  protected EspacioRepository espacioRepositorio;

  @Test
  public void getInfoTest() {
    Espacio e = new Espacio("CRE.1065.00.020","PASILLO","TORRES QUEVEDO","PASILLO",false,0,null);
    Espacio buscado = espacioRepositorio.findEspacioByID(e.getId());
    assert(e.getId().equals(buscado.getId()));
  }
}