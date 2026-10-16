package com.techsolutions.fabricas;

import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.complemento.ComplementoServicio;
import com.techsolutions.util.Formato;

/**
 * Fabrica abstracta de caracteristicas complementarias. Aplica el patron
 * <b>Factory Method</b> al otro extremo del Decorator: crea el decorador
 * concreto sin que el resto del sistema lo conozca.
 *
 * <p>De esta forma, si TechSolutions agrega un nuevo complemento (por ejemplo
 * "respaldo en la nube"), solo se crea su fabrica y se registra en
 * {@link CatalogoComplementos}; ni la interfaz grafica ni las solicitudes se
 * modifican (principio <b>abierto/cerrado</b>).</p>
 */
public abstract class FabricaComplemento {

    /** @return nombre del complemento (etiqueta que ve el usuario) */
    public abstract String getNombre();

    /** @return valor que el complemento suma al costo del servicio */
    public abstract double getIncremento();

    /**
     * Envuelve un componente con el complemento.
     *
     * @param componente servicio, servicio personalizado o paquete a decorar
     * @return componente decorado con el complemento
     */
    public abstract ComplementoServicio aplicarA(ComponenteServicio componente);

    /** Se usa para mostrar la fabrica en los {@code ComboBox} de JavaFX. */
    @Override
    public String toString() {
        return getNombre() + " (+ " + Formato.moneda(getIncremento()) + ")";
    }
}
