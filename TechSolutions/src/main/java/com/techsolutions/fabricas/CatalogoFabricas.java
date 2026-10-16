package com.techsolutions.fabricas;

import java.util.List;

/**
 * Registro de las fabricas de servicios disponibles. Implementa el patron
 * <b>Singleton</b>.
 *
 * <p><b>Por que Singleton:</b> el conjunto de tipos de servicio que ofrece la
 * empresa es unico para toda la aplicacion. Tener una sola instancia evita que
 * el catalogo y la interfaz grafica trabajen con listas diferentes.</p>
 *
 * <p><b>Por que facilita la extension:</b> este registro es el <i>unico</i>
 * lugar donde se declara que tipos de servicio existen. Si TechSolutions
 * incorpora un nuevo servicio, solo se agrega su fabrica aqui y aparece
 * automaticamente en el catalogo, en los formularios y en los paquetes.</p>
 */
public final class CatalogoFabricas {

    /** Instancia unica (se crea al cargar la clase). */
    private static final CatalogoFabricas INSTANCIA = new CatalogoFabricas();

    /** Tipos de servicio que ofrece TechSolutions actualmente. */
    private final List<FabricaServicio> fabricas = List.of(
            new FabricaMantenimientoPreventivo(),
            new FabricaMantenimientoCorrectivo(),
            new FabricaInstalacionRedes(),
            new FabricaRecuperacionInformacion()
    );

    /** Constructor privado: nadie mas puede crear instancias. */
    private CatalogoFabricas() {
    }

    /** @return la instancia unica del registro de fabricas */
    public static CatalogoFabricas obtenerInstancia() {
        return INSTANCIA;
    }

    /** @return lista inmutable con las fabricas registradas */
    public List<FabricaServicio> listar() {
        return fabricas;
    }
}
