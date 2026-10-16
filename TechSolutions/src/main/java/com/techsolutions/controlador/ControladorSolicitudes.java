package com.techsolutions.controlador;

import com.techsolutions.excepciones.TecnicoNoDisponibleException;
import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.modelo.Cliente;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.ConsultaTecnicos;
import com.techsolutions.modelo.Especialidad;
import com.techsolutions.modelo.Prioridad;
import com.techsolutions.modelo.Solicitud;
import com.techsolutions.modelo.TecnicoEspecializado;
import com.techsolutions.servicio.CatalogoClientes;
import com.techsolutions.servicio.ServicioCatalogo;
import com.techsolutions.servicio.ServicioPaquetes;
import com.techsolutions.servicio.ServicioPersonalizacion;
import com.techsolutions.servicio.ServicioSolicitudes;
import com.techsolutions.util.Formato;
import com.techsolutions.vista.Alertas;
import com.techsolutions.vista.VistaSolicitudes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Window;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de las funcionalidades 3 y 4: <b>registro de solicitudes</b> y
 * <b>consulta de tecnicos / calculo del costo</b>.
 *
 * <p>Capa <b>Controlador</b> del MVC. Se apoya en {@link ServicioSolicitudes}
 * para las reglas de negocio (validaciones, codigo unico, disponibilidad de
 * tecnicos y costo total) y solo se encarga de conectar la vista con ese
 * servicio.</p>
 */
public class ControladorSolicitudes {

    private final VistaSolicitudes vista;
    private final ServicioCatalogo servicioCatalogo;
    private final ServicioPersonalizacion servicioPersonalizacion;
    private final ServicioPaquetes servicioPaquetes;
    private final ServicioSolicitudes servicioSolicitudes;

    /** Elementos que el usuario va agregando a la solicitud que esta armando. */
    private final ObservableList<ComponenteServicio> elementosDeLaSolicitud = FXCollections.observableArrayList();

    /**
     * @param vista                    vista de la pestana de solicitudes
     * @param servicioCatalogo         servicios del catalogo
     * @param servicioPersonalizacion  servicios personalizados
     * @param servicioPaquetes         paquetes configurados
     * @param servicioSolicitudes      servicio de aplicacion de solicitudes
     */
    public ControladorSolicitudes(VistaSolicitudes vista, ServicioCatalogo servicioCatalogo,
                                  ServicioPersonalizacion servicioPersonalizacion,
                                  ServicioPaquetes servicioPaquetes,
                                  ServicioSolicitudes servicioSolicitudes) {
        this.vista = vista;
        this.servicioCatalogo = servicioCatalogo;
        this.servicioPersonalizacion = servicioPersonalizacion;
        this.servicioPaquetes = servicioPaquetes;
        this.servicioSolicitudes = servicioSolicitudes;
    }

    /**
     * Enlaza los eventos y carga los datos iniciales.
     */
    public void iniciar() {
        vista.getComboClientes().getItems()
                .setAll(FXCollections.observableArrayList(CatalogoClientes.obtenerInstancia().listar()));
        vista.getComboClientes().getSelectionModel().selectFirst();

        vista.getComboPrioridad().getItems().setAll(Prioridad.values());
        vista.getComboPrioridad().getSelectionModel().select(Prioridad.MEDIA);

        vista.getListaElementos().setItems(elementosDeLaSolicitud);

        actualizarElementosDisponibles();

        vista.getBotonAgregarElemento().setOnAction(evento -> agregarElemento());
        vista.getBotonQuitarElemento().setOnAction(evento -> quitarElemento());
        vista.getBotonRegistrar().setOnAction(evento -> registrarSolicitud());
        vista.getBotonLimpiar().setOnAction(evento -> limpiarFormulario());
        vista.getBotonConfirmar().setOnAction(evento -> confirmarSolicitud());
        vista.getBotonConsultarTecnicos().setOnAction(evento -> consultarTecnicos());
        vista.getTablaSolicitudes().getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, nueva) -> mostrarDetalle(nueva));

        actualizarTablaSolicitudes();
        actualizarCostoElementos();
    }

    // ------------------------------ acciones --------------------------------

    /** Agrega a la solicitud en construccion el elemento elegido. */
    private void agregarElemento() {
        ComponenteServicio elemento = vista.getComboElementos().getValue();
        if (elemento == null) {
            mostrarError("Seleccione un servicio o paquete para agregar.");
            return;
        }
        elementosDeLaSolicitud.add(elemento);
        actualizarCostoElementos();
    }

    /** Quita de la solicitud el elemento seleccionado en la lista. */
    private void quitarElemento() {
        ComponenteServicio seleccionado = vista.getListaElementos().getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            elementosDeLaSolicitud.remove(seleccionado);
            actualizarCostoElementos();
        }
    }

    /** Registra la solicitud con los datos del formulario. */
    private void registrarSolicitud() {
        Cliente cliente = vista.getComboClientes().getValue();
        LocalDate fechaProgramada = vista.getSelectorFechaProgramada().getValue();
        String observaciones = vista.getCampoObservaciones().getText();
        Prioridad prioridad = vista.getComboPrioridad().getValue();

        try {
            Solicitud solicitud = servicioSolicitudes.registrar(
                    cliente, new ArrayList<>(elementosDeLaSolicitud), fechaProgramada, observaciones, prioridad);

            actualizarTablaSolicitudes();
            vista.getTablaSolicitudes().getSelectionModel().select(solicitud);
            limpiarFormulario();

            Alertas.informacion(ventana(), "Solicitud registrada",
                    "Se registro la solicitud " + solicitud.getCodigo() + " para "
                            + solicitud.getCliente().getNombreCompleto() + ".\n"
                            + "Elementos contratados: " + solicitud.getElementos().size() + "\n"
                            + "Costo de los elementos: " + Formato.moneda(solicitud.calcularCostoElementos()) + "\n"
                            + (solicitud.requiereAtencionPresencial()
                            ? "Requiere atencion presencial: confirme para verificar tecnicos."
                            : "No requiere atencion presencial."));
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Confirma la solicitud verificando tecnicos cuando sea presencial. */
    private void confirmarSolicitud() {
        Solicitud solicitud = vista.getTablaSolicitudes().getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarError("Seleccione una solicitud de la tabla.");
            return;
        }
        try {
            servicioSolicitudes.confirmar(solicitud);
            actualizarTablaSolicitudes();
            mostrarDetalle(solicitud);
            Alertas.informacion(ventana(), "Solicitud confirmada",
                    "La solicitud " + solicitud.getCodigo() + " fue confirmada.\n"
                            + "Costo de elementos: " + Formato.moneda(solicitud.calcularCostoElementos()) + "\n"
                            + "Costo de desplazamiento: " + Formato.moneda(solicitud.getCostoDesplazamiento()) + "\n"
                            + "COSTO TOTAL: " + Formato.moneda(solicitud.calcularCostoTotal()));
        } catch (TecnicoNoDisponibleException e) {
            // Regla del enunciado: informar la situacion y evitar la confirmacion.
            actualizarTablaSolicitudes();
            mostrarDetalle(solicitud);
            Alertas.advertencia(ventana(), "No se pudo confirmar la solicitud", e.getMessage());
        }
    }

    /** Consulta los tecnicos requeridos y muestra el resultado. */
    private void consultarTecnicos() {
        Solicitud solicitud = vista.getTablaSolicitudes().getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            mostrarError("Seleccione una solicitud de la tabla.");
            return;
        }
        ConsultaTecnicos consulta = servicioSolicitudes.consultarTecnicos(solicitud);
        StringBuilder texto = new StringBuilder(vista.getDetalleSolicitud().getText());
        texto.append("\n\n--- CONSULTA DE TECNICOS ---\n");
        texto.append(consulta.getMensaje()).append('\n');
        for (TecnicoEspecializado tecnico : consulta.getTecnicos()) {
            texto.append("   * ").append(tecnico.getIdentificador())
                    .append(" (").append(tecnico.getEspecialidad().getNombreVisible()).append("): ")
                    .append(tecnico.isDisponible() ? "disponible" : "no disponible")
                    .append(", desplazamiento ").append(Formato.moneda(tecnico.getCostoDesplazamiento()))
                    .append('\n');
        }
        vista.getDetalleSolicitud().setText(texto.toString());

        if (consulta.isTodosDisponibles()) {
            Alertas.informacion(ventana(), "Consulta de tecnicos", consulta.getMensaje());
        } else {
            Alertas.advertencia(ventana(), "Consulta de tecnicos", consulta.getMensaje());
        }
    }

    /** Limpia el formulario de registro. */
    private void limpiarFormulario() {
        elementosDeLaSolicitud.clear();
        vista.getSelectorFechaProgramada().setValue(null);
        vista.getCampoObservaciones().clear();
        vista.getComboPrioridad().getSelectionModel().select(Prioridad.MEDIA);
        actualizarCostoElementos();
    }

    // --------------------------- actualizar la vista -------------------------

    /** Recarga la tabla de solicitudes. */
    private void actualizarTablaSolicitudes() {
        vista.getTablaSolicitudes().setItems(FXCollections.observableArrayList(servicioSolicitudes.listar()));
    }

    /** Recarga la lista de elementos que se pueden contratar. */
    private void actualizarElementosDisponibles() {
        List<ComponenteServicio> elementos = new ArrayList<>();
        elementos.addAll(servicioCatalogo.listarDisponibles());
        elementos.addAll(servicioPersonalizacion.listarPersonalizados());
        elementos.addAll(servicioPaquetes.listar());

        vista.getComboElementos().setItems(FXCollections.observableArrayList(elementos));
        vista.getComboElementos().getSelectionModel().selectFirst();
    }

    /** Actualiza el costo estimado de los elementos agregados a la solicitud. */
    private void actualizarCostoElementos() {
        double total = elementosDeLaSolicitud.stream().mapToDouble(ComponenteServicio::calcularCosto).sum();
        boolean presencial = elementosDeLaSolicitud.stream().anyMatch(ComponenteServicio::requiereAtencionPresencial);
        vista.getLabelCostoElementos().setText("Costo de los elementos: " + Formato.moneda(total)
                + "   |   Requiere atencion presencial: " + (presencial ? "Si" : "No"));
    }

    /** Muestra en el area de detalle toda la informacion de la solicitud. */
    private void mostrarDetalle(Solicitud solicitud) {
        if (solicitud == null) {
            vista.getDetalleSolicitud().clear();
            return;
        }
        StringBuilder texto = new StringBuilder();
        texto.append("Codigo: ").append(solicitud.getCodigo()).append('\n');
        texto.append("Cliente: ").append(solicitud.getCliente().getNombreCompleto())
                .append(" (").append(solicitud.getCliente().getDocumento()).append(")\n");
        texto.append("Correo: ").append(solicitud.getCliente().getCorreo()).append('\n');
        texto.append("Fecha de creacion: ").append(Formato.fecha(solicitud.getFechaCreacion())).append('\n');
        texto.append("Fecha programada: ").append(Formato.fecha(solicitud.getFechaProgramada())).append('\n');
        texto.append("Prioridad: ").append(solicitud.getPrioridad().getNombreVisible()).append('\n');
        texto.append("Observaciones: ")
                .append(solicitud.getObservaciones().isBlank() ? "(ninguna)" : solicitud.getObservaciones())
                .append('\n');
        texto.append("Estado: ").append(solicitud.getEstado().getNombreVisible()).append('\n');
        texto.append("Elementos contratados:\n");
        for (ComponenteServicio elemento : solicitud.getElementos()) {
            texto.append("   - ").append(elemento.getNombre())
                    .append(": ").append(Formato.moneda(elemento.calcularCosto())).append('\n');
        }
        texto.append("Costo de los elementos: ").append(Formato.moneda(solicitud.calcularCostoElementos())).append('\n');
        texto.append("Costo de desplazamiento: ").append(Formato.moneda(solicitud.getCostoDesplazamiento()))
                .append(" (se cobra una sola vez)\n");
        texto.append("COSTO TOTAL: ").append(Formato.moneda(solicitud.calcularCostoTotal())).append('\n');

        texto.append("Requiere atencion presencial: ")
                .append(solicitud.requiereAtencionPresencial() ? "Si" : "No").append('\n');
        if (!solicitud.getEspecialidadesRequeridas().isEmpty()) {
            texto.append("Especialidades requeridas: ");
            List<String> nombres = new ArrayList<>();
            for (Especialidad especialidad : solicitud.getEspecialidadesRequeridas()) {
                nombres.add(especialidad.getNombreVisible());
            }
            texto.append(String.join(", ", nombres)).append('\n');
        }
        if (solicitud.getTecnicoAsignado() != null) {
            texto.append("Tecnico asignado: ").append(solicitud.getTecnicoAsignado().getIdentificador())
                    .append(" (").append(solicitud.getTecnicoAsignado().getEspecialidad().getNombreVisible())
                    .append(")\n");
        }

        vista.getDetalleSolicitud().setText(texto.toString());
    }

    /** Muestra un error de validacion al usuario. */
    private void mostrarError(String mensaje) {
        Alertas.error(ventana(), "No se pudo completar la operacion", mensaje);
    }

    /** @return la ventana actual (puede ser null si la vista aun no se muestra) */
    private Window ventana() {
        return vista.getScene() == null ? null : vista.getScene().getWindow();
    }
}
