package com.techsolutions.modelo;

import com.techsolutions.excepciones.PaqueteCiclicoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Paquete tecnologico: agrupa servicios individuales, servicios personalizados
 * y otros paquetes.
 *
 * <p>Implementa el patron <b>Composite</b>:</p>
 * <ul>
 *   <li>{@code PaqueteTecnologico} es el <i>Composite</i> (puede contener
 *       hijos y delegar el calculo en ellos).</li>
 *   <li>{@link ServicioTecnico} (a traves de {@link ComponenteServicio}) es la
 *       abstraccion de los <i>Leaf</i> (elementos simples).</li>
 *   <li>{@link ComponenteServicio} es el <i>Component</i> comun.</li>
 * </ul>
 *
 * <p>Como el costo se calcula recorriendo recursivamente los componentes, el
 * sistema soporta cualquier cantidad de niveles de agrupacion (paquete dentro
 * de paquete dentro de paquete...).</p>
 *
 * <p>Ademas impide que un paquete se agregue dentro de si mismo, directa o
 * indirectamente, porque eso generaria un ciclo infinito.</p>
 */
public class PaqueteTecnologico implements ComponenteServicio {

    private final String codigo;
    private final String nombre;
    private final String descripcion;

    /** Componentes del paquete; el orden de insercion se conserva. */
    private final List<ComponenteServicio> componentes = new ArrayList<>();

    /**
     * @param codigo      identificador unico del paquete
     * @param nombre      nombre visible
     * @param descripcion descripcion (puede ser vacia)
     */
    public PaqueteTecnologico(String codigo, String nombre, String descripcion) {
        this.codigo = Objects.requireNonNull(codigo, "El codigo es obligatorio");
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.descripcion = descripcion == null ? "" : descripcion;
    }

    /**
     * Agrega un componente al paquete validando antes que no se cree un ciclo.
     *
     * @param componente servicio, servicio personalizado o paquete a agregar
     * @throws PaqueteCiclicoException si el componente contiene a este paquete
     *                                 (directa o indirectamente)
     */
    public void agregar(ComponenteServicio componente) throws PaqueteCiclicoException {
        Objects.requireNonNull(componente, "El componente no puede ser nulo");

        // Si el componente que se quiere agregar ya contiene a este paquete,
        // agregarlo produciria un ciclo: paquete -> componente -> ... -> paquete.
        if (componente.contieneA(this)) {
            throw new PaqueteCiclicoException(
                    "No se puede agregar \"" + componente.getNombre() + "\" al paquete \""
                            + nombre + "\" porque contiene al paquete y se crearia un ciclo.");
        }
        componentes.add(componente);
    }

    /** @return copia inmutable de los componentes del paquete */
    public List<ComponenteServicio> getComponentes() {
        return Collections.unmodifiableList(componentes);
    }

    @Override
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Calcula el costo total sumando recursivamente el costo de cada componente
     * (no importa cuantos niveles de anidamiento existan).
     */
    @Override
    public double calcularCosto() {
        double total = 0;
        for (ComponenteServicio componente : componentes) {
            total += componente.calcularCosto();
        }
        return total;
    }

    /** Un paquete requiere presencia si al menos uno de sus componentes la requiere. */
    @Override
    public boolean requiereAtencionPresencial() {
        for (ComponenteServicio componente : componentes) {
            if (componente.requiereAtencionPresencial()) {
                return true;
            }
        }
        return false;
    }

    /** Un paquete necesita todas las especialidades de sus componentes (union). */
    @Override
    public Set<Especialidad> getEspecialidadesRequeridas() {
        Set<Especialidad> especialidades = new LinkedHashSet<>();
        for (ComponenteServicio componente : componentes) {
            especialidades.addAll(componente.getEspecialidadesRequeridas());
        }
        return especialidades;
    }

    /** Recorre los componentes del paquete buscando el objetivo. */
    @Override
    public boolean contieneA(ComponenteServicio objetivo) {
        if (this == objetivo) {
            return true;
        }
        for (ComponenteServicio componente : componentes) {
            if (componente.contieneA(objetivo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return nombre + " (" + codigo + ") - paquete";
    }
}
