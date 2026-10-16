package com.techsolutions.excepciones;

/**
 * Se lanza cuando una solicitud requiere atencion presencial y la plataforma
 * externa informa que no hay tecnicos disponibles para alguna de las
 * especialidades necesarias.
 *
 * <p>Con esta excepcion el sistema cumple la regla: <i>"si una solicitud
 * requiere atencion presencial y no existen tecnicos disponibles, el sistema
 * debera informar esta situacion y evitar su confirmacion"</i>.</p>
 */
public class TecnicoNoDisponibleException extends TechSolutionsException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje descripcion de las especialidades sin tecnico disponible
     */
    public TecnicoNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
