package com.techsolutions.modelo;

/**
 * Especialidades de tecnico que maneja TechSolutions.
 *
 * <p>Cada especialidad guarda dos datos:</p>
 * <ul>
 *   <li><b>codigoLegacy</b>: codigo numerico que exige la plataforma externa
 *       {@code LegacySupport}. El sistema nuevo trabaja con el nombre de la
 *       especialidad y el adaptador se encarga de traducirlo.</li>
 *   <li><b>nombreVisible</b>: texto que se muestra al usuario.</li>
 * </ul>
 */
public enum Especialidad {

    /** Tecnicos que atienden mantenimiento de equipos de computo. */
    MANTENIMIENTO(101, "Mantenimiento de equipos"),

    /** Tecnicos que instalan y configuran redes. */
    REDES(202, "Instalacion de redes"),

    /** Tecnicos que recuperan informacion de discos y servidores. */
    RECUPERACION_DATOS(303, "Recuperacion de informacion");

    private final int codigoLegacy;
    private final String nombreVisible;

    Especialidad(int codigoLegacy, String nombreVisible) {
        this.codigoLegacy = codigoLegacy;
        this.nombreVisible = nombreVisible;
    }

    /** @return codigo numerico entendido por la plataforma externa LegacySupport */
    public int getCodigoLegacy() {
        return codigoLegacy;
    }

    /** @return nombre de la especialidad para mostrar en pantalla */
    public String getNombreVisible() {
        return nombreVisible;
    }

    /** Se sobrescribe para que los {@code ComboBox} de JavaFX muestren el nombre. */
    @Override
    public String toString() {
        return nombreVisible;
    }
}
