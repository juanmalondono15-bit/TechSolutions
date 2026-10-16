package com.techsolutions.integracion;

/**
 * Plataforma externa <b>LegacySupport</b>.
 *
 * <p><b>IMPORTANTE:</b> esta clase representa un sistema de un proveedor
 * externo, por lo que su implementacion <i>no se puede modificar</i>. Tiene dos
 * caracteristicas que el sistema nuevo no puede adoptar:</p>
 * <ul>
 *   <li>Recibe un <b>codigo numerico</b> de especialidad, mientras que el
 *       sistema nuevo trabaja con el <b>nombre</b> de la especialidad.</li>
 *   <li>Devuelve una <b>cadena de texto con barras</b>, por ejemplo
 *       {@code "TEC-105|DISPONIBLE|30000"} o
 *       {@code "SIN_TECNICO|NO_DISPONIBLE|0"}, mientras que el sistema nuevo
 *       necesita informacion estructurada.</li>
 * </ul>
 *
 * <p>Por eso se integra mediante el patron <b>Adapter</b>: ver
 * {@link AdaptadorLegacySupport}.</p>
 */
public class LegacySupport {

    /** Codigo de especialidad: mantenimiento de equipos (uso interno del legado). */
    private static final int CODIGO_MANTENIMIENTO = 101;

    /** Codigo de especialidad: instalacion de redes (uso interno del legado). */
    private static final int CODIGO_REDES = 202;

    /** Codigo de especialidad: recuperacion de informacion (uso interno del legado). */
    private static final int CODIGO_RECUPERACION = 303;

    /**
     * Consulta la disponibilidad de un tecnico en la plataforma externa.
     *
     * <p>El texto de respuesta siempre tiene tres campos separados por "|":
     * identificador del tecnico, estado y costo de desplazamiento.</p>
     *
     * @param codigoEspecialidad codigo numerico de la especialidad
     * @return cadena con el formato {@code ID|ESTADO|COSTO_DESPLAZAMIENTO}
     */
    public String consultarDisponibilidadTecnico(int codigoEspecialidad) {
        // La respuesta esta simulada: reproduce exactamente los ejemplos del enunciado.
        switch (codigoEspecialidad) {
            case CODIGO_MANTENIMIENTO:
                return "TEC-105|DISPONIBLE|30000";
            case CODIGO_REDES:
                return "TEC-210|DISPONIBLE|45000";
            case CODIGO_RECUPERACION:
                return "SIN_TECNICO|NO_DISPONIBLE|0";
            default:
                return "SIN_TECNICO|NO_DISPONIBLE|0";
        }
    }
}
