package com.techsolutions.fabricas;

import com.techsolutions.modelo.MantenimientoCorrectivo;
import com.techsolutions.modelo.ServicioTecnico;

/**
 * Fabrica concreta de {@link MantenimientoCorrectivo}.
 *
 * <p>Costo base definido por TechSolutions: <b>$120.000</b>.</p>
 */
public class FabricaMantenimientoCorrectivo extends FabricaServicio {

    @Override
    public String getTipo() {
        return "Mantenimiento correctivo";
    }

    @Override
    public String getCodigoSugerido() {
        return "SRV-COR";
    }

    @Override
    public String getDescripcionSugerida() {
        return "Reparacion de equipos de computo que presentan fallas.";
    }

    @Override
    public double getCostoBaseSugerido() {
        return 120_000;
    }

    @Override
    public ServicioTecnico crear(String codigo, String nombre, String descripcion, double costoBase) {
        return new MantenimientoCorrectivo(codigo, nombre, descripcion, costoBase);
    }
}
