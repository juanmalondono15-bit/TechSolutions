package com.techsolutions.consola;

import com.techsolutions.excepciones.PaqueteCiclicoException;
import com.techsolutions.excepciones.TechSolutionsException;
import com.techsolutions.excepciones.TecnicoNoDisponibleException;
import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.fabricas.CatalogoComplementos;
import com.techsolutions.fabricas.FabricaComplemento;
import com.techsolutions.fabricas.FabricaServicio;
import com.techsolutions.integracion.AdaptadorLegacySupport;
import com.techsolutions.integracion.LegacySupport;
import com.techsolutions.modelo.Cliente;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.ConsultaTecnicos;
import com.techsolutions.modelo.Especialidad;
import com.techsolutions.modelo.PaqueteTecnologico;
import com.techsolutions.modelo.Prioridad;
import com.techsolutions.modelo.ServicioTecnico;
import com.techsolutions.modelo.Solicitud;
import com.techsolutions.modelo.TecnicoEspecializado;
import com.techsolutions.modelo.complemento.ComplementoServicio;
import com.techsolutions.servicio.CatalogoClientes;
import com.techsolutions.servicio.CatalogoServicios;
import com.techsolutions.servicio.RepositorioSolicitudes;
import com.techsolutions.servicio.ServicioCatalogo;
import com.techsolutions.servicio.ServicioPaquetes;
import com.techsolutions.servicio.ServicioPersonalizacion;
import com.techsolutions.servicio.ServicioSolicitudes;
import com.techsolutions.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Demostracion por consola de las cuatro funcionalidades del parcial.
 *
 * <p>Esta clase <b>no</b> usa JavaFX. Se incluye como apoyo para:</p>
 * <ul>
 *   <li>Verificar rapidamente que la logica del modelo y los servicios de
 *       aplicacion funcionan sin abrir la interfaz grafica.</li>
 *   <li>Tener una evidencia ejecutable del comportamiento del sistema
 *       (catalogos, complementos, paquetes, solicitudes y la integracion con
 *       la plataforma externa {@code LegacySupport}).</li>
 * </ul>
 *
 * <p>La aplicacion con interfaz grafica es {@code com.techsolutions.Main}. Para
 * ejecutar esta demo, ver las instrucciones del {@code README.md}.</p>
 */
public final class DemoConsola {

    /** Clase solo de demostracion: no se instancia. */
    private DemoConsola() {
    }

    /** Contador de comprobaciones exitosas (sirve como evidencia en consola). */
    private static int comprobacionesOk = 0;

    /**
     * Punto de entrada de la demostracion.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        System.out.println("================================================================");
        System.out.println("  TECHSOLUTIONS - DEMO POR CONSOLA (sin interfaz grafica)");
        System.out.println("  Parcial II - Programacion II");
        System.out.println("================================================================");

        demostrarFuncionalidad1ServiciosYPersistencia();
        demostrarFuncionalidad2Paquetes();
        demostrarFuncionalidad3Solicitudes();
        demostrarFuncionalidad4TecnicosYCostoTotal();

        System.out.println();
        System.out.println("================================================================");
        System.out.println("  DEMO FINALIZADA. Comprobaciones correctas: " + comprobacionesOk);
        System.out.println("================================================================");
    }

    // =========================================================================
    //  FUNCIONALIDAD 1: gestion y personalizacion de servicios
    // =========================================================================

    /**
     * Consulta el catalogo, crea un servicio con su fabrica (Factory Method) y
     * aplica caracteristicas complementarias (Decorator), demostrando que el
     * servicio original del catalogo no se modifica.
     */
    private static void demostrarFuncionalidad1ServiciosYPersistencia() {
        titulo("FUNCIONALIDAD 1: Gestion y personalizacion de servicios");

        ServicioCatalogo servicioCatalogo = new ServicioCatalogo();
        ServicioPersonalizacion personalizacion = new ServicioPersonalizacion();

        System.out.println(">> Servicios precargados en el catalogo (patron Singleton):");
        for (ServicioTecnico servicio : servicioCatalogo.listarDisponibles()) {
            System.out.printf("   %-8s %-30s %12s   presencial=%s%n",
                    servicio.getCodigo(),
                    servicio.getNombre(),
                    Formato.moneda(servicio.getCostoBase()),
                    servicio.requiereAtencionPresencial() ? "si" : "no");
        }

        System.out.println();
        System.out.println(">> Tipos de servicio que se pueden crear (cada uno tiene su fabrica):");
        for (FabricaServicio fabrica : servicioCatalogo.listarTiposDeServicio()) {
            System.out.println("   - " + fabrica.getTipo()
                    + " (codigo sugerido " + fabrica.getCodigoSugerido()
                    + ", costo base " + Formato.moneda(fabrica.getCostoBaseSugerido()) + ")");
        }

        // ---- Crear un servicio nuevo usando una fabrica (Factory Method) ----
        System.out.println();
        System.out.println(">> Crear un servicio nuevo con la fabrica de instalacion de redes...");
        try {
            FabricaServicio fabricaRedes = servicioCatalogo.listarTiposDeServicio().stream()
                    .filter(f -> f.getCodigoSugerido().equals("SRV-RED"))
                    .findFirst()
                    .orElseThrow();
            ServicioTecnico redesCorporativas = servicioCatalogo.crearServicio(
                    fabricaRedes, "SRV-RED-EMP", "Red empresarial", "Red para oficina nueva", 210_000);
            System.out.println("   Servicio creado: " + redesCorporativas
                    + " por " + Formato.moneda(redesCorporativas.getCostoBase()));
            afirmar(servicioCatalogo.listarDisponibles().contains(redesCorporativas),
                    "El servicio creado quedo registrado en el catalogo.");
        } catch (ValidacionException e) {
            System.out.println("   Error inesperado: " + e.getMessage());
        }

        // ---- Personalizar un servicio: costo base, incrementos y costo final ----
        // Se toma el mantenimiento correctivo del catalogo (sin modificar la definicion).
        ServicioTecnico correctivo = CatalogoServicios.obtenerInstancia().buscar("SRV-COR").orElseThrow();
        double costoBaseOriginal = correctivo.getCostoBase();
        double costoCatalogoAntes = correctivo.calcularCosto();

        System.out.println();
        System.out.println(">> Personalizar \"" + correctivo.getNombre() + "\" con dos complementos:");
        System.out.println("   Costo base del servicio: " + Formato.moneda(costoBaseOriginal));

        List<FabricaComplemento> complementos = personalizacion.listarComplementosDisponibles();
        FabricaComplemento prioritaria = buscarComplemento(complementos, "Atencion prioritaria");
        FabricaComplemento soporte = buscarComplemento(complementos, "Soporte tecnico 30 dias");

        ComponenteServicio personalizado = correctivo;
        try {
            personalizado = personalizacion.aplicarComplemento(personalizado, prioritaria);
            System.out.println("   + " + prioritaria.getNombre() + ": "
                    + Formato.moneda(prioritaria.getIncremento()));
            personalizado = personalizacion.aplicarComplemento(personalizado, soporte);
            System.out.println("   + " + soporte.getNombre() + ": "
                    + Formato.moneda(soporte.getIncremento()));
            personalizacion.guardarPersonalizado(personalizado);
        } catch (ValidacionException e) {
            System.out.println("   Error inesperado: " + e.getMessage());
        }

        System.out.println("   Nombre del servicio personalizado: " + personalizado.getNombre());
        System.out.println("   COSTO FINAL personalizado: " + Formato.moneda(personalizado.calcularCosto()));

        // ---- Demostrar que el catalogo NO se modifico ----
        double costoCatalogoDespues = correctivo.calcularCosto();
        System.out.println();
        afirmar(costoBaseOriginal == correctivo.getCostoBase()
                        && costoCatalogoAntes == costoCatalogoDespues,
                "La definicion original del catalogo NO se modifico (Decorator).");
        System.out.println("   Costo del servicio en el catalogo: " + Formato.moneda(costoCatalogoDespues)
                + " (sigue igual)");

        // ---- TypeError controlado: no se puede personalizar sin complemento ----
        try {
            personalizacion.aplicarComplemento(correctivo, null);
            System.out.println("   (No deberia llegar aqui)");
        } catch (ValidacionException e) {
            afirmar(true, "Se valido el error: " + e.getMessage());
        }
    }

    // =========================================================================
    //  FUNCIONALIDAD 2: configuracion de paquetes tecnologicos (Composite)
    // =========================================================================

    /**
     * Crea paquetes anidados (Composite), calcula su costo total y demuestra que
     * el sistema impide incluir un paquete dentro de si mismo.
     */
    private static void demostrarFuncionalidad2Paquetes() {
        titulo("FUNCIONALIDAD 2: Configuracion de paquetes tecnologicos (Composite)");

        ServicioPaquetes servicioPaquetes = new ServicioPaquetes();
        CatalogoServicios catalogo = CatalogoServicios.obtenerInstancia();

        ServicioTecnico preventivo = catalogo.buscar("SRV-PRE").orElseThrow();
        ServicioTecnico correctivo = catalogo.buscar("SRV-COR").orElseThrow();
        ServicioTecnico redes = catalogo.buscar("SRV-RED").orElseThrow();
        ServicioTecnico recuperacion = catalogo.buscar("SRV-REC").orElseThrow();

        try {
            // Paquete interno (paquete dentro de paquete -> varios niveles).
            PaqueteTecnologico mantenimiento = servicioPaquetes.crear(
                    "PQ-MANT", "Paquete mantenimiento", "Preventivo y correctivo");
            servicioPaquetes.agregarComponente(mantenimiento, preventivo);
            servicioPaquetes.agregarComponente(mantenimiento, correctivo);

            // Paquete empresarial que contiene el paquete anterior.
            PaqueteTecnologico empresarial = servicioPaquetes.crear(
                    "PQ-EMP", "Paquete empresarial", "Mantenimiento, redes y recuperacion");
            servicioPaquetes.agregarComponente(empresarial, mantenimiento);
            servicioPaquetes.agregarComponente(empresarial, redes);
            servicioPaquetes.agregarComponente(empresarial, recuperacion);

            System.out.println(">> Composicion y costo del paquete empresarial (consulta jerarquica):");
            for (String linea : servicioPaquetes.describirComposicion(empresarial)) {
                System.out.println("   " + linea);
            }

            double esperado = preventivo.calcularCosto() + correctivo.calcularCosto()
                    + redes.calcularCosto() + recuperacion.calcularCosto();
            afirmar(Math.abs(servicioPaquetes.calcularCostoTotal(empresarial) - esperado) < 0.001,
                    "El costo total del paquete suma todos los niveles: " + Formato.moneda(esperado));

            // ---- Impedir ciclos (paquete dentro de si mismo) ----
            System.out.println();
            System.out.println(">> Intentar meter el paquete empresarial dentro del paquete mantenimiento...");
            try {
                servicioPaquetes.agregarComponente(mantenimiento, empresarial);
                System.out.println("   (No deberia llegar aqui: no se detecto el ciclo)");
            } catch (PaqueteCiclicoException e) {
                afirmar(true, "Ciclo bloqueado correctamente: " + e.getMessage());
            }
        } catch (ValidacionException | PaqueteCiclicoException e) {
            System.out.println("   Error inesperado: " + e.getMessage());
        }
    }

    // =========================================================================
    //  FUNCIONALIDAD 3: registro de solicitudes (Builder)
    // =========================================================================

    /**
     * Registra solicitudes validando los datos obligatorios (Builder) y muestra
     * que no es posible crear una solicitud sin la informacion esencial.
     */
    private static void demostrarFuncionalidad3Solicitudes() {
        titulo("FUNCIONALIDAD 3: Registro de solicitudes de servicios (Builder)");

        ServicioPersonalizacion personalizacion = new ServicioPersonalizacion();
        CatalogoServicios catalogo = CatalogoServicios.obtenerInstancia();
        List<Cliente> clientes = CatalogoClientes.obtenerInstancia().listar();

        System.out.println(">> Clientes precargados en el sistema:");
        for (Cliente cliente : clientes) {
            System.out.println("   - " + cliente.getNombreCompleto() + " (" + cliente.getDocumento() + ")");
        }

        AdaptadorLegacySupport adaptador = new AdaptadorLegacySupport(new LegacySupport());
        ServicioSolicitudes servicioSolicitudes =
                new ServicioSolicitudes(new RepositorioSolicitudes(), adaptador);

        try {
            // Servicio personalizado reutilizable (correctivo + prioritaria + soporte).
            ServicioTecnico correctivo = catalogo.buscar("SRV-COR").orElseThrow();
            List<FabricaComplemento> complementos = personalizacion.listarComplementosDisponibles();
            ComponenteServicio correctivoPersonalizado = personalizacion.aplicarComplemento(
                    correctivo, buscarComplemento(complementos, "Atencion prioritaria"));

            // ---- Intento invalido: sin elementos (debe fallar) ----
            System.out.println();
            System.out.println(">> Intentar registrar una solicitud SIN elementos...");
            try {
                servicioSolicitudes.registrar(clientes.get(0), new ArrayList<>(),
                        null, null, null);
                System.out.println("   (No deberia llegar aqui)");
            } catch (ValidacionException e) {
                afirmar(true, "Registro bloqueado: " + e.getMessage());
            }

            // ---- Registro valido con datos opcionales ----
            List<ComponenteServicio> elementos = new ArrayList<>();
            elementos.add(correctivoPersonalizado);
            Solicitud solicitud = servicioSolicitudes.registrar(
                    clientes.get(0),
                    elementos,
                    LocalDate.now().plusDays(3),
                    "Equipo de la sala de sistemas",
                    Prioridad.ALTA);

            System.out.println();
            System.out.println(">> Solicitud registrada con codigo unico generado por el sistema:");
            System.out.printf("   Codigo: %s | Cliente: %s | Estado: %s%n",
                    solicitud.getCodigo(),
                    solicitud.getCliente().getNombreCompleto(),
                    solicitud.getEstado().getNombreVisible());
            System.out.println("   Fecha programada (opcional): " + Formato.fecha(solicitud.getFechaProgramada()));
            System.out.println("   Observaciones (opcional): " + solicitud.getObservaciones());
            System.out.println("   Prioridad (opcional): " + solicitud.getPrioridad().getNombreVisible());
            System.out.println("   Elementos contratados: " + solicitud.getCodigo()
                    + " -> " + solicitud.getElementos().size() + " elemento(s)");
            System.out.println("   Costo de los elementos: " + Formato.moneda(solicitud.calcularCostoElementos()));

            afirmar(solicitud.getCodigo() != null && !solicitud.getCodigo().isBlank(),
                    "La solicitud tiene un codigo unico.");
            afirmar(!solicitud.getElementos().isEmpty(),
                    "La solicitud conserva los elementos contratados.");
        } catch (ValidacionException e) {
            System.out.println("   Error inesperado: " + e.getMessage());
        }
    }

    // =========================================================================
    //  FUNCIONALIDAD 4: consulta de tecnicos y costo total (Adapter + SOLID)
    // =========================================================================

    /**
     * Consulta la plataforma externa {@code LegacySupport} a traves del
     * adaptador, muestra la respuesta cruda frente a la informacion
     * estructurada y comprueba las reglas de confirmacion y de costo total sin
     * cargos duplicados.
     */
    private static void demostrarFuncionalidad4TecnicosYCostoTotal() {
        titulo("FUNCIONALIDAD 4: Consulta de tecnicos y costo total de la solicitud");

        LegacySupport legacy = new LegacySupport();
        AdaptadorLegacySupport adaptador = new AdaptadorLegacySupport(legacy);

        // ---- Respuesta cruda vs informacion estructurada (patron Adapter) ----
        System.out.println(">> Traduccion que hace el adaptador (patron Adapter):");
        for (Especialidad especialidad : Especialidad.values()) {
            System.out.printf("   Legado recibe codigo %d -> responde \"%s\"%n",
                    especialidad.getCodigoLegacy(),
                    legacy.consultarDisponibilidadTecnico(especialidad.getCodigoLegacy()));
            TecnicoEspecializado tecnico = adaptador.consultar(especialidad);
            System.out.printf("      Sistema nuevo recibe: id=%s, especialidad=%s, disponible=%s, desplazamiento=%s%n",
                    tecnico.getIdentificador(),
                    tecnico.getEspecialidad().getNombreVisible(),
                    tecnico.isDisponible(),
                    Formato.moneda(tecnico.getCostoDesplazamiento()));
        }
        afirmar(adaptador.getUltimaRespuestaCruda() != null,
                "El adaptador expone la ultima respuesta cruda del legado para la interfaz.");

        // ---- Preparar solicitudes ----
        CatalogoServicios catalogo = CatalogoServicios.obtenerInstancia();
        List<Cliente> clientes = CatalogoClientes.obtenerInstancia().listar();
        ServicioPersonalizacion personalizacion = new ServicioPersonalizacion();
        ServicioSolicitudes servicioSolicitudes =
                new ServicioSolicitudes(new RepositorioSolicitudes(), adaptador);

        try {
            ServicioTecnico correctivo = catalogo.buscar("SRV-COR").orElseThrow();
            List<FabricaComplemento> complementos = personalizacion.listarComplementosDisponibles();
            ComponenteServicio correctivoConDomicilio = personalizacion.aplicarComplemento(
                    correctivo, buscarComplemento(complementos, "Atencion a domicilio"));

            // ---- Caso 1: solicitud presencial con tecnico disponible ----
            List<ComponenteServicio> elementos1 = new ArrayList<>();
            elementos1.add(correctivoConDomicilio);
            Solicitud solicitud1 = servicioSolicitudes.registrar(
                    clientes.get(1), elementos1, LocalDate.now().plusDays(2), null, Prioridad.MEDIA);

            System.out.println();
            System.out.println(">> Caso 1: solicitud " + solicitud1.getCodigo()
                    + " requiere atencion presencial (" + correctivoConDomicilio.getNombre() + ").");
            ConsultaTecnicos consulta1 = servicioSolicitudes.consultarTecnicos(solicitud1);
            System.out.println("   Consulta: " + consulta1.getMensaje());

            double costoElementos1 = solicitud1.calcularCostoElementos();
            servicioSolicitudes.confirmar(solicitud1);
            System.out.println("   Estado tras confirmar: " + solicitud1.getEstado().getNombreVisible());
            System.out.println("   Costo de elementos: " + Formato.moneda(costoElementos1));
            System.out.println("   Desplazamiento (una sola vez): " + Formato.moneda(solicitud1.getCostoDesplazamiento()));
            System.out.println("   COSTO TOTAL: " + Formato.moneda(solicitud1.calcularCostoTotal()));

            afirmar(solicitud1.getEstado() == com.techsolutions.modelo.EstadoSolicitud.CONFIRMADA,
                    "La solicitud con tecnico disponible quedo CONFIRMADA.");
            afirmar(Math.abs(solicitud1.calcularCostoTotal()
                            - (costoElementos1 + solicitud1.getCostoDesplazamiento())) < 0.001,
                    "El costo total suma elementos + un unico desplazamiento (sin duplicados).");

            // ---- Caso 2: solicitud presencial SIN tecnico disponible ----
            ServicioTecnico recuperacion = catalogo.buscar("SRV-REC").orElseThrow();
            List<ComponenteServicio> elementos2 = new ArrayList<>();
            elementos2.add(recuperacion);
            Solicitud solicitud2 = servicioSolicitudes.registrar(
                    clientes.get(2), elementos2, null, null, Prioridad.ALTA);

            System.out.println();
            System.out.println(">> Caso 2: solicitud " + solicitud2.getCodigo()
                    + " (" + recuperacion.getNombre() + "). El legado no reporta tecnico.");
            try {
                servicioSolicitudes.confirmar(solicitud2);
                System.out.println("   (No deberia llegar aqui)");
            } catch (TecnicoNoDisponibleException e) {
                System.out.println("   Aviso del sistema: " + e.getMessage());
            }
            System.out.println("   Estado: " + solicitud2.getEstado().getNombreVisible());
            System.out.println("   Desplazamiento cobrado: " + Formato.moneda(solicitud2.getCostoDesplazamiento()));

            afirmar(solicitud2.getEstado() == com.techsolutions.modelo.EstadoSolicitud.NO_CONFIRMADA,
                    "El sistema informo la situacion y evito la confirmacion.");
            afirmar(solicitud2.getCostoDesplazamiento() == 0,
                    "No se cobro desplazamiento cuando no hubo tecnico.");

            // ---- Caso 3: solicitud NO presencial (no consulta tecnicos) ----
            ServicioTecnico preventivo = catalogo.buscar("SRV-PRE").orElseThrow();
            List<ComponenteServicio> elementos3 = new ArrayList<>();
            elementos3.add(preventivo);
            Solicitud solicitud3 = servicioSolicitudes.registrar(
                    clientes.get(0), elementos3, null, "Revision remota", null);
            ConsultaTecnicos consulta3 = servicioSolicitudes.consultarTecnicos(solicitud3);

            System.out.println();
            System.out.println(">> Caso 3: solicitud " + solicitud3.getCodigo()
                    + " (" + preventivo.getNombre() + ", no presencial).");
            System.out.println("   Consulta: " + consulta3.getMensaje());
            servicioSolicitudes.confirmar(solicitud3);
            System.out.println("   Estado: " + solicitud3.getEstado().getNombreVisible());
            System.out.println("   Desplazamiento: " + Formato.moneda(solicitud3.getCostoDesplazamiento()));
            System.out.println("   COSTO TOTAL: " + Formato.moneda(solicitud3.calcularCostoTotal()));

            afirmar(!solicitud3.requiereAtencionPresencial()
                            && solicitud3.getCostoDesplazamiento() == 0,
                    "La solicitud no presencial no genera costo de desplazamiento.");

            // ---- Resumen de solicitudes registradas ----
            System.out.println();
            System.out.println(">> Resumen de solicitudes registradas:");
            for (Solicitud s : servicioSolicitudes.listar()) {
                System.out.printf("   %-8s %-22s estado=%-14s total=%s%n",
                        s.getCodigo(),
                        s.getCliente().getNombreCompleto(),
                        s.getEstado().getNombreVisible(),
                        Formato.moneda(s.calcularCostoTotal()));
            }
        } catch (TechSolutionsException e) {
            System.out.println("   Error inesperado: " + e.getMessage());
        }
    }

    // =========================================================================
    //  Utilidades de la demo
    // =========================================================================

    /** Busca un complemento por su nombre entre las fabricas registradas. */
    private static FabricaComplemento buscarComplemento(List<FabricaComplemento> complementos, String nombre) {
        return complementos.stream()
                .filter(c -> c.getNombre().equals(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Complemento no registrado: " + nombre));
    }

    /** Imprime un titulo de seccion. */
    private static void titulo(String texto) {
        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.println("  " + texto);
        System.out.println("----------------------------------------------------------------");
    }

    /** Marca una comprobacion como correcta y la imprime. */
    private static void afirmar(boolean condicion, String mensaje) {
        if (condicion) {
            comprobacionesOk++;
        }
        System.out.println("   [" + (condicion ? "OK" : "FALLO") + "] " + mensaje);
    }
}
