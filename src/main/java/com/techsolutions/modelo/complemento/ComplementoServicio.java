package com.techsolutions.modelo.complemento;

import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.Especialidad;

import java.util.Objects;
import java.util.Set;

/**
 * Clase base de las <b>caracteristicas complementarias</b> que un cliente puede
 * incorporar a un servicio contratado.
 *
 * <p>Implementa el patron <b>Decorator</b>:</p>
 * <ul>
 *   <li><i>Component</i>: {@link ComponenteServicio}.</li>
 *   <li><i>ConcreteDecorator</i>: {@link AtencionPrioritaria},
 *       {@link SoporteTecnico30Dias} y {@link AtencionDomicilio}.</li>
 *   <li><i>Decorator</i>: esta clase, que guarda el componente decorado
 *       ({@code servicioContratado}) y delega en el todas las operaciones,
 *       agregando unicamente el incremento de costo.</li>
 * </ul>
 *
 * <p><b>Idea clave:</b> el decorador envuelve una copia del servicio
 * contratado; nunca modifica el objeto original que vive en el catalogo. Asi
 * un mismo servicio puede contratarse con complementos para un cliente y sin
 * complementos para otro.</p>
 */
public abstract class ComplementoServicio implements ComponenteServicio {

    /** Componente que se esta decorando (servicio individual, personalizado o paquete). */
    protected final ComponenteServicio servicioContratado;

    /**
     * @param servicioContratado componente al que se le agregara el complemento
     */
    protected ComplementoServicio(ComponenteServicio servicioContratado) {
        this.servicioContratado = Objects.requireNonNull(servicioContratado,
                "El servicio contratado no puede ser nulo");
    }

    /** @return el componente que este decorador envuelve */
    public ComponenteServicio getServicioContratado() {
        return servicioContratado;
    }

    /** @return nombre del complemento (por ejemplo "Atencion prioritaria") */
    public abstract String getNombreComplemento();

    /** @return valor que el complemento suma al costo del servicio contratado */
    public abstract double getIncremento();

    @Override
    public String getCodigo() {
        // El servicio personalizado conserva el codigo del servicio original,
        // porque es una variacion contratada y no una definicion nueva del catalogo.
        return servicioContratado.getCodigo();
    }

    @Override
    public String getNombre() {
        // El nombre acumula los complementos aplicados, lo que hace visible la
        // personalizacion en tablas, arboles y solicitudes.
        return servicioContratado.getNombre() + " + " + getNombreComplemento();
    }

    @Override
    public String getDescripcion() {
        return servicioContratado.getDescripcion() + " Incluye el complemento: "
                + getNombreComplemento() + ".";
    }

    /** Costo del servicio contratado mas el incremento del complemento. */
    @Override
    public double calcularCosto() {
        return servicioContratado.calcularCosto() + getIncremento();
    }

    @Override
    public boolean requiereAtencionPresencial() {
        return servicioContratado.requiereAtencionPresencial();
    }

    @Override
    public Set<Especialidad> getEspecialidadesRequeridas() {
        return servicioContratado.getEspecialidadesRequeridas();
    }

    @Override
    public boolean contieneA(ComponenteServicio objetivo) {
        return this == objetivo || servicioContratado.contieneA(objetivo);
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
