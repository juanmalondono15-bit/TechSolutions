package com.techsolutions.modelo;

import java.util.Set;

/**
 * Instalacion y configuracion de redes.
 *
 * <p>Costo base definido por TechSolutions: <b>$180.000</b>.</p>
 *
 * <p><b>Regla de negocio:</b> requiere atencion presencial con un tecnico de la
 * especialidad {@link Especialidad#REDES}, ya que el trabajo se realiza en las
 * instalaciones del cliente.</p>
 */
public class InstalacionRedes extends ServicioTecnico {

    /**
     * @param codigo      identificador unico del servicio
     * @param nombre      nombre visible
     * @param descripcion descripcion del servicio
     * @param costoBase   costo del servicio (sugerido: 180000)
     */
    public InstalacionRedes(String codigo, String nombre, String descripcion, double costoBase) {
        super(codigo, nombre, descripcion, costoBase, Set.of(Especialidad.REDES));
    }

    /** La instalacion de redes siempre se hace en el sitio del cliente. */
    @Override
    public boolean requiereAtencionPresencial() {
        return true;
    }
}
