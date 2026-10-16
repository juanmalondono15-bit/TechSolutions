package com.techsolutions.fabricas;

import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.complemento.AtencionPrioritaria;
import com.techsolutions.modelo.complemento.ComplementoServicio;

/**
 * Fabrica concreta del complemento {@link AtencionPrioritaria} (+$35.000).
 */
public class FabricaAtencionPrioritaria extends FabricaComplemento {

    @Override
    public String getNombre() {
        return AtencionPrioritaria.NOMBRE;
    }

    @Override
    public double getIncremento() {
        return AtencionPrioritaria.INCREMENTO;
    }

    @Override
    public ComplementoServicio aplicarA(ComponenteServicio componente) {
        return new AtencionPrioritaria(componente);
    }
}
