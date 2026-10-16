package com.techsolutions.modelo.complemento;

import com.techsolutions.modelo.ComponenteServicio;

/**
 * Caracteristica complementaria <b>Atencion prioritaria</b>.
 *
 * <p>Incremento definido por TechSolutions: <b>$35.000</b>.</p>
 *
 * <p>Hace que la solicitud se atienda antes que las demas; no cambia la
 * necesidad de tecnico presencial del servicio contratado.</p>
 */
public class AtencionPrioritaria extends ComplementoServicio {

    /** Incremento del complemento en pesos colombianos. */
    public static final double INCREMENTO = 35_000;

    /** Nombre con el que se identifica el complemento. */
    public static final String NOMBRE = "Atencion prioritaria";

    /**
     * @param servicioContratado servicio o paquete al que se le agrega el complemento
     */
    public AtencionPrioritaria(ComponenteServicio servicioContratado) {
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
