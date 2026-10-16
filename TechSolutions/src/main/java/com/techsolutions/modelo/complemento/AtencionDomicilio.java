package com.techsolutions.modelo.complemento;

import com.techsolutions.modelo.ComponenteServicio;

/**
 * Caracteristica complementaria <b>Atencion a domicilio</b>.
 *
 * <p>Incremento definido por TechSolutions: <b>$25.000</b>.</p>
 *
 * <p>Representa el cargo por el traslado programado del tecnico hasta el sitio
 * del cliente. El costo de desplazamiento que reporta la plataforma externa
 * {@code LegacySupport} se contabiliza una sola vez por solicitud, de modo que
 * este complemento nunca produce cargos duplicados.</p>
 */
public class AtencionDomicilio extends ComplementoServicio {

    /** Incremento del complemento en pesos colombianos. */
    public static final double INCREMENTO = 25_000;

    /** Nombre con el que se identifica el complemento. */
    public static final String NOMBRE = "Atencion a domicilio";

    /**
     * @param servicioContratado servicio o paquete al que se le agrega el complemento
     */
    public AtencionDomicilio(ComponenteServicio servicioContratado) {
        super(servicioContratado);
    }

    @Override
    public String getNombreComplemento() {
        return NOMBRE;
    }

    @Override
    public double getIncremento() {
        return INCREMENTO;
    }
}
