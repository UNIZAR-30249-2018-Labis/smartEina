package src;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})

public class IncidenceServiceTest {

    @Autowired
    protected IncidenciaRepository incidenciaRepository;

    @Test
    public void addIncidenciaTest() {
        String idIncidencia = incidenciaRepository.addIncidenciaTest("Prueba", "Prueba", "0", 1,1,"00","CRE.1065.00.020");
        assert(!idIncidencia.equals(""));
        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void buscarIncidenciaNoExisteTest() {
        assert(incidenciaRepository.findIncidenciaByID("-1") == null);
    }

    @Test
    public void buscarIncidenciaExisteTest() {
        String idIncidencia = incidenciaRepository.addIncidenciaTest("Prueba", "Prueba", "0", 1,1,"00","CRE.1065.00.020");
        assert(incidenciaRepository.findIncidenciaByID(idIncidencia) != null);
        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void borrarIncidenciaNoExisteTest() {
        assert(!incidenciaRepository.deleteIncidenciaByID("-1"));
    }

    @Test
    public void borrarIncidenciaExisteTest() {
        String idIncidencia = incidenciaRepository.addIncidenciaTest("Prueba", "Prueba", "0", 1,1,"00","CRE.1065.00.020");
        assert(incidenciaRepository.deleteIncidenciaByID(idIncidencia));
    }

    @Test
    public void updateIncidenciaNoExisteTest() {
        assert(!incidenciaRepository.updateIncidenciaByID("-1", "updated", "updated"));
    }

    @Test
    public void updateIncidenciaExisteTEst() {
        String idIncidencia = incidenciaRepository.addIncidenciaTest("Prueba", "Prueba", "0", 1,1,"00","CRE.1065.00.020");
        assert(incidenciaRepository.updateIncidenciaByID(idIncidencia, "updated", "updated"));
        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

/*    @Test
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
    }*/

    @Test
    public void cambioDeEstadoTest() {
        String idIncidencia = incidenciaRepository.addIncidenciaTest("Prueba", "Prueba", "0", 1,1,"00","CRE.1065.00.020");
        assert(incidenciaRepository.pendienteToIncompleta(idIncidencia));
        assert(incidenciaRepository.incompletaToPendiente(idIncidencia));
        assert(incidenciaRepository.pendienteToAceptada(idIncidencia));

        // TODO: FALTAN LOS DE TRABAJADOR, CUANDO TENGAMOS UNO DE PRUEBA
    }

/*
    @Test
    public void sacarHorarioDeEspacio(){
        Localizacion l = new Localizacion("CRE.1065.00.020",10,15,1);
        CeldaHorario lunes = new CeldaHorario(l.getIdEspacio(),"Lunes",8,"Actividad1");
        CeldaHorario m = new CeldaHorario(l.getIdEspacio(),"Martes",15,"Actividad2");
        CeldaHorario x = new CeldaHorario(l.getIdEspacio(),"Miercoles",16,"Actividad3");
        CeldaHorario j = new CeldaHorario(l.getIdEspacio(),"Jueves",10,"Actividad4");
        CeldaHorario v = new CeldaHorario(l.getIdEspacio(),"Viernes",12,"Actividad5");
        ArrayList<CeldaHorario> celdaLunes = new ArrayList<CeldaHorario>();celdaLunes.add(lunes);
        ArrayList<CeldaHorario> celdaMartes = new ArrayList<CeldaHorario>();celdaMartes.add(m);
        ArrayList<CeldaHorario> celdaMiercoles = new ArrayList<CeldaHorario>();celdaMiercoles.add(x);
        ArrayList<CeldaHorario> celdaJueves = new ArrayList<CeldaHorario>();celdaJueves.add(j);
        ArrayList<CeldaHorario> celdaViernes = new ArrayList<CeldaHorario>();celdaViernes.add(v);
        Horario h = new Horario(l.getIdEspacio(),celdaLunes,celdaMartes,celdaMiercoles
                ,celdaJueves,celdaViernes);
        int incidencia1 = incidenciaRepositorio.addIncidencia("Nuevo","Test","PENDIENTE","3",l);
        horarioRepositorio.addActividadAlHorario(l.getIdEspacio(),"Lunes",8,"Actividad1");
        horarioRepositorio.addActividadAlHorario(l.getIdEspacio(),"Martes",15,"Actividad2");
        horarioRepositorio.addActividadAlHorario(l.getIdEspacio(),"Miercoles",16,"Actividad3");
        horarioRepositorio.addActividadAlHorario(l.getIdEspacio(),"Jueves",10,"Actividad4");
        horarioRepositorio.addActividadAlHorario(l.getIdEspacio(),"Viernes",12,"Actividad5");
        Incidencia i = incidenciaRepositorio.findIncidenciaByID(String.valueOf(incidencia1));
        Horario comprarar = horarioRepositorio.horarioDeEspacioDeIncidencia(i.getLocalizacion().getIdEspacio());
        assertTrue(h.equals(comprarar));
        horarioRepositorio.deleteActividadDelHorario(l.getIdEspacio(),"Lunes",8);
        horarioRepositorio.deleteActividadDelHorario(l.getIdEspacio(),"Martes",15);
        horarioRepositorio.deleteActividadDelHorario(l.getIdEspacio(),"Miercoles",16);
        horarioRepositorio.deleteActividadDelHorario(l.getIdEspacio(),"Jueves",10);
        horarioRepositorio.deleteActividadDelHorario(l.getIdEspacio(),"Viernes",12);
    }*/

}
