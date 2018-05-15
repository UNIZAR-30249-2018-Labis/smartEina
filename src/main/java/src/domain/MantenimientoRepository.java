package src.domain;


import java.util.ArrayList;

public interface MantenimientoRepository {

    CeldaMantenimiento findCeldaMantenimientoByIDs(String idTrabajador, String idIncidencia);

    ArrayList<CeldaMantenimiento> findAllCeldasMantenimientoByTrabajador(String idTrabajador);

    ArrayList<CeldaMantenimiento> findAllCeldasMantenimientoByIdEspacio(String idEspacio);

    boolean addCeldaMantenimiento(CeldaMantenimiento celda);

    boolean deleteCeldaMantenimiento(CeldaMantenimiento celda);
}
