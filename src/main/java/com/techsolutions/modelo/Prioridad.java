package com.techsolutions.modelo;

/**
 * Nivel de prioridad de una solicitud. Es un dato <b>opcional</b>: si el
 * usuario no lo elige se usa {@link #MEDIA}.
 */
public enum Prioridad {

    /** Se atiende despues de las solicitudes de prioridad media y alta. */
    BAJA("Baja"),

    /** Prioridad por defecto de una solicitud. */
    MEDIA("Media"),

    /** Se atiende antes que las demas solicitudes. */
    ALTA("Alta");

    private final String nombreVisible;

    Prioridad(String nombreVisible) {
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
