package com.techsolutions.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resultado de consultar los tecnicos requeridos por una solicitud.
 *
 * <p>Agrupa toda la informacion de la consulta al proveedor externo:</p>
 * <ul>
 *   <li>los tecnicos encontrados (uno por especialidad),</li>
 *   <li>el <b>unico</b> costo de desplazamiento que se cobrara a la solicitud,</li>
 *   <li>y un mensaje de texto listo para mostrar al usuario.</li>
 * </ul>
 *
 * <p><b>Regla de cargos duplicados:</b> sin importar cuantos servicios
 * presenciales tenga la solicitud ni cuantas especialidades requiera, el
 * desplazamiento se contabiliza una sola vez: se toma el valor mas alto
 * reportado por el proveedor.</p>
 */
public class ConsultaTecnicos {

    private final List<TecnicoEspecializado> tecnicos;
    private final double costoDesplazamiento;
    private final boolean todosDisponibles;
    private final String mensaje;

    /**
     * @param tecnicos            tecnicos consultados (uno por especialidad)
     * @param costoDesplazamiento costo unico de desplazamiento que se cobrara
     * @param todosDisponibles    {@code true} si todas las especialidades tienen tecnico
     * @param mensaje             texto descriptivo del resultado
     */
    public ConsultaTecnicos(List<TecnicoEspecializado> tecnicos, double costoDesplazamiento,
                            boolean todosDisponibles, String mensaje) {
        this.tecnicos = new ArrayList<>(tecnicos);
        this.costoDesplazamiento = costoDesplazamiento;
        this.todosDisponibles = todosDisponibles;
        this.mensaje = mensaje;
    }

    /** @return lista inmutable con los tecnicos consultados */
    public List<TecnicoEspecializado> getTecnicos() {
        return Collections.unmodifiableList(tecnicos);
    }

    /** @return valor unico de desplazamiento a cobrar en la solicitud */
    public double getCostoDesplazamiento() {
        return costoDesplazamiento;
    }

    /** @return {@code true} si todos los tecnicos requeridos estan disponibles */
    public boolean isTodosDisponibles() {
        return todosDisponibles;
    }

    /** @return mensaje descriptivo para mostrar al usuario */
    public String getMensaje() {
        return mensaje;
    }
}
