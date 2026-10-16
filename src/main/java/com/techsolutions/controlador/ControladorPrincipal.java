package com.techsolutions.controlador;

import com.techsolutions.integracion.AdaptadorLegacySupport;
import com.techsolutions.integracion.LegacySupport;
import com.techsolutions.modelo.ProveedorTecnicos;
import com.techsolutions.servicio.RepositorioSolicitudes;
import com.techsolutions.servicio.ServicioCatalogo;
import com.techsolutions.servicio.ServicioPaquetes;
import com.techsolutions.servicio.ServicioPersonalizacion;
import com.techsolutions.servicio.ServicioSolicitudes;
import com.techsolutions.vista.VistaPrincipal;

/**
 * Controlador principal y <b>punto de armado</b> de la aplicacion
 * (<i>composition root</i>).
 *
 * <p>Aqui se crean las dependencias del sistema y se inyectan en cada
 * controlador de pestana:</p>
 * <ul>
 *   <li>Servicios de aplicacion: catalogo, personalizacion, paquetes y
 *       solicitudes.</li>
 *   <li>Repositorios en memoria: solicitudes.</li>
 *   <li>Integracion: la plataforma externa {@code LegacySupport} y su adaptador,
 *       que se entrega <b>como interfaz</b> {@code ProveedorTecnicos}.</li>
 * </ul>
 *
 * <p>Este unico lugar concentra la creacion de objetos concretos, lo que
 * mantiene el resto de las clases desacopladas y facilita cambiar cualquier
 * implementacion (por ejemplo, otro proveedor de tecnicos).</p>
 */
public class ControladorPrincipal {

    private final VistaPrincipal vista;

    // ------------------------ servicios de aplicacion ------------------------
    private final ServicioCatalogo servicioCatalogo;
    private final ServicioPersonalizacion servicioPersonalizacion;
    private final ServicioPaquetes servicioPaquetes;
    private final ServicioSolicitudes servicioSolicitudes;

    // ------------------------------ integracion ------------------------------
    private final AdaptadorLegacySupport adaptadorTecnicos;
    private final ProveedorTecnicos proveedorTecnicos;

    /**
     * Crea los servicios y arma el sistema MVC completo.
     *
     * @param vista ventana principal de la aplicacion
     */
    public ControladorPrincipal(VistaPrincipal vista) {
        this.vista = vista;

        // 1) Integracion con la plataforma externa (patron Adapter).
        LegacySupport legacySupport = new LegacySupport();
        this.adaptadorTecnicos = new AdaptadorLegacySupport(legacySupport);
        this.proveedorTecnicos = adaptadorTecnicos;

        // 2) Servicios de aplicacion (fachadas de las funcionalidades).
        this.servicioCatalogo = new ServicioCatalogo();
        this.servicioPersonalizacion = new ServicioPersonalizacion();
        this.servicioPaquetes = new ServicioPaquetes();
        this.servicioSolicitudes = new ServicioSolicitudes(new RepositorioSolicitudes(), proveedorTecnicos);
    }

    /**
     * Crea los controladores de cada pestana y los enlaza con las vistas.
     */
    public void iniciar() {
        new ControladorServicios(vista.getVistaServicios(), servicioCatalogo, servicioPersonalizacion).iniciar();
        new ControladorPaquetes(vista.getVistaPaquetes(), servicioCatalogo, servicioPersonalizacion,
                servicioPaquetes).iniciar();
        new ControladorSolicitudes(vista.getVistaSolicitudes(), servicioCatalogo, servicioPersonalizacion,
                servicioPaquetes, servicioSolicitudes).iniciar();
        new ControladorTecnicos(vista.getVistaTecnicos(), proveedorTecnicos,
                adaptadorTecnicos::getUltimaRespuestaCruda).iniciar();
    }
}
