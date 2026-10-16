package com.techsolutions.fabricas;

import com.techsolutions.modelo.InstalacionRedes;
import com.techsolutions.modelo.ServicioTecnico;

/**
 * Fabrica concreta de {@link InstalacionRedes}.
 *
 * <p>Costo base definido por TechSolutions: <b>$180.000</b>.</p>
 */
public class FabricaInstalacionRedes extends FabricaServicio {

    @Override
    public String getTipo() {
        return "Instalacion de redes";
    }

    @Override
    public String getCodigoSugerido() {
        return "SRV-RED";
    }

    @Override
    public String getDescripcionSugerida() {
        return "Instalacion y configuracion de redes de datos.";
    }

    @Override
    public double getCostoBaseSugerido() {
        return 180_000;
    }

    @Override
    public ServicioTecnico crear(String codigo, String nombre, String descripcion, double costoBase) {
        return new InstalacionRedes(codigo, nombre, descripcion, costoBase);
    }
}
