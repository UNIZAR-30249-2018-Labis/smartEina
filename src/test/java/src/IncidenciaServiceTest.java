package src;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import src.application.controllers.IncidenceService;
import src.domain.*;

import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})

public class IncidenceServiceTest {

    @Autowired
    protected IncidenciaRepository incidenciaRepository;

    @Autowired
    protected MantenimientoRepository mantenimientoRepository;

    @Autowired
    protected EspacioRepository espacioRepository;

    @Autowired
    protected IncidenceService incidenceService;

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
            incidencia = new Incidencia(String.valueOf(i) ,"Test_trabajador" + i,"TEST_TRABAJADOR","PENDIENTE","2","worker1",loc);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            Incidencia incidencia2 = new Incidencia(id ,"Test_trabajador" + i,"TEST_TRABAJADOR","PENDIENTE","2","worker1",loc);
            System.out.println(incidenciaRepository.aceptadaToAsignada(incidencia2));
            ResponseEntity<String> response = incidenceService.asignarIncidencia(id,"worker1","Martes", String.valueOf(9));
            ids.add(id);
        }

        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByTrabajador("worker1");

        for(int i = 0; i<ids.size(); i++) {


            Boolean existeInicidencia = false;
            for ( Incidencia inci : listaIncidencias) {


                if (inci.getId().equals(ids.get(i))) {
                    existeInicidencia = true;
                }
            }
            assertTrue(existeInicidencia);
            System.out.println("Existe incidencia: "+existeInicidencia);
        }

        for( String id : ids) {
            CeldaMantenimiento celda = mantenimientoRepository.findCeldaMantenimientoByIDs("worker1", id);
            mantenimientoRepository.deleteCeldaMantenimiento(celda);
            incidenciaRepository.deleteIncidenciaByID(id);
        }
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
        Horario comparar = espacioRepository.horarioDeEspacioDeIncidencia(in.getLocalizacion().getIdEspacio());
        assertTrue(h.equals(comparar));

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
        String username = "testUsuario";
        Incidencia incidencia;
        ArrayList<String> ids = new ArrayList<>();
        for( int i = 0; i < 5 ; i++){
            incidencia = new Incidencia(String.valueOf(i) ,"Test_user" + i,"TEST_user","PENDIENTE",username,"99",l);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            Incidencia incidencia2 = new Incidencia(id ,"Test_user" + i,"TEST_user","PENDIENTE",username,"99",l);

            ids.add(id);
        }

        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByUser(username);

        for(int i = 0; i < 5 ; i++) System.out.println(listaIncidencias.get(i).getTitulo());

        for(int i = 0; i<ids.size(); i++) {
            Boolean existeInci = false;

            for ( Incidencia inci : listaIncidencias) {
                if (inci.getId().equals(ids.get(i))) {

                    existeInci = true;
                }
            }
            assertTrue(existeInci);
        }

        for(String elId : ids) incidenciaRepository.deleteIncidenciaByID(elId);

    }

    @Test
    public void sacarIncidenciasdelEspacio(){
        Localizacion l = new Localizacion(null,"CRE.1201.03.340",15,1,"S00");

        String username = "TestEspacio";
        Incidencia incidencia;
        ArrayList<String> ids = new ArrayList<>();
        for( int i = 0; i < 5 ; i++){
            incidencia = new Incidencia(String.valueOf(i) ,"Test_espacio" + i,"TEST_espacio","ACEPTADA",username,"99",l);
            String id = incidenciaRepository.addIncidenciaTest(incidencia);
            ids.add(id);
        }
        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasByEspacio(l.getIdEspacio());


        for(int i = 0; i < 5 ; i++) System.out.println(listaIncidencias.get(i).getTitulo());

        for(int i = 0; i<ids.size(); i++) {

            Boolean existeInicidencia = false;
            for ( Incidencia inci : listaIncidencias) {
                if (inci.getId().equals(ids.get(i))) {
                    existeInicidencia = true;
                }
            }
            assertTrue(existeInicidencia);
        }

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

        for(String idIncidencia : ids) incidenciaRepository.deleteIncidenciaByID(idIncidencia);

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

    @Test
    public void rechazarIncidencia() {
        Localizacion loc = new Localizacion("CRE.1065.00.020","10",15,1,"S00");
        String username = "prueba";
        Incidencia incidencia;

        incidencia = new Incidencia(String.valueOf(55) ,"Test_Rechazada","TEST_Rechazada","PENDIENTE",username,"99",loc);
        String id = incidenciaRepository.addIncidenciaTest(incidencia);

        ArrayList<Incidencia> incidenciasCreadas = incidenciaRepository.findAllIncidenciasCreadas();

        Boolean incidenciaEstaCreada = false;

        for (Incidencia inci : incidenciasCreadas) {
            if (inci.getId().equals(id)) incidenciaEstaCreada = true;
        }
        assertTrue(incidenciaEstaCreada);

        incidencia = new Incidencia(id ,"Test_Rechazada","TEST_Rechazada","RECHAZADA",username,"99",loc);
        System.out.println("Rechazada: "+incidenciaRepository.pendienteToRechazada(incidencia));

        //La hemos rechazado, ya no debe de aparecer
        incidenciasCreadas = incidenciaRepository.findAllIncidenciasCreadas();

        incidenciaEstaCreada = false;

        for (Incidencia inci : incidenciasCreadas) {
            if (inci.getId().equals(id)) incidenciaEstaCreada = true;
        }
        assertFalse(incidenciaEstaCreada);

        incidenciaRepository.deleteIncidenciaByID(id);
    }

    ///////////////////////////
    ////TEST DE CONTROLLERS////
    ///////////////////////////

    @Test
    public void testGetIncidencia() throws JSONException {

        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "PruebaGetIncidencia", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idIncidencia")).thenReturn(idIncidencia);

        ResponseEntity<String> response = incidenceService.getIncidencia(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void testGetIncidenciasUsuario() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idUser")).thenReturn("0");

        ResponseEntity<String> response = incidenceService.getIncidenciasUsuario(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testGetIncidenciasTrabajador() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idTrabajador")).thenReturn("0");

        ResponseEntity<String> response = incidenceService.getIncidenciasTrabajador(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testGetAllIncidencias() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        ResponseEntity<String> response = incidenceService.getAllIncidencias(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

    }

    @Test
    public void testGetIncidenciasEspacio() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idEspacio")).thenReturn("0");

        ResponseEntity<String> response = incidenceService.getIncidenciasEspacio(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testGetIncidenciasEspacioAceptadas() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idEspacio")).thenReturn("0");

        ResponseEntity<String> response = incidenceService.getIncidenciasEspacioAceptadas(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testGetIncidenciasActivasYAsignadas() throws JSONException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        ResponseEntity<String> response = incidenceService.getIncidenciasActivasYAsignadas(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testGetIncidenciasActivas() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        ResponseEntity<String> response = incidenceService.getIncidenciasActivas(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testGetIncidenciasCreadas() throws JSONException {

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);

        ResponseEntity<String> response = incidenceService.getIncidenciasCreadas(mockRequest);

        String expected = "\"Exito obteniendo incidencias\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());
    }

    @Test
    public void testCrearIncidencia() throws JSONException {

        ResponseEntity<String> response = incidenceService.crearIncidencia("titulo", "descripcion",
                "idUsuarioTestCrearIncidencia", 33.3f, 33.3f, "plantaPrimera", "CRE.1065.00.020");

        String expected = "\"Se ha añadido la incidencia\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        ArrayList<Incidencia> listaIncidencias = incidenciaRepository.findAllIncidenciasCreadas();

        Boolean incidenciaEncontrada = false;
        int i = 0;

        while (!incidenciaEncontrada && i<listaIncidencias.size()) {
            if(listaIncidencias.get(i).getTitulo().equals("titulo") && listaIncidencias.get(i).getDesc().equals("descripcion")
                    && listaIncidencias.get(i).getEstado().equals("PENDIENTE")) {
                incidenciaEncontrada = true;
            } else {
                i++;
            }
        }

        incidenciaRepository.deleteIncidenciaByID(listaIncidencias.get(i).getId());

    }

    @Test
    public void testUpdateIncidencia() throws JSONException {

        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "PruebaUpdateIncidenciaController", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        ResponseEntity<String> response = incidenceService.updateIncidencia("TituloNuevo", "DescripcionNueva",
                idIncidencia);

        String expected = "\"Se ha modificado la incidencia correctamente\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void testAsignarIncidencia() throws JSONException {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "PruebaAsignarIncidencia", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        Incidencia incidencia2 = new Incidencia(idIncidencia ,"PruebaAsignarIncidencia","desc","ACEPTADA","prueba","worker1",l);
        System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));


        ResponseEntity<String> response = incidenceService.asignarIncidencia(idIncidencia,"worker1","Lunes","11");

        String expected = "\"El estado de la incidencia ha sido actualizado y se ha creado la celda\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        CeldaMantenimiento celda = mantenimientoRepository.findCeldaMantenimientoByIDs("worker1", idIncidencia);
        mantenimientoRepository.deleteCeldaMantenimiento(celda);
        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void testDesAsignarIncidencia() throws JSONException {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "PruebaAsignarIncidencia", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        Incidencia incidencia2 = new Incidencia(idIncidencia ,"PruebaAsignarIncidencia","desc","ACEPTADA","prueba","worker1",l);
        System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));


        ResponseEntity<String> response = incidenceService.asignarIncidencia(idIncidencia,"worker1","Lunes","11");

        response = incidenceService.desAsignarIncidencia(idIncidencia,"worker1");

        String expected = "\"El estado de la incidencia ha sido actualizado y la celda borrada\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void testPendientarIncidencia() throws JSONException {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "PruebaPendientarIncidencia", "desc", "INCOMPLETA","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        ResponseEntity<String> response = incidenceService.pendientarIncidencia(idIncidencia);

        String expected = "\"El estado de la incidencia ha sido actualizado\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }


    /*
    ES NECESARIO EL SERVIDOR SMTP DE ENVÍO DE EMAILS PARA EJECUTAR EL TEST CORRECTAMENTE

    @Test
    public void testTerminarIncidencia() throws JSONException {

        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "PruebaTerminarIncidencia", "FalloEmail", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        Incidencia incidencia2 = new Incidencia(idIncidencia ,"PruebaTerminarIncidencia","FalloEmail","ACEPTADA","prueba","worker1",l);
        System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));


        ResponseEntity<String> response = incidenceService.asignarIncidencia(idIncidencia,"worker1","Lunes","11");

        response = incidenceService.terminarIncidencia(idIncidencia);

        String expected = "\"El estado de la incidencia ha sido actualizado\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);

    }
    */

    // TODO Test aceptarIncidencia(), es necesario el servidor SMTP de envío de correo
    // TODO Test rechazarIncidencia(), es necesario el servidor SMTP de envío de correo
    // TODO Test incompletarIncidencia(), es necesario el servidor SMTP de envío de correo

}