package src;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.CeldaHorario;
import src.domain.Espacio;
import src.domain.EspacioRepository;
import src.domain.Horario;

import java.util.ArrayList;
import src.domain.Incidencia;
import src.domain.IncidenciaRepository;
import src.domain.Localizacion;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})

public class MapControllerTest {

  @Autowired
  protected EspacioRepository espacioRepositorio;

  @Autowired
  protected IncidenciaRepository incidenciaRepositorio;

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

  @Test
  public void addIncidenciaTest() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    Incidencia i = new Incidencia("0","Test","Test","PENDIENTE",l);
    boolean res = incidenciaRepositorio.addIncidencia(i);
    assertTrue(res);
  }

  @Test
  public void buscarIncidencia_noexiste() {
    Incidencia res = incidenciaRepositorio.findIncidenciaByID("-1");
    assertTrue(res == null);
  }

  @Test
  public void buscarIncidencia_existe() {
    Localizacion l = new Localizacion("CRE.1065.00.030",10,15,1);
    Incidencia i = new Incidencia("1","Test","Test","PENDIENTE",l);
    incidenciaRepositorio.addIncidencia(i);
    Incidencia resultado = incidenciaRepositorio.findIncidenciaByID(i.getId());
    assertTrue(resultado.getId().equals(i.getId()));
  }

  @Test
  public void borrarIncidencia_noexiste() {
    boolean res = incidenciaRepositorio.deleteIncidenciaByID("-1");
    assertFalse(res);
  }

  @Test
  public void borrarIncidencia_existe() {
    boolean res = incidenciaRepositorio.deleteIncidenciaByID("1");
    assertTrue(res);
  }

  @Test
  public void updateIncidencia_noexiste() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    Incidencia i = new Incidencia("88","Nuevo","Test","PENDIENTE",l);
    boolean res = incidenciaRepositorio.updateIncidenciaByID(i);
    assertTrue(incidenciaRepositorio.findIncidenciaByID(i.getId()) == null);
  }

  @Test
  public void updateIncidencia_existe() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    Incidencia i = new Incidencia("50","Nuevo","Test","PENDIENTE",l);
    boolean anadir = incidenciaRepositorio.addIncidencia(i);
    i = new Incidencia("50","trasUpdate","trasUpdate","PENDIENTE",l);
    boolean res = incidenciaRepositorio.updateIncidenciaByID(i);
    assertTrue(res && i.getTitulo().equals(("trasUpdate")));
  }



}