package com.techsolutions.modelo;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Clase base de los <b>servicios tecnologicos individuales</b> del catalogo.
 *
 * <p>Todos los servicios comparten la misma informacion (codigo, nombre,
 * descripcion y costo base) y se diferencian en su comportamiento particular:
 * las especialidades de tecnico que requieren y si necesitan o no atencion
 * presencial. Esa parte variable la definen las subclases, lo que permite
 * agregar nuevos tipos de servicio sin tocar esta clase ni el resto del
 * sistema (principio abierto/cerrado).</p>
 *
 * <p>Es la clase <i>Component</i> del catalogo y la clase base que reutiliza
 * el patron <b>Decorator</b> (a traves de {@link ComponenteServicio}).</p>
 */
public abstract class ServicioTecnico implements ComponenteServicio {

    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private final double costoBase;
    private final Set<Especialidad> especialidades;

    /**
     * @param codigo        identificador unico del servicio
     * @param nombre        nombre visible
     * @param descripcion   descripcion (puede ser vacia)
     * @param costoBase     costo del servicio sin complementos
     * @param especialidades especialidades de tecnico necesarias para prestarlo
     */
    protected ServicioTecnico(String codigo, String nombre, String descripcion,
                              double costoBase, Set<Especialidad> especialidades) {
        this.codigo = Objects.requireNonNull(codigo, "El codigo es obligatorio");
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio");
        this.descripcion = descripcion == null ? "" : descripcion;
        if (costoBase < 0) {
            throw new IllegalArgumentException("El costo base no puede ser negativo");
        }
        this.costoBase = costoBase;
        this.especialidades = new LinkedHashSet<>(especialidades);
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

    /** @return costo base del servicio (sin complementos) */
    public double getCostoBase() {
        return costoBase;
    }

    /**
     * Un servicio individual siempre cuesta su costo base.
     *
     * <p>Los complementos son los encargados de sumar incrementos, y se
     * aplican como decoradores alrededor de este objeto; por eso el catalogo
     * nunca se modifica cuando un cliente personaliza un servicio.</p>
     */
    @Override
    public double calcularCosto() {
        return costoBase;
    }

    @Override
    public Set<Especialidad> getEspecialidadesRequeridas() {
        return Collections.unmodifiableSet(especialidades);
    }

    /** @return nombre del tipo de servicio (nombre simple de la clase) */
    public String getTipo() {
        return getClass().getSimpleName();
    }

    /** Se usa para mostrar el servicio en listas y tablas de JavaFX. */
    @Override
    public String toString() {
        return nombre + " (" + codigo + ")";
    }
}
