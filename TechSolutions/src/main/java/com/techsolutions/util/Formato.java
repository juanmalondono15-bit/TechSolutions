package com.techsolutions.util;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Utilidades de presentacion.
 *
 * <p>Se centraliza aqui el formato de dinero y de fechas para no repetir el
 * mismo codigo en cada vista (principio de responsabilidad unica y DRY:
 * <i>Don't Repeat Yourself</i>).</p>
 */
public final class Formato {

    /** Formato de moneda colombiana: $ 80.000. */
    private static final NumberFormat MONEDA =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    /** Formato de fecha para mostrar en pantalla: 16/10/2026. */
    private static final DateTimeFormatter FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Clase de utilidades: no se instancia. */
    private Formato() {
    }

    /**
     * Da formato de moneda a un valor.
     *
     * @param valor valor en pesos colombianos
     * @return texto con separador de miles y simbolo de peso
     */
    public static String moneda(double valor) {
        return MONEDA.format(valor);
    }

    /**
     * Da formato de fecha a una fecha (acepta {@code null}).
     *
     * @param fecha fecha a mostrar
     * @return fecha en formato dd/MM/yyyy o "(sin definir)" si es nula
     */
    public static String fecha(LocalDate fecha) {
        return fecha == null ? "(sin definir)" : fecha.format(FECHA);
    }
}
