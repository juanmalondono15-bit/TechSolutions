package com.techsolutions.integracion;

import com.techsolutions.modelo.Especialidad;
import com.techsolutions.modelo.ProveedorTecnicos;
import com.techsolutions.modelo.TecnicoEspecializado;

/**
 * Adaptador que integra la plataforma externa {@link LegacySupport} con el
 * sistema nuevo. Implementa el patron <b>Adapter</b>.
 *
 * <p>Traduce en los dos sentidos:</p>
 * <ol>
 *   <li><b>Entrada:</b> el sistema pide por nombre de especialidad y el
 *       adaptador envia a la plataforma externa el codigo numerico
 *       ({@link Especialidad#getCodigoLegacy()}).</li>
 *   <li><b>Salida:</b> la plataforma devuelve una cadena con barras y el
 *       adaptador la convierte en un objeto {@link TecnicoEspecializado} con
 *       campos con nombre (identificador, disponibilidad y costo de
 *       desplazamiento).</li>
 * </ol>
 *
 * <p>Gracias a este adaptador, el resto del sistema depende solo de la interfaz
 * {@link ProveedorTecnicos}: no conoce {@code LegacySupport}, no maneja codigos
 * numericos ni interpreta textos con barras (<b>principio de inversion de
 * dependencias</b>).</p>
 */
public class AdaptadorLegacySupport implements ProveedorTecnicos {

    /** Separador que usa la plataforma externa en sus respuestas. */
    private static final String SEPARADOR = "\\|";

    /** Estado que la plataforma externa devuelve cuando hay tecnico. */
    private static final String DISPONIBLE = "DISPONIBLE";

    /** Plataforma externa que se esta adaptando. */
    private final LegacySupport plataformaLegacy;

    /** Ultima respuesta cruda recibida; se usa para demostrarlo en la interfaz. */
    private String ultimaRespuestaCruda = "";

    /**
     * @param plataformaLegacy instancia de la plataforma externa (no modificable)
     */
    public AdaptadorLegacySupport(LegacySupport plataformaLegacy) {
        this.plataformaLegacy = plataformaLegacy;
    }

    /**
     * Traduce la consulta del sistema nuevo a la llamada del sistema legado y
     * estructura la respuesta.
     *
     * @param especialidad especialidad requerida por el servicio
     * @return tecnico con la informacion ya estructurada
     */
    @Override
    public TecnicoEspecializado consultar(Especialidad especialidad) {
        // 1) Traduce nombre de especialidad -> codigo numerico del legado.
        String respuesta = plataformaLegacy.consultarDisponibilidadTecnico(especialidad.getCodigoLegacy());
        ultimaRespuestaCruda = respuesta;

        // 2) Divide la respuesta "ID|ESTADO|COSTO" en sus tres campos.
        String[] partes = respuesta.split(SEPARADOR);
        String identificador = partes.length > 0 ? partes[0] : "SIN_TECNICO";
        String estado = partes.length > 1 ? partes[1] : "NO_DISPONIBLE";
        double costo = 0;

        // 3) Convierte el costo de texto a numero (si el texto no es valido, queda en 0).
        if (partes.length > 2) {
            try {
                costo = Double.parseDouble(partes[2]);
            } catch (NumberFormatException e) {
                costo = 0;
            }
        }

        // 4) Entrega informacion estructurada al sistema nuevo.
        boolean disponible = DISPONIBLE.equalsIgnoreCase(estado);
        return new TecnicoEspecializado(identificador, especialidad, disponible, costo);
    }

    /**
     * @return ultima cadena cruda devuelta por {@code LegacySupport}; sirve para
     *         mostrar en la interfaz la traduccion que hace el adaptador
     */
    public String getUltimaRespuestaCruda() {
        return ultimaRespuestaCruda;
    }
}
