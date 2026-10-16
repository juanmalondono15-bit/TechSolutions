package com.techsolutions.fabricas;

import com.techsolutions.modelo.ServicioTecnico;

/**
 * Fabrica abstracta de servicios tecnologicos. Implementa el patron
 * <b>Factory Method</b> (variante de fabrica por tipo de servicio) y es el
 * punto que hace extensible el catalogo.
 *
 * <p><b>Problema que resuelve:</b> el enunciado pide que la creacion de
 * servicios sea organizada y que los componentes que gestionan las solicitudes
 * no dependan de las implementaciones especificas de cada tipo de servicio.</p>
 *
 * <p><b>Como lo resuelve:</b> cada tipo de servicio tiene su propia fabrica
 * (subclase de esta clase). El resto del sistema trabaja con
 * {@code FabricaServicio} y con {@link ServicioTecnico}; nunca con
 * {@code new MantenimientoCorrectivo(...)}. Asi, para incorporar un nuevo tipo
 * de servicio solo se agrega una fabrica nueva y se registra en
 * {@link CatalogoFabricas}: no hay que modificar el catalogo, ni los paquetes,
 * ni las solicitudes, ni la interfaz grafica (principio <b>abierto/cerrado</b>).</p>
 *
 * <p>Ademas cada fabrica conoce los valores por defecto del tipo de servicio
 * (codigo, descripcion y costo base definido por TechSolutions), con lo que el
 * catalogo se puede precargar automaticamente.</p>
 */
public abstract class FabricaServicio {

    /** @return nombre del tipo de servicio (etiqueta que ve el usuario) */
    public abstract String getTipo();

    /** @return codigo con el que se precarga el servicio en el catalogo */
    public abstract String getCodigoSugerido();

    /** @return descripcion con la que se precarga el servicio en el catalogo */
    public abstract String getDescripcionSugerida();

    /** @return costo base definido por TechSolutions para este tipo de servicio */
    public abstract double getCostoBaseSugerido();

    /**
     * Crea una instancia del servicio con los datos indicados.
     *
     * <p>Este es el <i>Factory Method</i>: cada subclase decide que clase
     * concreta construir.</p>
     *
     * @param codigo      codigo del servicio
     * @param nombre      nombre del servicio
     * @param descripcion descripcion del servicio
     * @param costoBase   costo base del servicio
     * @return servicio tecnologico del tipo que fabrica esta clase
     */
    public abstract ServicioTecnico crear(String codigo, String nombre, String descripcion, double costoBase);

    /**
     * Crea el servicio con los valores por defecto del tipo (usado para
     * precargar el catalogo de TechSolutions).
     *
     * @return servicio con codigo, nombre, descripcion y costo sugeridos
     */
    public ServicioTecnico crearConValoresPorDefecto() {
        return crear(getCodigoSugerido(), getTipo(), getDescripcionSugerida(), getCostoBaseSugerido());
    }

    /** Se usa para mostrar la fabrica en los {@code ComboBox} de JavaFX. */
    @Override
    public String toString() {
        return getTipo();
    }
}
