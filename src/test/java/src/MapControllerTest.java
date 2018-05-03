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
    int idIncidencia1 = incidenciaRepositorio.addIncidencia("Test","Test","PENDIENTE","3",l);
    System.out.println(idIncidencia1);
    assertTrue(idIncidencia1 > -1);
  }

  @Test
  public void buscarIncidencia_noexiste() {
    Incidencia res = incidenciaRepositorio.findIncidenciaByID("-1");
    assertTrue(res == null);
  }

  @Test
  public void buscarIncidencia_existe() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int idIncidencia1 = incidenciaRepositorio.addIncidencia("Test","Test","PENDIENTE","3",l);
    Incidencia resultado = incidenciaRepositorio.findIncidenciaByID(String.valueOf(idIncidencia1));
    assertTrue(resultado.getId().equals(String.valueOf(idIncidencia1)));
  }

  @Test
  public void borrarIncidencia_noexiste() {
    boolean res = incidenciaRepositorio.deleteIncidenciaByID("-1");
    assertFalse(res);
  }

  @Test
  public void borrarIncidencia_existe() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int idIncidencia1 = incidenciaRepositorio.addIncidencia("Test","Test","PENDIENTE","3",l);
    boolean res = incidenciaRepositorio.deleteIncidenciaByID(String.valueOf(idIncidencia1));
    assertTrue(res);
  }

  @Test
  public void updateIncidencia_noexiste() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    boolean res = incidenciaRepositorio.updateIncidenciaByID("-1","Nuevo","Test","PENDIENTE","3",l);
    assertFalse(res);
  }

  @Test
  public void updateIncidencia_existe() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","PENDIENTE","3",l);
    boolean res = incidenciaRepositorio.updateIncidenciaByID(String.valueOf(incidencia1),"trasUpdate","Test","PENDIENTE","3",l);
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    System.out.println(res);
    System.out.println(i.getTitulo());
    assertTrue(res && i.getTitulo().equals(("trasUpdate")));
  }

  @Test
  public void sacarIncidenciasdeTrabajador_existe() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    for( int i = 0; i < 5 ; i++) incidenciaRepositorio.addIncidencia("Nuevo" + i,"Test","PENDIENTE","4",l);
    ArrayList<Incidencia> listaIncidencias = incidenciaRepositorio.findIncidenciaOfTrabajador("4");
    for(int i = 0; i < 5 ; i++) System.out.println(listaIncidencias.get(i).getTitulo());
    assertTrue(listaIncidencias.size() > 0);
  }

  @Test
  public void sacarIncidenciasdeTrabajador_noexiste() {
    ArrayList<Incidencia> listaIncidencias = incidenciaRepositorio.findIncidenciaOfTrabajador("-1");
    assertTrue(listaIncidencias.size() == 0);
  }

  @Test
  public void pendienteToIncompleta() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","PENDIENTE","3",l);
    boolean res = incidenciaRepositorio.pendienteToIncompleta(String.valueOf(incidencia1));
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    assertTrue(res && i.getEstado().equals(("INCOMPLETA")));
  }

  @Test
  public void asignadaToCompletada() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","ASIGNADA","3",l);
    boolean res = incidenciaRepositorio.asignadaToCompletada(String.valueOf(incidencia1));
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    assertTrue(res && i.getEstado().equals(("COMPLETADA")));
  }

  @Test
  public void incompletaToPendiente() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","INCOMPLETA","3",l);
    boolean res = incidenciaRepositorio.incompletaToPendiente(String.valueOf(incidencia1));
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    assertTrue(res && i.getEstado().equals(("PENDIENTE")));
  }

  @Test
  public void pendienteToAceptada() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","PENDIENTE","3",l);
    boolean res = incidenciaRepositorio.pendienteToAceptada(String.valueOf(incidencia1));
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    assertTrue(res && i.getEstado().equals(("ACEPTADA")));
  }

  @Test
  public void aceptadaToAsignada() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","ACEPTADA","3",l);
    boolean res = incidenciaRepositorio.aceptadaToAsignada(String.valueOf(incidencia1),"3");
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    assertTrue(res && i.getEstado().equals(("ASIGNADA")));
  }

  @Test
  public void pendienteToRechazada() {
    Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
    int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","PENDIENTE","3",l);
    boolean res = incidenciaRepositorio.pendienteToRechazada(String.valueOf(incidencia1));
    Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
    assertTrue(res && i.getEstado().equals(("RECHAZADA")));
  }

}