package com.techsolutions.vista;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

/**
 * Ventana principal de la aplicacion: agrupa las cuatro vistas, una por cada
 * funcionalidad requerida en el parcial.
 *
 * <p>Es la raiz de la capa <b>Vista</b>. No conoce los controladores ni los
 * servicios: la clase {@code ControladorPrincipal} la recibe, construye los
 * controladores y los enlaza con cada vista.</p>
 */
public class VistaPrincipal extends TabPane {

    private final VistaServicios vistaServicios = new VistaServicios();
    private final VistaPaquetes vistaPaquetes = new VistaPaquetes();
    private final VistaSolicitudes vistaSolicitudes = new VistaSolicitudes();
    private final VistaTecnicos vistaTecnicos = new VistaTecnicos();

    /**
     * Construye la ventana principal con las cuatro pestanas.
     */
    public VistaPrincipal() {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        getTabs().addAll(
                new Tab("1. Servicios", vistaServicios),
                new Tab("2. Paquetes", vistaPaquetes),
                new Tab("3. Solicitudes", vistaSolicitudes),
                new Tab("4. Tecnicos", vistaTecnicos));
    }

    public VistaServicios getVistaServicios() {
        return vistaServicios;
    }

    public VistaPaquetes getVistaPaquetes() {
        return vistaPaquetes;
    }

    public VistaSolicitudes getVistaSolicitudes() {
        return vistaSolicitudes;
    }

    public VistaTecnicos getVistaTecnicos() {
        return vistaTecnicos;
    }
}
