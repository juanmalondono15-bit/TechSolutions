package com.techsolutions.modelo;

import com.techsolutions.excepciones.ValidacionException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Solicitud de servicio realizada por un cliente.
 *
 * <p>Se construye con el patron <b>Builder</b> (ver {@link Builder}), que es el
 * encargado de garantizar que nunca exista una solicitud sin la informacion
 * esencial. Esta es una de las exigencias del enunciado:</p>
 *
 * <blockquote>"La empresa requiere que las solicitudes puedan configurarse de
 * forma flexible y que no sea posible registrar una solicitud sin la
 * informacion esencial."</blockquote>
 *
 * <p>Datos obligatorios: codigo unico, fecha de creacion, cliente y elementos
 * contratados (al menos uno). Datos opcionales: fecha programada de atencion,
 * observaciones tecnicas y nivel de prioridad.</p>
 */
public class Solicitud {

    // --------------------------- datos obligatorios ---------------------------

    private final String codigo;
    private final LocalDate fechaCreacion;
    private final Cliente cliente;
    private final List<ComponenteServicio> elementos;

    // --------------------------- datos opcionales -----------------------------

    private final LocalDate fechaProgramada;
    private final String observaciones;
    private final Prioridad prioridad;

    // --------------------- datos que se calculan despues ----------------------

    private EstadoSolicitud estado;
    private double costoDesplazamiento;
    private TecnicoEspecializado tecnicoAsignado;

    /** Constructor privado: solo el Builder puede crear solicitudes. */
    private Solicitud(Builder builder) {
        this.codigo = builder.codigo;
        this.fechaCreacion = builder.fechaCreacion;
        this.cliente = builder.cliente;
        this.elementos = Collections.unmodifiableList(new ArrayList<>(builder.elementos));
        this.fechaProgramada = builder.fechaProgramada;
        this.observaciones = builder.observaciones;
        this.prioridad = builder.prioridad;
        this.estado = EstadoSolicitud.REGISTRADA;
        this.costoDesplazamiento = 0;
    }

    public String getCodigo() {
        return codigo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public Cliente getCliente() {
        return cliente;
    }

    /** @return lista inmutable de los elementos contratados */
    public List<ComponenteServicio> getElementos() {
        return elementos;
    }

    /** @return fecha programada de atencion o {@code null} si no se definio */
    public LocalDate getFechaProgramada() {
        return fechaProgramada;
    }

    /** @return observaciones tecnicas; cadena vacia si no se escribieron */
    public String getObservaciones() {
        return observaciones;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    /** @return costo de desplazamiento que se cobra una sola vez por solicitud */
    public double getCostoDesplazamiento() {
        return costoDesplazamiento;
    }

    /** @return tecnico asignado durante la confirmacion, o {@code null} */
    public TecnicoEspecializado getTecnicoAsignado() {
        return tecnicoAsignado;
    }

    // ----------------------------- calculos ----------------------------------

    /**
     * Suma el costo de todos los elementos contratados. Como los elementos
     * pueden ser servicios, servicios personalizados (con complementos) o
     * paquetes, el polimorfismo se encarga del calculo correcto.
     *
     * @return costo de los servicios, complementos y paquetes contratados
     */
    public double calcularCostoElementos() {
        double total = 0;
        for (ComponenteServicio elemento : elementos) {
            total += elemento.calcularCosto();
        }
        return total;
    }

    /**
     * Costo total de la solicitud: elementos contratados mas el costo de
     * desplazamiento (que ya viene unificado, sin duplicados).
     *
     * @return costo total de la solicitud
     */
    public double calcularCostoTotal() {
        return calcularCostoElementos() + costoDesplazamiento;
    }

    /** @return {@code true} si algun elemento exige la presencia de un tecnico */
    public boolean requiereAtencionPresencial() {
        for (ComponenteServicio elemento : elementos) {
            if (elemento.requiereAtencionPresencial()) {
                return true;
            }
        }
        return false;
    }

    /** @return especialidades de tecnico necesarias para atender la solicitud */
    public Set<Especialidad> getEspecialidadesRequeridas() {
        Set<Especialidad> especialidades = new LinkedHashSet<>();
        for (ComponenteServicio elemento : elementos) {
            especialidades.addAll(elemento.getEspecialidadesRequeridas());
        }
        return especialidades;
    }

    // ---------------------------- ciclo de vida ------------------------------

    /**
     * Confirma la solicitud cuando se verifico la disponibilidad de tecnicos.
     *
     * @param costoDesplazamiento costo unico de desplazamiento (0 si no aplica)
     * @param tecnicoAsignado     tecnico asignado, o {@code null} si no se requiere
     */
    public void confirmar(double costoDesplazamiento, TecnicoEspecializado tecnicoAsignado) {
        this.costoDesplazamiento = costoDesplazamiento;
        this.tecnicoAsignado = tecnicoAsignado;
        this.estado = EstadoSolicitud.CONFIRMADA;
    }

    /**
     * Marca la solicitud como no confirmada cuando no hay tecnico disponible.
     * La solicitud permanece registrada y su costo no incluye desplazamiento.
     */
    public void marcarNoConfirmada() {
        this.costoDesplazamiento = 0;
        this.tecnicoAsignado = null;
        this.estado = EstadoSolicitud.NO_CONFIRMADA;
    }

    /** Se usa para mostrar la solicitud en tablas de JavaFX. */
    @Override
    public String toString() {
        return codigo + " - " + cliente.getNombreCompleto() + " (" + estado.getNombreVisible() + ")";
    }

    // -------------------------------------------------------------------------
    //  PATRON BUILDER
    // -------------------------------------------------------------------------

    /**
     * Constructor paso a paso de {@link Solicitud}.
     *
     * <p>Ventajas frente a un constructor con muchos parametros:</p>
     * <ul>
     *   <li>Los datos opcionales se agregan solo cuando el usuario los define
     *       (metodos encadenables).</li>
     *   <li>{@link #construir()} valida la informacion obligatoria y lanza
     *       {@link ValidacionException} si falta algo, por lo que es imposible
     *       crear una solicitud incompleta.</li>
     * </ul>
     *
     * <p>Ejemplo de uso:</p>
     * <pre>{@code
     * Solicitud s = Solicitud.builder("SOL-001", cliente)
     *         .agregarElemento(servicio)
     *         .conFechaProgramada(LocalDate.now().plusDays(3))
     *         .conObservaciones("Equipo de la sala de sistemas")
     *         .conPrioridad(Prioridad.ALTA)
     *         .construir();
     * }</pre>
     */
    public static class Builder {

        // Obligatorios
        private final String codigo;
        private final Cliente cliente;
        private final List<ComponenteServicio> elementos = new ArrayList<>();
        private LocalDate fechaCreacion = LocalDate.now();

        // Opcionales (con valores por defecto)
        private LocalDate fechaProgramada;
        private String observaciones = "";
        private Prioridad prioridad = Prioridad.MEDIA;

        /**
         * Crea el constructor de una solicitud.
         *
         * @param codigo  codigo unico generado por el sistema
         * @param cliente cliente que realiza la solicitud
         */
        public Builder(String codigo, Cliente cliente) {
            this.codigo = codigo;
            this.cliente = cliente;
        }

        /**
         * Agrega un elemento contratado (servicio, servicio personalizado o paquete).
         *
         * @param elemento componente a incluir en la solicitud
         * @return este mismo constructor para encadenar llamadas
         */
        public Builder agregarElemento(ComponenteServicio elemento) {
            if (elemento != null) {
                this.elementos.add(elemento);
            }
            return this;
        }

        /**
         * Define la fecha programada de atencion (dato opcional).
         *
         * @param fecha fecha acordada con el cliente; puede ser {@code null}
         * @return este mismo constructor
         */
        public Builder conFechaProgramada(LocalDate fecha) {
            this.fechaProgramada = fecha;
            return this;
        }

        /**
         * Define las observaciones tecnicas (dato opcional).
         *
         * @param observaciones texto libre; puede ser {@code null}
         * @return este mismo constructor
         */
        public Builder conObservaciones(String observaciones) {
            this.observaciones = observaciones == null ? "" : observaciones.trim();
            return this;
        }

        /**
         * Define la prioridad de la solicitud (dato opcional, por defecto MEDIA).
         *
         * @param prioridad prioridad elegida
         * @return este mismo constructor
         */
        public Builder conPrioridad(Prioridad prioridad) {
            if (prioridad != null) {
                this.prioridad = prioridad;
            }
            return this;
        }

        /**
         * Valida la informacion obligatoria y crea la solicitud.
         *
         * @return solicitud lista para usarse
         * @throws ValidacionException si falta el codigo, el cliente o los elementos
         */
        public Solicitud construir() throws ValidacionException {
            if (codigo == null || codigo.isBlank()) {
                throw new ValidacionException("El codigo de la solicitud es obligatorio.");
            }
            if (cliente == null) {
                throw new ValidacionException("La solicitud debe estar asociada a un cliente.");
            }
            if (elementos.isEmpty()) {
                throw new ValidacionException("La solicitud debe incluir al menos un servicio o paquete.");
            }
            return new Solicitud(this);
        }
    }
}
