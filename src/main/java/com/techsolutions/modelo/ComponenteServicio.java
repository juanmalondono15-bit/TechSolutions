package com.techsolutions.modelo;

import java.util.Set;

/**
 * Abstraccion comun de <b>todo lo que un cliente puede contratar</b> en
 * TechSolutions: servicios individuales, servicios personalizados (servicios
 * con complementos) y paquetes tecnologicos.
 *
 * <p>Esta interfaz es la clave del <b>bajo acoplamiento</b> del sistema: las
 * solicitudes, los paquetes y la interfaz grafica trabajan siempre contra
 * {@code ComponenteServicio} y nunca contra las clases concretas
 * (principio de inversion de dependencias, la "D" de SOLID).</p>
 *
 * <p>Gracias a ella:</p>
 * <ul>
 *   <li>El patron <b>Composite</b> puede tratar un paquete igual que un
 *       servicio individual ({@code calcularCosto()} devuelve la suma de sus
 *       componentes).</li>
 *   <li>El patron <b>Decorator</b> puede envolver cualquier componente para
 *       agregarle caracteristicas complementarias sin modificarlo.</li>
 *   <li>Si en el futuro aparece un nuevo tipo de servicio solo se crea una
 *       clase nueva que implemente esta interfaz (principio abierto/cerrado).</li>
 * </ul>
 */
public interface ComponenteServicio {

    /** Codigo unico del componente (por ejemplo {@code SRV-001}). */
    String getCodigo();

    /** Nombre visible para el usuario (por ejemplo "Mantenimiento correctivo"). */
    String getNombre();

    /** Descripcion del componente; puede quedar vacia si el usuario no la escribe. */
    String getDescripcion();

    /**
     * Costo del componente.
     *
     * <p>Un servicio individual devuelve su costo base, un complemento suma su
     * incremento y un paquete devuelve la suma de todos sus componentes
     * (calculo recursivo, sin importar cuantos niveles de anidamiento haya).</p>
     *
     * @return costo en pesos colombianos
     */
    double calcularCosto();

    /**
     * Indica si el componente obliga a que un tecnico asista de forma
     * presencial. En ese caso el sistema debe consultar la disponibilidad de
     * tecnicos antes de confirmar la solicitud.
     *
     * @return {@code true} si requiere atencion presencial
     */
    default boolean requiereAtencionPresencial() {
        return false;
    }

    /**
     * Especialidades de tecnico que se necesitan para prestar el componente.
     * Un paquete devuelve la union de las especialidades de sus componentes.
     *
     * @return conjunto (posiblemente vacio) de especialidades requeridas
     */
    Set<Especialidad> getEspecialidadesRequeridas();

    /**
     * Indica si este componente contiene (directa o indirectamente) a otro.
     *
     * <p>Se usa para impedir que un paquete termine contenido dentro de si
     * mismo, lo que produciria un ciclo infinito al calcular su costo.</p>
     *
     * <p>Por defecto un componente solo se "contiene" a si mismo; los paquetes
     * y los complementos sobrescriben este metodo para recorrer su interior.</p>
     *
     * @param objetivo componente que se esta buscando
     * @return {@code true} si el objetivo esta dentro de este componente
     */
    default boolean contieneA(ComponenteServicio objetivo) {
        return this == objetivo;
    }
}
