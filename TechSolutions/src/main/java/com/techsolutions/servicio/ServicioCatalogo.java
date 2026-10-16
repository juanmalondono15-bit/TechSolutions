package com.techsolutions.servicio;

import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.fabricas.CatalogoFabricas;
import com.techsolutions.fabricas.FabricaServicio;
import com.techsolutions.modelo.ServicioTecnico;

import java.util.List;

/**
 * Servicio de aplicacion que atiende la funcionalidad
 * <b>"Gestion y personalizacion de servicios tecnologicos"</b> (primera
 * funcionalidad requerida).
 *
 * <p>Responsabilidades (principio de responsabilidad unica):</p>
 * <ul>
 *   <li>Consultar los servicios tecnologicos disponibles.</li>
 *   <li>Ofrecer los tipos de servicio que se pueden crear (sus fabricas).</li>
 *   <li>Crear servicios de los tipos establecidos, validando los datos.</li>
 * </ul>
 *
 * <p>No construye objetos por si mismo: delega la creacion en las fabricas
 * (patron <b>Factory Method</b>), de modo que no depende de las clases
 * concretas de cada tipo de servicio.</p>
 */
public class ServicioCatalogo {

    /** Catalogo (unico) de servicios disponibles. */
    private final CatalogoServicios catalogo;

    /** Registro (unico) de fabricas de servicios. */
    private final CatalogoFabricas fabricas;

    /**
     * Crea el servicio de aplicacion tomando el catalogo y las fabricas
     * compartidas de la aplicacion.
     */
    public ServicioCatalogo() {
        this.catalogo = CatalogoServicios.obtenerInstancia();
        this.fabricas = CatalogoFabricas.obtenerInstancia();
    }

    /** @return servicios tecnologicos disponibles en el catalogo */
    public List<ServicioTecnico> listarDisponibles() {
        return catalogo.listar();
    }

    /** @return tipos de servicio que se pueden crear (una fabrica por tipo) */
    public List<FabricaServicio> listarTiposDeServicio() {
        return fabricas.listar();
    }

    /**
     * Crea un servicio nuevo y lo agrega al catalogo.
     *
     * @param fabrica     tipo de servicio a crear (por ejemplo mantenimiento correctivo)
     * @param codigo      codigo unico del servicio
     * @param nombre      nombre visible del servicio
     * @param descripcion descripcion del servicio
     * @param costoBase   costo base del servicio
     * @return el servicio creado y registrado
     * @throws ValidacionException si algun dato falta, el costo es negativo o
     *                             el codigo ya existe
     */
    public ServicioTecnico crearServicio(FabricaServicio fabrica, String codigo, String nombre,
                                         String descripcion, double costoBase) throws ValidacionException {
        if (fabrica == null) {
            throw new ValidacionException("Debe seleccionar el tipo de servicio a crear.");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new ValidacionException("El codigo del servicio es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre del servicio es obligatorio.");
        }
        if (costoBase < 0) {
            throw new ValidacionException("El costo base no puede ser negativo.");
        }

        // La fabrica decide que clase concreta se instancia (Factory Method).
        ServicioTecnico servicio = fabrica.crear(codigo.trim(), nombre.trim(), descripcion, costoBase);
        catalogo.registrar(servicio);
        return servicio;
    }
}
