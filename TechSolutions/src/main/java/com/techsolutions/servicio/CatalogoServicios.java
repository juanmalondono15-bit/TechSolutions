package com.techsolutions.servicio;

import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.fabricas.CatalogoFabricas;
import com.techsolutions.modelo.ServicioTecnico;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Catalogo de servicios tecnologicos disponibles en TechSolutions.
 * Implementa el patron <b>Singleton</b>.
 *
 * <p><b>Por que Singleton:</b> el catalogo es un recurso compartido y unico:
 * la interfaz grafica, los paquetes y las solicitudes deben ver exactamente los
 * mismos servicios. Con una unica instancia se evita tener catalogos
 * duplicados y desincronizados.</p>
 *
 * <p>El catalogo se <b>precarga</b> automaticamente con los cuatro servicios y
 * sus costos definidos por la empresa, usando las fabricas registradas en
 * {@link CatalogoFabricas}. Ademas permite registrar servicios nuevos creados
 * por el usuario, sin que eso modifique los servicios ya existentes.</p>
 */
public final class CatalogoServicios {

    /** Instancia unica del catalogo. */
    private static final CatalogoServicios INSTANCIA = new CatalogoServicios();

    /** Servicios indexados por codigo, conservando el orden de insercion. */
    private final Map<String, ServicioTecnico> servicios = new LinkedHashMap<>();

    /** Constructor privado: patron Singleton. */
    private CatalogoServicios() {
        precargar();
    }

    /** @return la instancia unica del catalogo de servicios */
    public static CatalogoServicios obtenerInstancia() {
        return INSTANCIA;
    }

    /**
     * Precarga los servicios definidos por TechSolutions (los cuatro tipos con
     * sus costos base) tomandolos de las fabricas registradas.
     */
    private void precargar() {
        for (var fabrica : CatalogoFabricas.obtenerInstancia().listar()) {
            ServicioTecnico servicio = fabrica.crearConValoresPorDefecto();
            servicios.put(servicio.getCodigo(), servicio);
        }
    }

    /**
     * Registra un servicio nuevo en el catalogo.
     *
     * @param servicio servicio a registrar
     * @throws ValidacionException si ya existe un servicio con el mismo codigo
     */
    public void registrar(ServicioTecnico servicio) throws ValidacionException {
        if (servicio == null) {
            throw new ValidacionException("El servicio no puede ser nulo.");
        }
        if (servicios.containsKey(servicio.getCodigo())) {
            throw new ValidacionException("Ya existe un servicio con el codigo " + servicio.getCodigo() + ".");
        }
        servicios.put(servicio.getCodigo(), servicio);
    }

    /** @return lista inmutable de los servicios disponibles (definiciones originales) */
    public List<ServicioTecnico> listar() {
        return List.copyOf(servicios.values());
    }

    /**
     * Busca un servicio por su codigo.
     *
     * @param codigo codigo del servicio
     * @return el servicio si existe
     */
    public Optional<ServicioTecnico> buscar(String codigo) {
        return Optional.ofNullable(servicios.get(codigo));
    }

    /**
     * Indica si ya existe un servicio con ese codigo.
     *
     * @param codigo codigo a verificar
     * @return {@code true} si el codigo esta en uso
     */
    public boolean existe(String codigo) {
        return servicios.containsKey(codigo);
    }
}
