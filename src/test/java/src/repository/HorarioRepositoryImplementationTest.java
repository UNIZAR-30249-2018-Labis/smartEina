package src.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.HorarioOV;
import src.repository.HorarioRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest

public class HorarioRepositoryImplementationTest {

    @Autowired
    protected HorarioRepository horarioRepository;

    //TODO Hacer test bien, necesario añadir y eliminar de la bd con los datos testeados en el propio test, no suponer que ya existen o no en la BD

    @Test
    public void addHorario() {
        assertTrue(horarioRepository.addHorario(new HorarioOV("EspacioTest","Domingo",99,"ActividadTest")));
        assertFalse(horarioRepository.addHorario(new HorarioOV("EspacioTest","Domingo",99,"ActividadTest")));
        assertTrue(horarioRepository.addHorario(new HorarioOV("Cocina","Domingo",99,"ActividadTest")));
    }

    @Test
    public void estaEspacioLibre() {
        assertTrue(horarioRepository.estaEspacioLibre("Cocina","Domingo",2));
        assertFalse(horarioRepository.estaEspacioLibre("Cocina","Domingo",99));
    }

    @Test
    public void deleteHorario() {
        assertTrue(horarioRepository.deleteHorario("EspacioTest","Domingo",99));
        assertTrue(horarioRepository.deleteHorario("Cocina","Domingo",99));
        assertFalse(horarioRepository.deleteHorario("EspacioTest","Domingo",1234));
    }

    @Test
    public void viewHorario() {

        for (int i = 0; i<3; i++) {
            horarioRepository.addHorario(new HorarioOV("EspacioTest","Domingo",i,"ActividadTest"));
        }

        List <HorarioOV> horarios = horarioRepository.viewHorario("EspacioTest","Domingo");

        for (int i = 0; i<3; i++) {
            assertEquals("EspacioTest",horarios.get(i).getIdEspacio());
            assertEquals("Domingo",horarios.get(i).getDia());
            assertEquals(i,horarios.get(i).getHoraInicio());
        }

        for (int i = 0; i<3; i++) {
            horarioRepository.deleteHorario("EspacioTest","Domingo",i);
        }

    }

    @Test
    public void viewHorarioLibre() {

        //Añade horarios ocupados
        for (int hora = 10; hora<15; hora++) {
            horarioRepository.addHorario(new HorarioOV("EspacioTest","Domingo",hora,"Test"));
        }

        List <Integer> horasLibresCorrectas = new ArrayList<>();

        //Crea una lista rellena con las horas que quedan libres
        for (int hora = 7; hora < 21; hora++) {
            if (hora < 10 || hora >= 15) {
                horasLibresCorrectas.add(hora);
            }
        }

        List <Integer> horasLibres = horarioRepository.viewHorarioLibre("EspacioTest","Domingo");

        //Comprueba que las horas libres que devuelve viewHorarioLibre() son realmente las que existen y solo son esas
        for (Integer horaLibre : horasLibres) {
            assertTrue(horasLibresCorrectas.contains(new Integer(horaLibre)));
            horasLibresCorrectas.remove(new Integer(horaLibre));
        }
        assertTrue(horasLibresCorrectas.isEmpty());

        //Elimina horarios ocupados
        for (int hora = 10; hora<15; hora++) {
            horarioRepository.deleteHorario("EspacioTest","Domingo",hora);
        }
    }
}
