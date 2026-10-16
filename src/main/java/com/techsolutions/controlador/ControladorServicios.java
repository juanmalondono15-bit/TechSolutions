package com.techsolutions.controlador;

import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.fabricas.FabricaComplemento;
import com.techsolutions.fabricas.FabricaServicio;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.ServicioTecnico;
import com.techsolutions.modelo.complemento.ComplementoServicio;
import com.techsolutions.servicio.ServicioCatalogo;
import com.techsolutions.servicio.ServicioPersonalizacion;
import com.techsolutions.util.Formato;
import com.techsolutions.vista.Alertas;
import com.techsolutions.vista.VistaServicios;
import javafx.collections.FXCollections;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Controlador de la funcionalidad 1: <b>gestion y personalizacion de
 * servicios</b>.
 *
 * <p>Capa <b>Controlador</b> del MVC: recibe los eventos de la vista
 * ({@link VistaServicios}), llama a los servicios de aplicacion y devuelve el
 * resultado a la vista. No contiene reglas de negocio ni construye controles
 * de JavaFX, con lo que la interfaz y el modelo quedan desacoplados.</p>
 */
public class ControladorServicios {

    private final VistaServicios vista;
    private final ServicioCatalogo servicioCatalogo;
    private final ServicioPersonalizacion servicioPersonalizacion;

    /**
     * Servicio que se esta personalizando en este momento.
     *
     * <p>Importante: al aplicar un complemento esta variable apunta al objeto
     * <b>decorado nuevo</b>, nunca al objeto del catalogo. Asi se cumple la
     * regla de que la personalizacion no altere la definicion original.</p>
     */
    private ComponenteServicio servicioEnProceso;

    /**
     * @param vista                  vista de la pestana de servicios
     * @param servicioCatalogo       servicio de aplicacion del catalogo
     * @param servicioPersonalizacion servicio de aplicacion de complementos
     */
    public ControladorServicios(VistaServicios vista, ServicioCatalogo servicioCatalogo,
                                ServicioPersonalizacion servicioPersonalizacion) {
        this.vista = vista;
        this.servicioCatalogo = servicioCatalogo;
        this.servicioPersonalizacion = servicioPersonalizacion;
    }

    /**
     * Enlaza los eventos de la vista y carga los datos iniciales.
     */
    public void iniciar() {
        // Datos iniciales de los combos.
        vista.getComboTipos().getItems().setAll(servicioCatalogo.listarTiposDeServicio());
        vista.getComboTipos().getSelectionModel().selectFirst();

        vista.getComboComplementos().getItems()
                .setAll(servicioPersonalizacion.listarComplementosDisponibles());
        vista.getComboComplementos().getSelectionModel().selectFirst();

        // Eventos de los botones.
        vista.getBotonCrear().setOnAction(evento -> crearServicio());
        vista.getBotonAplicarComplemento().setOnAction(evento -> aplicarComplemento());
        vista.getBotonGuardarPersonalizado().setOnAction(evento -> guardarPersonalizado());
        vista.getBotonReiniciar().setOnAction(evento -> reiniciarPersonalizacion());

        // Al seleccionar un servicio del catalogo comienza una personalizacion nueva.
        vista.getTablaCatalogo().getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> seleccionarServicio(nuevo));

        actualizarTablaCatalogo();
        actualizarTablaPersonalizados();
        vista.getDetallePersonalizacion().setText(
                "Seleccione un servicio del catalogo para personalizarlo con caracteristicas complementarias.");
    }

    // ------------------------------ acciones --------------------------------

    /** Crea un servicio del tipo elegido usando la fabrica correspondiente. */
    private void crearServicio() {
        FabricaServicio fabrica = vista.getComboTipos().getValue();
        String codigo = vista.getCampoCodigo().getText();
        String nombre = vista.getCampoNombre().getText();
        String descripcion = vista.getCampoDescripcion().getText();

        double costo;
        try {
            costo = convertirCosto(vista.getCampoCosto().getText());
        } catch (NumberFormatException e) {
            mostrarError("El costo base debe ser un numero valido. Ejemplo: 120000");
            return;
        }

        try {
            ServicioTecnico nuevo = servicioCatalogo.crearServicio(fabrica, codigo, nombre, descripcion, costo);
            actualizarTablaCatalogo();
            limpiarFormularioCreacion();
            Alertas.informacion(ventana(), "Servicio creado",
                    "Se creo el servicio \"" + nuevo.getNombre() + "\" (" + nuevo.getCodigo()
                            + ") con costo base " + Formato.moneda(nuevo.getCostoBase()) + ".");
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Marca el servicio del catalogo que se va a personalizar. */
    private void seleccionarServicio(ServicioTecnico servicio) {
        if (servicio == null) {
            return;
        }
        // Se guarda la referencia al objeto del catalogo: al responder a los
        // complementos se reemplazara por objetos decorados nuevos.
        servicioEnProceso = servicio;
        vista.getLabelServicioSeleccionado().setText("Personalizando: " + servicio.getNombre());
        vista.getDetallePersonalizacion().setText(describirPersonalizacion(servicio));
    }

    /** Aplica el complemento elegido envolviendo el componente actual (Decorator). */
    private void aplicarComplemento() {
        if (servicioEnProceso == null) {
            mostrarError("Seleccione primero un servicio del catalogo.");
            return;
        }
        FabricaComplemento fabrica = vista.getComboComplementos().getValue();
        try {
            servicioEnProceso = servicioPersonalizacion.aplicarComplemento(servicioEnProceso, fabrica);
            vista.getDetallePersonalizacion().setText(describirPersonalizacion(servicioEnProceso));
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Guarda el servicio personalizado para poder usarlo en paquetes y solicitudes. */
    private void guardarPersonalizado() {
        if (!(servicioEnProceso instanceof ComplementoServicio)) {
            mostrarError("Aplique al menos una caracteristica complementaria antes de guardar.");
            return;
        }
        try {
            servicioPersonalizacion.guardarPersonalizado(servicioEnProceso);
            actualizarTablaPersonalizados();
            Alertas.informacion(ventana(), "Servicio personalizado guardado",
                    "El servicio personalizado ya puede incluirse en paquetes y solicitudes.");
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Quita los complementos aplicados: vuelve a mostrar el servicio original. */
    private void reiniciarPersonalizacion() {
        ServicioTecnico seleccionado = vista.getTablaCatalogo().getSelectionModel().getSelectedItem();
        servicioEnProceso = seleccionado;
        if (seleccionado == null) {
            vista.getDetallePersonalizacion().setText(
                    "Seleccione un servicio del catalogo para personalizarlo con caracteristicas complementarias.");
        } else {
            vista.getDetallePersonalizacion().setText(describirPersonalizacion(seleccionado));
        }
    }

    // --------------------------- actualizar la vista -------------------------

    /** Recarga la tabla del catalogo. */
    private void actualizarTablaCatalogo() {
        vista.getTablaCatalogo().setItems(
                FXCollections.observableArrayList(servicioCatalogo.listarDisponibles()));
    }

    /** Recarga la tabla de servicios personalizados guardados. */
    private void actualizarTablaPersonalizados() {
        vista.getTablaPersonalizados().setItems(
                FXCollections.observableArrayList(servicioPersonalizacion.listarPersonalizados()));
    }

    /** Limpia el formulario de creacion de servicios. */
    private void limpiarFormularioCreacion() {
        vista.getCampoCodigo().clear();
        vista.getCampoNombre().clear();
        vista.getCampoDescripcion().clear();
        vista.getCampoCosto().clear();
    }

    // ------------------------------- utilidades ------------------------------

    /**
     * Construye el texto de detalle que pide el enunciado: costo base,
     * incrementos de cada complemento y costo final.
     *
     * @param componente servicio original o servicio personalizado
     * @return texto explicativo listo para mostrar
     */
    private String describirPersonalizacion(ComponenteServicio componente) {
        // Recorre la cadena de decoradores desde el exterior hacia el interior.
        List<String> complementos = new ArrayList<>();
        ComponenteServicio actual = componente;
        while (actual instanceof ComplementoServicio decorador) {
            complementos.add(decorador.getNombreComplemento() + " -> "
                    + Formato.moneda(decorador.getIncremento()));
            actual = decorador.getServicioContratado();
        }
        // Se muestran en el orden en que se aplicaron (de adentro hacia afuera).
        Collections.reverse(complementos);

        StringBuilder texto = new StringBuilder();
        texto.append("Servicio contratado: ").append(componente.getNombre()).append('\n');
        texto.append("Costo base del servicio: ").append(Formato.moneda(actual.calcularCosto())).append('\n');
        if (complementos.isEmpty()) {
            texto.append("Complementos: ninguno\n");
        } else {
            texto.append("Complementos incorporados:\n");
            for (String complemento : complementos) {
                texto.append("   + ").append(complemento).append('\n');
            }
        }
        texto.append('\n');
        texto.append("COSTO FINAL: ").append(Formato.moneda(componente.calcularCosto())).append('\n');
        texto.append("Requiere atencion presencial: ")
                .append(componente.requiereAtencionPresencial() ? "Si" : "No").append('\n');

        // Demostracion de que el catalogo no se modifica con la personalizacion.
        if (!complementos.isEmpty()) {
            servicioCatalogo.listarDisponibles().stream()
                    .filter(servicio -> servicio.getCodigo().equals(componente.getCodigo()))
                    .findFirst()
                    .ifPresent(original -> texto.append("\nNota: el servicio original del catalogo conserva su "
                            + "costo base de ").append(Formato.moneda(original.getCostoBase())).append('.'));
        }
        return texto.toString();
    }

    /** Convierte el texto de un campo a numero aceptando separadores de miles. */
    private double convertirCosto(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new NumberFormatException("costo vacio");
        }
        String limpio = texto.trim().replace(".", "").replace(" ", "").replace(",", ".");
        return Double.parseDouble(limpio);
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
