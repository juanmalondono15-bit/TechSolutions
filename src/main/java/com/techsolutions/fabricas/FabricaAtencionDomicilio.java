package com.techsolutions.fabricas;

import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.complemento.AtencionDomicilio;
import com.techsolutions.modelo.complemento.ComplementoServicio;

/**
 * Fabrica concreta del complemento {@link AtencionDomicilio} (+$25.000).
 */
public class FabricaAtencionDomicilio extends FabricaComplemento {

    @Override
    public String getNombre() {
        return AtencionDomicilio.NOMBRE;
    }

    @Override
    public double getIncremento() {
        return AtencionDomicilio.INCREMENTO;
    }

    @Override
    public ComplementoServicio aplicarA(ComponenteServicio componente) {
        return new AtencionDomicilio(componente);
    }
}
