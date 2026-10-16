package com.techsolutions.modelo;

import java.util.Set;

/**
 * Recuperacion de informacion de discos y servidores.
 *
 * <p>Costo base definido por TechSolutions: <b>$150.000</b>.</p>
 *
 * <p><b>Regla de negocio:</b> requiere un tecnico de la especialidad
 * {@link Especialidad#RECUPERACION_DATOS}. En la simulacion de la plataforma
 * externa esta especialidad figura como <i>no disponible</i>, lo que permite
 * demostrar que el sistema informa la situacion y evita confirmar la
 * solicitud cuando no hay tecnico.</p>
 */
public class RecuperacionInformacion extends ServicioTecnico {

    /**
     * @param codigo      identificador unico del servicio
     * @param nombre      nombre visible
     * @param descripcion descripcion del servicio
     * @param costoBase   costo del servicio (sugerido: 150000)
     */
    public RecuperacionInformacion(String codigo, String nombre, String descripcion, double costoBase) {
        super(codigo, nombre, descripcion, costoBase, Set.of(Especialidad.RECUPERACION_DATOS));
    }

    /** El tecnico debe manipular los equipos del cliente. */
    @Override
    public boolean requiereAtencionPresencial() {
        return true;
    }
}
