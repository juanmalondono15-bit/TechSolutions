package com.techsolutions.servicio;

import com.techsolutions.excepciones.TecnicoNoDisponibleException;
import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.modelo.Cliente;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.ConsultaTecnicos;
import com.techsolutions.modelo.Especialidad;
import com.techsolutions.modelo.Prioridad;
import com.techsolutions.modelo.ProveedorTecnicos;
import com.techsolutions.modelo.Solicitud;
import com.techsolutions.modelo.TecnicoEspecializado;
import com.techsolutions.util.Formato;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicacion que atiende las funcionalidades <b>"Registro de
 * solicitudes de servicios"</b> y <b>"Consulta de tecnicos y calculo del costo
 * de las solicitudes"</b> (tercera y cuarta funcionalidad requerida).
 *
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Registrar solicitudes validando la informacion obligatoria (patron
 *       <b>Builder</b>) y asignando un codigo unico.</li>
 *   <li>Consultar la disponibilidad de tecnicos a traves de la interfaz
 *       {@link ProveedorTecnicos} (la implementacion concreta es el adaptador
 *       de {@code LegacySupport}).</li>
 *   <li>Confirmar la solicitud solo si hay tecnicos disponibles y calcular el
 *       costo total, incluyendo una sola vez el costo de desplazamiento.</li>
 * </ul>
 *
 * <p><b>Inversion de dependencias:</b> esta clase recibe un
 * {@code ProveedorTecnicos} en el constructor. Gracias a eso no conoce
 * {@code LegacySupport} ni sus cadenas de texto: se le puede inyectar
 * cualquier otro proveedor en el futuro.</p>
 */
public class ServicioSolicitudes {

    /** Almacen de solicitudes registradas. */
    private final RepositorioSolicitudes repositorio;

    /** Puerto hacia el proveedor de tecnicos (patron Adapter al otro lado). */
    private final ProveedorTecnicos proveedorTecnicos;

    /**
     * @param repositorio       almacen donde se guardan las solicitudes
     * @param proveedorTecnicos proveedor de tecnicos a consultar
     */
    public ServicioSolicitudes(RepositorioSolicitudes repositorio, ProveedorTecnicos proveedorTecnicos) {
        this.repositorio = repositorio;
        this.proveedorTecnicos = proveedorTecnicos;
    }

    /**
     * Registra una solicitud.
     *
     * @param cliente         cliente que contrata (obligatorio)
     * @param elementos       servicios, servicios personalizados o paquetes (al menos uno)
     * @param fechaProgramada fecha programada de atencion (opcional, puede ser null)
     * @param observaciones   observaciones tecnicas (opcional, puede ser null)
     * @param prioridad       prioridad (opcional; si es null se usa MEDIA)
     * @return la solicitud registrada con su codigo unico
     * @throws ValidacionException si falta la informacion esencial
     */
    public Solicitud registrar(Cliente cliente, List<ComponenteServicio> elementos,
                               LocalDate fechaProgramada, String observaciones, Prioridad prioridad)
            throws ValidacionException {

        if (cliente == null) {
            throw new ValidacionException("Debe seleccionar el cliente que realiza la solicitud.");
        }
        if (elementos == null || elementos.isEmpty()) {
            throw new ValidacionException("Debe agregar al menos un servicio o paquete a la solicitud.");
        }

        // Codigo unico generado por el repositorio (por ejemplo SOL-001).
        String codigo = repositorio.generarCodigo();

        // El Builder valida y arma la solicitud paso a paso.
        Solicitud.Builder builder = new Solicitud.Builder(codigo, cliente);
        for (ComponenteServicio elemento : elementos) {
            builder.agregarElemento(elemento);
        }
        Solicitud solicitud = builder
                .conFechaProgramada(fechaProgramada)
                .conObservaciones(observaciones)
                .conPrioridad(prioridad)
                .construir();

        repositorio.agregar(solicitud);
        return solicitud;
    }

    /** @return solicitudes registradas */
    public List<Solicitud> listar() {
        return repositorio.listar();
    }

    /**
     * Consulta los tecnicos necesarios para atender una solicitud.
     *
     * <p>Si la solicitud no requiere atencion presencial no se consulta nada.
     * Si la requiere, se consulta una especialidad a la vez a traves del
     * proveedor y el costo de desplazamiento se toma <b>una sola vez</b> (el
     * valor mas alto reportado), evitando cargos duplicados.</p>
     *
     * @param solicitud solicitud a analizar
     * @return resultado de la consulta con tecnicos, costo de desplazamiento y mensaje
     */
    public ConsultaTecnicos consultarTecnicos(Solicitud solicitud) {
        if (solicitud == null) {
            return new ConsultaTecnicos(List.of(), 0, false, "No hay solicitud seleccionada.");
        }
        if (!solicitud.requiereAtencionPresencial()) {
            return new ConsultaTecnicos(List.of(), 0, true,
                    "La solicitud no requiere atencion presencial, no se consultan tecnicos.");
        }

        List<TecnicoEspecializado> tecnicos = new ArrayList<>();
        List<String> noDisponibles = new ArrayList<>();
        double costoDesplazamiento = 0;
        boolean todosDisponibles = true;

        for (Especialidad especialidad : solicitud.getEspecialidadesRequeridas()) {
            TecnicoEspecializado tecnico = proveedorTecnicos.consultar(especialidad);
            tecnicos.add(tecnico);
            if (tecnico.isDisponible()) {
                // Un solo desplazamiento por solicitud: se conserva el mayor valor.
                costoDesplazamiento = Math.max(costoDesplazamiento, tecnico.getCostoDesplazamiento());
            } else {
                todosDisponibles = false;
                noDisponibles.add(especialidad.getNombreVisible());
            }
        }

        String mensaje;
        if (todosDisponibles) {
            mensaje = "Tecnicos disponibles para todas las especialidades requeridas. "
                    + "Costo unico de desplazamiento: " + Formato.moneda(costoDesplazamiento) + ".";
        } else {
            mensaje = "No hay tecnico disponible para: " + String.join(", ", noDisponibles)
                    + ". La solicitud no puede confirmarse.";
            costoDesplazamiento = 0;
        }
        return new ConsultaTecnicos(tecnicos, costoDesplazamiento, todosDisponibles, mensaje);
    }

    /**
     * Confirma una solicitud.
     *
     * <p>Antes de confirmar verifica la disponibilidad de tecnicos cuando la
     * solicitud requiere atencion presencial. Si no hay tecnico, la solicitud
     * queda en estado {@code NO_CONFIRMADA} y <b>no</b> se cobra desplazamiento.</p>
     *
     * @param solicitud solicitud a confirmar
     * @return la misma solicitud, ya confirmada
     * @throws TecnicoNoDisponibleException si alguna especialidad no tiene tecnico
     */
    public Solicitud confirmar(Solicitud solicitud) throws TecnicoNoDisponibleException {
        if (solicitud == null) {
            throw new TecnicoNoDisponibleException("No hay solicitud seleccionada para confirmar.");
        }

        ConsultaTecnicos consulta = consultarTecnicos(solicitud);
        if (!consulta.isTodosDisponibles()) {
            solicitud.marcarNoConfirmada();
            throw new TecnicoNoDisponibleException(consulta.getMensaje());
        }

        TecnicoEspecializado tecnico = consulta.getTecnicos().stream()
                .filter(TecnicoEspecializado::isDisponible)
                .findFirst()
                .orElse(null);

        solicitud.confirmar(consulta.getCostoDesplazamiento(), tecnico);
        return solicitud;
    }
}
