package com.techsolutions.modelo;

/**
 * Tecnico especializado reportado por la plataforma externa {@code LegacySupport}.
 *
 * <p>Es la <b>informacion estructurada</b> que necesita el sistema: el
 * proveedor externo entrega una cadena de texto con barras
 * ({@code "TEC-105|DISPONIBLE|30000"}) y el adaptador la convierte en este
 * objeto, de modo que el resto del sistema consulte campos con nombre y no
 * tenga que interpretar textos.</p>
 */
public class TecnicoEspecializado {

    private final String identificador;
    private final Especialidad especialidad;
    private final boolean disponible;
    private final double costoDesplazamiento;

    /**
     * @param identificador       identificador del tecnico (por ejemplo TEC-105)
     * @param especialidad        especialidad que atiende
     * @param disponible          {@code true} si puede atender el servicio
     * @param costoDesplazamiento costo de desplazamiento reportado por el proveedor
     */
    public TecnicoEspecializado(String identificador, Especialidad especialidad,
                                boolean disponible, double costoDesplazamiento) {
        this.identificador = identificador;
        this.especialidad = especialidad;
        this.disponible = disponible;
        this.costoDesplazamiento = costoDesplazamiento;
    }

    public String getIdentificador() {
        return identificador;
    }

    public Especialidad getEspecialidad() {
        return especialidad;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public double getCostoDesplazamiento() {
        return costoDesplazamiento;
    }

    @Override
    public String toString() {
        return identificador + " (" + especialidad.getNombreVisible() + ")"
                + (disponible ? " - disponible" : " - no disponible");
    }
}
