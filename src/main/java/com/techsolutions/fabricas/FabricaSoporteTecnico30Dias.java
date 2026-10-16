package com.techsolutions.fabricas;

import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.complemento.ComplementoServicio;
import com.techsolutions.modelo.complemento.SoporteTecnico30Dias;

/**
 * Fabrica concreta del complemento {@link SoporteTecnico30Dias} (+$50.000).
 */
public class FabricaSoporteTecnico30Dias extends FabricaComplemento {

    @Override
    public String getNombre() {
        return SoporteTecnico30Dias.NOMBRE;
    }

    @Override
    public double getIncremento() {
        return SoporteTecnico30Dias.INCREMENTO;
    }

    @Override
    public ComplementoServicio aplicarA(ComponenteServicio componente) {
        return new SoporteTecnico30Dias(componente);
    }
}
