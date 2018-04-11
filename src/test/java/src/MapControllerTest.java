package src;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.EspacioEntity;
import src.repository.EspacioRepository;
import src.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest

public class MapControllerTest {

  @Autowired
  protected EspacioRepository espacioRepositorio;

  @Test
  public void getInfoTest() {
    EspacioEntity e = new EspacioEntity("CRE.1065.00.020","PASILLO","TORRES QUEVEDO","PASILLO",false,0,null);
    EspacioEntity buscado = espacioRepositorio.getInfoByID(e.getId());
    assert(e.getId().equals(buscado.getId()));
  }
}