package src;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.*;

import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})

public class IncidenceServiceTest {

    @Autowired
    protected IncidenciaRepository incidenciaRepository;

    @Autowired
    protected EspacioRepository espacioRepository;

    @Test
    public void addIncidenciaTest() {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "Prueba", "desc", "PENDIENTE","3","3",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);
        assert(!idIncidencia.equals(""));
        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void buscarIncidenciaNoExisteTest() {
        assert(incidenciaRepository.findIncidenciaByID("-1") == null);
    }

    @Test
    public void buscarIncidenciaExisteTest() {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "Prueba", "desc", "PENDIENTE","3","3",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);
        assert(incidenciaRepository.findIncidenciaByID(idIncidencia) != null);
        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void borrarIncidenciaNoExisteTest() {
        assert(!incidenciaRepository.deleteIncidenciaByID("-1"));
    }

    @Test
    public void borrarIncidenciaExisteTest() {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "Prueba", "desc", "PENDIENTE","3","3",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);
        assert(incidenciaRepository.deleteIncidenciaByID(idIncidencia));
    }

    @Test
    public void updateIncidenciaNoExisteTest() {

        assert(!incidenciaRepository.updateIncidenciaByID(new Incidencia("-1",null,null,
            null,null,null,null)));
    }

    @Test
    public void updateIncidenciaExisteTest() {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "Prueba", "desc", "PENDIENTE","3","3",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);
        Incidencia nueva = new Incidencia(idIncidencia,"NUEVO TITULO","NUEVA DESC",i.getEstado(),
            i.getIdUsuario(),i.getIdTrabajador(),i.getLocalizacion());
        assert(incidenciaRepository.updateIncidenciaByID(nueva));
        i = incidenciaRepository.findIncidenciaByID(nueva.getId());
        assert(i.getTitulo().equals(nueva.getTitulo()) && i.getDesc().equals(nueva.getDesc()));
        incidenciaRepository.deleteIncidenciaByID(i.getId());
    }

     @Test
    public void sacarIncidenciasdeTrabajador_existe() {
        Localizacion loc = new Localizacion("CRE.1065.00.020","10",15,1,"S00");
        Incidencia incidencia;
        ArrayList<String> ids = new ArrayList<>();
        for( int i = 0; i < 5 ; i++){
            incidencia = new Incidencia(String.valueOf(i) ,"Test_trabajador" + i,"TEST_TRABAJADOR","PENDIENTE","2","999",loc);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            Incidencia incidencia2 = new Incidencia(id ,"Test_trabajador" + i,"TEST_TRABAJADOR","PENDIENTE","2","999",loc);
            System.out.println(incidenciaRepository.aceptadaToAsignada(incidencia2));
            ids.add(id);
        }


        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByTrabajador("999");



         System.out.println(listaIncidencias.size());

         for(int i = 0; i<ids.size(); i++) {

             Boolean existeInicidencia = false;
             for ( Incidencia inci : listaIncidencias) {
                 if (inci.getId().equals(ids.get(i))) {
                     existeInicidencia = true;
                 }
             }
             //assertTrue(existeInicidencia);
             System.out.println("Existe incidencia: "+existeInicidencia);
         }

         for(Incidencia laIncidencia : listaIncidencias) incidenciaRepository.deleteIncidenciaByID(laIncidencia.getId());
    }

    @Test
    public void sacarIncidenciasdeTrabajador_noexiste() {
        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByTrabajador("-1");
        assertTrue(listaIncidencias.size() == 0);
    }

    @Test
    public void cambioDeEstadoTest() {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "Prueba", "desc", "PENDIENTE","1","99",l);
        String id = incidenciaRepository.addIncidenciaTest(i);
        i = new Incidencia(id, "Prueba", "desc", "PENDIENTE","1","99",l);
        assert(incidenciaRepository.pendienteToIncompleta(i));
        assert(incidenciaRepository.incompletaToPendiente(i));
        assert(incidenciaRepository.pendienteToAceptada(i));
        assert(incidenciaRepository.aceptadaToAsignada(i));
        assert(incidenciaRepository.asignadaToAceptada(i,i.getIdTrabajador()));
        assert(incidenciaRepository.asignadaToFinalizada(i));
        assert(incidenciaRepository.pendienteToRechazada(i));
        // TODO: FALTAN LOS DE TRABAJADOR, CUANDO TENGAMOS UNO DE PRUEBA
        incidenciaRepository.deleteIncidenciaByID(id);
    }

    @Test
    public void sacarHorarioDeEspacio(){
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"S00");
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
        Incidencia i = new Incidencia("00","TEST_HIORARIOS","DES","PENDIENTE","3",null,l);
        String id = incidenciaRepository.addIncidenciaTest(i);
        espacioRepository.addActividadAlHorario(l.getIdEspacio(),"Lunes",8,"Actividad1");
        espacioRepository.addActividadAlHorario(l.getIdEspacio(),"Martes",15,"Actividad2");
        espacioRepository.addActividadAlHorario(l.getIdEspacio(),"Miercoles",16,"Actividad3");
        espacioRepository.addActividadAlHorario(l.getIdEspacio(),"Jueves",10,"Actividad4");
        espacioRepository.addActividadAlHorario(l.getIdEspacio(),"Viernes",12,"Actividad5");
        Incidencia in = incidenciaRepository.findIncidenciaByID(id);
        Horario comprarar = espacioRepository.horarioDeEspacioDeIncidencia(in.getLocalizacion().getIdEspacio());
        assertTrue(h.equals(comprarar));
        espacioRepository.deleteActividadDelHorario(l.getIdEspacio(),"Lunes",8);
        espacioRepository.deleteActividadDelHorario(l.getIdEspacio(),"Martes",15);
        espacioRepository.deleteActividadDelHorario(l.getIdEspacio(),"Miercoles",16);
        espacioRepository.deleteActividadDelHorario(l.getIdEspacio(),"Jueves",10);
        espacioRepository.deleteActividadDelHorario(l.getIdEspacio(),"Viernes",12);
        incidenciaRepository.deleteIncidenciaByID(id);
    }

    @Test
    public void sacarIncidenciasdeUser(){
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"S00");
        String username = "prueba";
        Incidencia incidencia;
        ArrayList<String> ids = new ArrayList<>();
        for( int i = 0; i < 5 ; i++){
            incidencia = new Incidencia(String.valueOf(i) ,"Test_user" + i,"TEST_user","PENDIENTE",username,"99",l);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            Incidencia incidencia2 = new Incidencia(id ,"Test_user" + i,"TEST_user","PENDIENTE",username,"99",l);
            //System.out.println(incidenciaRepository.aceptadaToAsignada(incidencia2));
            ids.add(id);
        }
        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByUser(username);
        for(int i = 0; i < 5 ; i++) System.out.println(listaIncidencias.get(i).getTitulo());
        assertTrue(listaIncidencias.size() >=  5);
        for(String elId : ids) incidenciaRepository.deleteIncidenciaByID(elId);

    }

    @Test
    public void sacarIncidenciasdelEspacio(){
        Localizacion l = new Localizacion(null,"CRE.1201.03.340",15,1,"S00");

        String username = "prueba";
        Incidencia incidencia;
        ArrayList<String> ids = new ArrayList<>();
        for( int i = 0; i < 5 ; i++){
            incidencia = new Incidencia(String.valueOf(i) ,"Test_espacio" + i,"TEST_espacio","ACEPTADA",username,"99",l);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            ids.add(id);
        }
        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByEspacio(l.getIdEspacio());
        for(int i = 0; i < 5 ; i++) System.out.println(listaIncidencias.get(i).getTitulo());
        assertTrue(listaIncidencias.size() >=  5);
        for(String elId : ids) incidenciaRepository.deleteIncidenciaByID(elId);

    }

    @Test
    public void sacarIncidenciasActivas(){
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"S00");
        String username = "prueba";
        Incidencia incidencia;
        ArrayList<String> ids = new ArrayList<>();
        for( int i = 0; i < 5 ; i++){
            incidencia = new Incidencia(String.valueOf(i) ,"Test_Aceptadas" + i,"TEST_Aceptadas","PENDIENTE",username,"99",l);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            Incidencia incidencia2 = new Incidencia(id ,"Test_Aceptadas" + i,"TEST_Aceptadas","ACEPTADA",username,"99",l);
            System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));
            ids.add(id);
        }
        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasAceptadas();
        System.out.println(listaIncidencias.size());

        for(int i = 0; i<ids.size(); i++) {

            Boolean existeInicidencia = false;
            for ( Incidencia inci : listaIncidencias) {
                if (inci.getId().equals(ids.get(i))) {
                    existeInicidencia = true;
                }
            }
            assertTrue(existeInicidencia);
        }

        for(Incidencia laIncidencia : listaIncidencias) incidenciaRepository.deleteIncidenciaByID(laIncidencia.getId());

    }

    @Test
    public void sacarTodasIncidencias() {

        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidencias();
        Integer numeroIncidencias = listaIncidencias.size();

        System.out.println(numeroIncidencias);

        Localizacion loc = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia inci = new Incidencia("01", "Prueba", "desc", "PENDIENTE","3","3",loc);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(inci);

        assertTrue(incidenciaRepository.findAllIncidencias().size()==numeroIncidencias+1);

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void aceptarIncidencia() {
        Localizacion loc = new Localizacion("CRE.1065.00.020","10",15,1,"S00");
        String username = "prueba";
        Incidencia incidencia;

        incidencia = new Incidencia(String.valueOf(33) ,"Test_Aceptada","TEST_Aceptada","PENDIENTE",username,"99",loc);
        String id = incidenciaRepository.addIncidenciaTest(incidencia);
        incidencia = new Incidencia(id ,"Test_Aceptada","TEST_Aceptada","ACEPTADA",username,"99",loc);
        System.out.println("Aceptada: "+incidenciaRepository.pendienteToAceptada(incidencia));

        ArrayList<Incidencia> incidenciasAceptadas = incidenciaRepository.findAllIncidenciasAceptadas();
        Boolean incidenciaEstaAceptada = false;

        for (Incidencia inci : incidenciasAceptadas) {
            if (inci.getId().equals(id)) incidenciaEstaAceptada = true;
        }
        assertTrue(incidenciaEstaAceptada);

        incidenciaRepository.deleteIncidenciaByID(id);


    }


}
