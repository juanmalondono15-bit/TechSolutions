package com.techsolutions.fabricas;

import com.techsolutions.modelo.RecuperacionInformacion;
import com.techsolutions.modelo.ServicioTecnico;

/**
 * Fabrica concreta de {@link RecuperacionInformacion}.
 *
 * <p>Costo base definido por TechSolutions: <b>$150.000</b>.</p>
 */
public class FabricaRecuperacionInformacion extends FabricaServicio {

    @Override
    public String getTipo() {
        return "Recuperacion de informacion";
    }

    @Override
    public String getCodigoSugerido() {
        return "SRV-REC";
    }

    @Override
    public String getDescripcionSugerida() {
        return "Recuperacion de informacion de discos y servidores.";
    }

    @Override
    public double getCostoBaseSugerido() {
        return 150_000;
    }

    @Override
    public ServicioTecnico crear(String codigo, String nombre, String descripcion, double costoBase) {
        return new RecuperacionInformacion(codigo, nombre, descripcion, costoBase);
    }
}
