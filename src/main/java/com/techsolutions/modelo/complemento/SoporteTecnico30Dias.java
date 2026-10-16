package com.techsolutions.modelo.complemento;

import com.techsolutions.modelo.ComponenteServicio;

/**
 * Caracteristica complementaria <b>Soporte tecnico durante 30 dias</b>.
 *
 * <p>Incremento definido por TechSolutions: <b>$50.000</b>.</p>
 */
public class SoporteTecnico30Dias extends ComplementoServicio {

    /** Incremento del complemento en pesos colombianos. */
    public static final double INCREMENTO = 50_000;

    /** Nombre con el que se identifica el complemento. */
    public static final String NOMBRE = "Soporte tecnico 30 dias";

    /**
     * @param servicioContratado servicio o paquete al que se le agrega el complemento
     */
    public SoporteTecnico30Dias(ComponenteServicio servicioContratado) {
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
