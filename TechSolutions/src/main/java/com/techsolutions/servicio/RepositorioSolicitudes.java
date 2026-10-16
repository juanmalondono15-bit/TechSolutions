package com.techsolutions.servicio;

import com.techsolutions.modelo.Solicitud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio en memoria de las solicitudes registradas.
 *
 * <p>Se separa del servicio de aplicacion para respetar el principio de
 * responsabilidad unica: aqui solo se <b>almacenan</b> solicitudes, mientras
 * que {@link ServicioSolicitudes} aplica las reglas de negocio.</p>
 *
 * <p>Ademas es el encargado de generar el <b>codigo unico</b> de cada
 * solicitud, garantizando que no se repita.</p>
 */
public class RepositorioSolicitudes {

    /** Solicitudes registradas, en orden de creacion. */
    private final List<Solicitud> solicitudes = new ArrayList<>();

    /** Consecutivo usado para generar codigos unicos. */
    private int consecutivo = 0;

    /**
     * Guarda una solicitud.
     *
     * @param solicitud solicitud a guardar
     */
    public void agregar(Solicitud solicitud) {
        if (solicitud != null) {
            solicitudes.add(solicitud);
        }
    }

    /** @return copia inmutable de las solicitudes registradas */
    public List<Solicitud> listar() {
        return List.copyOf(solicitudes);
    }

    /**
     * Busca una solicitud por su codigo.
     *
     * @param codigo codigo de la solicitud
     * @return la solicitud si existe
     */
    public Optional<Solicitud> buscarPorCodigo(String codigo) {
        return solicitudes.stream()
                .filter(s -> s.getCodigo().equals(codigo))
                .findFirst();
    }

    /**
     * Genera el siguiente codigo unico de solicitud.
     *
     * @return codigo con el formato {@code SOL-001}, {@code SOL-002}, ...
     */
    public String generarCodigo() {
        consecutivo++;
        return String.format("SOL-%03d", consecutivo);
    }
}
