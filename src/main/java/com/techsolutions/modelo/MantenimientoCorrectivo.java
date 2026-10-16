package com.techsolutions.modelo;

import java.util.Set;

/**
 * Mantenimiento correctivo de equipos de computo.
 *
 * <p>Costo base definido por TechSolutions: <b>$120.000</b>.</p>
 *
 * <p><b>Regla de negocio:</b> se realiza en las instalaciones del cliente, por
 * lo que requiere atencion presencial y un tecnico de la especialidad
 * {@link Especialidad#MANTENIMIENTO}. Antes de confirmar la solicitud el
 * sistema debe verificar que exista un tecnico disponible.</p>
 */
public class MantenimientoCorrectivo extends ServicioTecnico {

    /**
     * @param codigo      identificador unico del servicio
     * @param nombre      nombre visible
     * @param descripcion descripcion del servicio
     * @param costoBase   costo del servicio (sugerido: 120000)
     */
    public MantenimientoCorrectivo(String codigo, String nombre, String descripcion, double costoBase) {
        super(codigo, nombre, descripcion, costoBase, Set.of(Especialidad.MANTENIMIENTO));
    }

    /** Requiere que un tecnico asista al sitio donde esta el equipo. */
    @Override
    public boolean requiereAtencionPresencial() {
        return true;
    }
}
