package com.techsolutions.modelo;

/**
 * Estados por los que pasa una solicitud de servicio.
 */
public enum EstadoSolicitud {

    /**
     * La solicitud fue registrada con todos los datos obligatorios, pero aun
     * no se confirmo la prestacion del servicio.
     */
    REGISTRADA("Registrada"),

    /**
     * La solicitud fue confirmada. Si requeria atencion presencial, se verifico
     * previamente que existiera un tecnico disponible.
     */
    CONFIRMADA("Confirmada"),

    /**
     * No fue posible confirmar la solicitud (por ejemplo, no hay tecnico
     * disponible para una especialidad requerida).
     */
    NO_CONFIRMADA("No confirmada");

    private final String nombreVisible;

    EstadoSolicitud(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    @Override
    public String toString() {
        return nombreVisible;
    }
}
