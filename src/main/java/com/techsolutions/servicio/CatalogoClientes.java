package com.techsolutions.servicio;

import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.modelo.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Clientes precargados en el sistema.
 *
 * <p>El enunciado indica que <i>"los clientes podran encontrarse precargados en
 * el sistema"</i> y que <b>no se requiere</b> implementar su gestion completa
 * (CRUD). Por eso esta clase solo ofrece los clientes de ejemplo que usan las
 * solicitudes.</p>
 *
 * <p>Implementa el patron <b>Singleton</b>: la lista de clientes es unica para
 * toda la aplicacion.</p>
 */
public final class CatalogoClientes {

    /** Instancia unica. */
    private static final CatalogoClientes INSTANCIA = new CatalogoClientes();

    /** Clientes precargados. */
    private final List<Cliente> clientes = new ArrayList<>();

    /** Constructor privado: patron Singleton. */
    private CatalogoClientes() {
        precargar();
    }

    /** @return la instancia unica del catalogo de clientes */
    public static CatalogoClientes obtenerInstancia() {
        return INSTANCIA;
    }

    /**
     * Crea los clientes de ejemplo.
     *
     * <p>Los datos son correctos por definicion; si alguno no lo fuera, el
     * programa fallaria al iniciar indicando un error de configuracion.</p>
     */
    private void precargar() {
        try {
            clientes.add(new Cliente("1020304050", "Ana Maria Gomez", "ana.gomez@correo.com"));
            clientes.add(new Cliente("1030405060", "Carlos Perez Rios", "carlos.perez@correo.com"));
            clientes.add(new Cliente("1040506070", "Laura Torres", "laura.torres@correo.com"));
        } catch (ValidacionException e) {
            throw new IllegalStateException("Error de configuracion: cliente precargado invalido.", e);
        }
    }

    /** @return lista inmutable con los clientes precargados */
    public List<Cliente> listar() {
        return List.copyOf(clientes);
    }
}
