package com.techsolutions.fabricas;

import com.techsolutions.modelo.MantenimientoPreventivo;
import com.techsolutions.modelo.ServicioTecnico;

/**
 * Fabrica concreta de {@link MantenimientoPreventivo}.
 *
 * <p>Costo base definido por TechSolutions: <b>$80.000</b>.</p>
 */
public class FabricaMantenimientoPreventivo extends FabricaServicio {

    @Override
    public String getTipo() {
        return "Mantenimiento preventivo";
    }

    @Override
    public String getCodigoSugerido() {
        return "SRV-PRE";
    }

    @Override
    public String getDescripcionSugerida() {
        return "Revision programada de equipos de computo para evitar fallas.";
    }

    @Override
    public double getCostoBaseSugerido() {
        return 80_000;
    }

    @Override
    public ServicioTecnico crear(String codigo, String nombre, String descripcion, double costoBase) {
        return new MantenimientoPreventivo(codigo, nombre, descripcion, costoBase);
    }
}
