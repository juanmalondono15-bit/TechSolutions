package com.techsolutions.modelo;

import java.util.Set;

/**
 * Mantenimiento preventivo de equipos de computo.
 *
 * <p>Costo base definido por TechSolutions: <b>$80.000</b>.</p>
 *
 * <p><b>Regla de negocio:</b> este servicio se presta de forma remota
 * (teleasistencia y revision programada), por lo que <i>no</i> exige la
 * presencia de un tecnico en las instalaciones del cliente. Por esa razon no
 * declara especialidades requeridas y el sistema no consulta disponibilidad de
 * tecnicos cuando solo se contrata este servicio.</p>
 */
public class MantenimientoPreventivo extends ServicioTecnico {

    /**
     * @param codigo      identificador unico del servicio
     * @param nombre      nombre visible
     * @param descripcion descripcion del servicio
     * @param costoBase   costo del servicio (sugerido: 80000)
     */
    public MantenimientoPreventivo(String codigo, String nombre, String descripcion, double costoBase) {
        // Sin especialidades: no requiere tecnico presencial.
        super(codigo, nombre, descripcion, costoBase, Set.of());
    }
}
