package com.techsolutions.servicio;

import com.techsolutions.excepciones.PaqueteCiclicoException;
import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.PaqueteTecnologico;
import com.techsolutions.util.Formato;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicacion que atiende la funcionalidad <b>"Configuracion de
 * paquetes tecnologicos"</b> (segunda funcionalidad requerida).
 *
 * <p>Permite:</p>
 * <ul>
 *   <li>Crear paquetes tecnologicos.</li>
 *   <li>Agregar componentes: servicios individuales, servicios personalizados
 *       u otros paquetes (patron <b>Composite</b>).</li>
 *   <li>Consultar la composicion del paquete en forma de texto con niveles.</li>
 *   <li>Calcular automaticamente el costo total, sin importar cuantos niveles
 *       de agrupacion existan.</li>
 * </ul>
 *
 * <p>El impedimento de meter un paquete dentro de si mismo lo garantiza
 * {@link PaqueteTecnologico#agregar(ComponenteServicio)}, que lanza
 * {@link PaqueteCiclicoException}; este servicio solo propaga el error.</p>
 */
public class ServicioPaquetes {

    /** Paquetes creados en esta sesion. */
    private final List<PaqueteTecnologico> paquetes = new ArrayList<>();

    /** @return paquetes configurados */
    public List<PaqueteTecnologico> listar() {
        return List.copyOf(paquetes);
    }

    /**
     * Crea un paquete tecnologico vacio.
     *
     * @param codigo      codigo unico del paquete
     * @param nombre      nombre visible
     * @param descripcion descripcion (puede ser vacia)
     * @return el paquete creado
     * @throws ValidacionException si faltan datos o el codigo ya existe
     */
    public PaqueteTecnologico crear(String codigo, String nombre, String descripcion)
            throws ValidacionException {
        if (codigo == null || codigo.isBlank()) {
            throw new ValidacionException("El codigo del paquete es obligatorio.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ValidacionException("El nombre del paquete es obligatorio.");
        }
        boolean repetido = paquetes.stream().anyMatch(p -> p.getCodigo().equalsIgnoreCase(codigo.trim()));
        if (repetido) {
            throw new ValidacionException("Ya existe un paquete con el codigo " + codigo + ".");
        }
        PaqueteTecnologico paquete = new PaqueteTecnologico(codigo.trim(), nombre.trim(), descripcion);
        paquetes.add(paquete);
        return paquete;
    }

    /**
     * Agrega un componente a un paquete.
     *
     * @param paquete    paquete que recibe el componente
     * @param componente servicio, servicio personalizado u otro paquete
     * @throws ValidacionException    si falta el paquete o el componente
     * @throws PaqueteCiclicoException si el componente contiene al paquete
     *                                (se intenta crear un ciclo)
     */
    public void agregarComponente(PaqueteTecnologico paquete, ComponenteServicio componente)
            throws ValidacionException, PaqueteCiclicoException {
        if (paquete == null) {
            throw new ValidacionException("Debe seleccionar el paquete al que desea agregar el componente.");
        }
        if (componente == null) {
            throw new ValidacionException("Debe seleccionar el componente que desea agregar.");
        }
        paquete.agregar(componente);
    }

    /**
     * Calcula el costo total de un paquete (delegando en el modelo).
     *
     * @param paquete paquete a calcular
     * @return costo total del paquete
     */
    public double calcularCostoTotal(PaqueteTecnologico paquete) {
        return paquete == null ? 0 : paquete.calcularCosto();
    }

    /**
     * Describe la composicion de un paquete con sangria por nivel, para que el
     * usuario pueda consultar facilmente que contiene.
     *
     * <p>Ejemplo de salida:</p>
     * <pre>
     * Paquete empresarial
     *   - Mantenimiento correctivo ($ 120.000)
     *   - Paquete mantenimiento ($ 200.000)
     *     - Mantenimiento preventivo ($ 80.000)
     *     - Mantenimiento correctivo ($ 120.000)
     * </pre>
     *
     * @param paquete paquete a describir
     * @return lineas de texto con la composicion jerarquica
     */
    public List<String> describirComposicion(PaqueteTecnologico paquete) {
        List<String> lineas = new ArrayList<>();
        if (paquete == null) {
            return lineas;
        }
        lineas.add(paquete.getNombre() + " - total " + Formato.moneda(paquete.calcularCosto()));
        agregarHijos(paquete, 1, lineas);
        return lineas;
    }

    /** Recorre recursivamente los componentes agregando las lineas con sangria. */
    private void agregarHijos(ComponenteServicio componente, int nivel, List<String> lineas) {
        for (ComponenteServicio hijo : obtenerHijos(componente)) {
            lineas.add("   ".repeat(nivel) + "- " + hijo.getNombre()
                    + " (" + Formato.moneda(hijo.calcularCosto()) + ")");
            agregarHijos(hijo, nivel + 1, lineas);
        }
    }

    /**
     * @param componente componente del que se quieren los hijos
     * @return los componentes internos si es un paquete, o lista vacia si es un servicio
     */
    private List<ComponenteServicio> obtenerHijos(ComponenteServicio componente) {
        if (componente instanceof PaqueteTecnologico paquete) {
            return paquete.getComponentes();
        }
        return List.of();
    }
}
