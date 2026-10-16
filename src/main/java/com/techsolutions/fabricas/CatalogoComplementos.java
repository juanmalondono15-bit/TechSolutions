package com.techsolutions.fabricas;

import java.util.List;

/**
 * Registro de las caracteristicas complementarias disponibles. Implementa el
 * patron <b>Singleton</b> con el mismo proposito que {@link CatalogoFabricas}:
 * tener una unica lista de complementos para toda la aplicacion y ser el unico
 * punto a modificar cuando la empresa agregue nuevas caracteristicas.
 */
public final class CatalogoComplementos {

    /** Instancia unica. */
    private static final CatalogoComplementos INSTANCIA = new CatalogoComplementos();

    /** Complementos que ofrece TechSolutions actualmente. */
    private final List<FabricaComplemento> fabricas = List.of(
            new FabricaAtencionPrioritaria(),
            new FabricaSoporteTecnico30Dias(),
            new FabricaAtencionDomicilio()
    );

    /** Constructor privado: patron Singleton. */
    private CatalogoComplementos() {
    }

    /** @return la instancia unica del registro de complementos */
    public static CatalogoComplementos obtenerInstancia() {
        return INSTANCIA;
    }

    /** @return lista inmutable con las fabricas de complementos registradas */
    public List<FabricaComplemento> listar() {
        return fabricas;
    }
}
