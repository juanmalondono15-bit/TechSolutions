package com.techsolutions.vista;

import com.techsolutions.modelo.Cliente;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.Prioridad;
import com.techsolutions.modelo.Solicitud;
import com.techsolutions.util.Formato;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Vista de las funcionalidades 3 y 4: <b>registro de solicitudes</b> y
 * <b>consulta de tecnicos / calculo del costo</b>.
 *
 * <p>Capa <b>Vista</b> del MVC. Incluye los campos obligatorios (cliente y
 * elementos contratados) y los opcionales (fecha programada, observaciones y
 * prioridad) exigidos por el enunciado.</p>
 */
public class VistaSolicitudes extends SplitPane {

    // ------------------------- datos de la solicitud ------------------------

    private final ComboBox<Cliente> comboClientes = new ComboBox<>();
    private final ComboBox<ComponenteServicio> comboElementos = new ComboBox<>();
    private final Button botonAgregarElemento = new Button("Agregar elemento");
    private final ListView<ComponenteServicio> listaElementos = new ListView<>();
    private final Button botonQuitarElemento = new Button("Quitar elemento");
    private final DatePicker selectorFechaProgramada = new DatePicker();
    private final TextArea campoObservaciones = new TextArea();
    private final ComboBox<Prioridad> comboPrioridad = new ComboBox<>();
    private final Button botonRegistrar = new Button("Registrar solicitud");
    private final Button botonLimpiar = new Button("Limpiar formulario");
    private final Label labelCostoElementos = new Label("Costo de los elementos: $ 0");

    // --------------------------- lista de solicitudes -----------------------

    private final TableView<Solicitud> tablaSolicitudes = new TableView<>();
    private final Button botonConfirmar = new Button("Confirmar solicitud");
    private final Button botonConsultarTecnicos = new Button("Consultar tecnicos");
    private final TextArea detalleSolicitud = new TextArea();

    /**
     * Construye la vista con el formulario (izquierda) y el listado de
     * solicitudes con sus acciones (derecha).
     */
    public VistaSolicitudes() {
        setPadding(new Insets(10));
        setDividerPositions(0.45);

        VBox columnaIzquierda = new VBox(10, crearPanelRegistro());
        VBox columnaDerecha = new VBox(10, crearPanelSolicitudes());
        getItems().addAll(columnaIzquierda, columnaDerecha);
    }

    // ------------------------- construccion de paneles ----------------------

    /** Formulario de registro de una solicitud. */
    private TitledPane crearPanelRegistro() {
        comboClientes.setMaxWidth(Double.MAX_VALUE);
        comboElementos.setMaxWidth(Double.MAX_VALUE);

        listaElementos.setPrefHeight(120);
        listaElementos.setPlaceholder(new Label("Agregue los servicios o paquetes contratados"));

        campoObservaciones.setPromptText("Observaciones tecnicas (opcional)");
        campoObservaciones.setPrefRowCount(2);
        campoObservaciones.setWrapText(true);

        selectorFechaProgramada.setPromptText("Fecha programada (opcional)");
        selectorFechaProgramada.setMaxWidth(Double.MAX_VALUE);

        VBox contenido = new VBox(8,
                new Label("Cliente:"), comboClientes,
                new Label("Elementos contratados:"), comboElementos, botonAgregarElemento, listaElementos,
                botonQuitarElemento,
                new Label("Fecha programada de atencion:"), selectorFechaProgramada,
                new Label("Observaciones tecnicas:"), campoObservaciones,
                new Label("Prioridad:"), comboPrioridad,
                labelCostoElementos,
                botonRegistrar, botonLimpiar);
        contenido.setPadding(new Insets(10));

        TitledPane panel = new TitledPane("Registrar solicitud de servicio", contenido);
        panel.setCollapsible(false);
        return panel;
    }

    /** Tabla de solicitudes registradas y acciones sobre la solicitud elegida. */
    private TitledPane crearPanelSolicitudes() {
        TableColumn<Solicitud, String> colCodigo = new TableColumn<>("Codigo");
        colCodigo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCodigo()));
        colCodigo.setPrefWidth(90);

        TableColumn<Solicitud, String> colCliente = new TableColumn<>("Cliente");
        colCliente.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCliente().getNombreCompleto()));
        colCliente.setPrefWidth(180);

        TableColumn<Solicitud, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(d -> new ReadOnlyStringWrapper(Formato.fecha(d.getValue().getFechaCreacion())));
        colFecha.setPrefWidth(90);

        TableColumn<Solicitud, String> colPrioridad = new TableColumn<>("Prioridad");
        colPrioridad.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getPrioridad().getNombreVisible()));
        colPrioridad.setPrefWidth(80);

        TableColumn<Solicitud, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getEstado().getNombreVisible()));
        colEstado.setPrefWidth(110);

        TableColumn<Solicitud, String> colTotal = new TableColumn<>("Costo total");
        colTotal.setCellValueFactory(d -> new ReadOnlyStringWrapper(Formato.moneda(d.getValue().calcularCostoTotal())));
        colTotal.setPrefWidth(120);

        tablaSolicitudes.getColumns().addAll(colCodigo, colCliente, colFecha, colPrioridad, colEstado, colTotal);
        tablaSolicitudes.setPlaceholder(new Label("Todavia no hay solicitudes registradas"));
        VBox.setVgrow(tablaSolicitudes, Priority.ALWAYS);

        detalleSolicitud.setEditable(false);
        detalleSolicitud.setWrapText(true);
        detalleSolicitud.setPrefRowCount(11);
        detalleSolicitud.setPromptText("Seleccione una solicitud para ver su detalle");

        VBox contenido = new VBox(8,
                tablaSolicitudes,
                new javafx.scene.layout.HBox(8, botonConfirmar, botonConsultarTecnicos),
                new Label("Detalle de la solicitud:"),
                detalleSolicitud);
        contenido.setPadding(new Insets(10));
        VBox.setVgrow(tablaSolicitudes, Priority.ALWAYS);

        TitledPane panel = new TitledPane("Solicitudes registradas", contenido);
        panel.setCollapsible(false);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    // ------------------------------- getters --------------------------------

    public ComboBox<Cliente> getComboClientes() {
        return comboClientes;
    }

    public ComboBox<ComponenteServicio> getComboElementos() {
        return comboElementos;
    }

    public Button getBotonAgregarElemento() {
        return botonAgregarElemento;
    }

    public ListView<ComponenteServicio> getListaElementos() {
        return listaElementos;
    }

    public Button getBotonQuitarElemento() {
        return botonQuitarElemento;
    }

    public DatePicker getSelectorFechaProgramada() {
        return selectorFechaProgramada;
    }

    public TextArea getCampoObservaciones() {
        return campoObservaciones;
    }

    public ComboBox<Prioridad> getComboPrioridad() {
        return comboPrioridad;
    }

    public Button getBotonRegistrar() {
        return botonRegistrar;
    }

    public Button getBotonLimpiar() {
        return botonLimpiar;
    }

    public Label getLabelCostoElementos() {
        return labelCostoElementos;
    }

    public TableView<Solicitud> getTablaSolicitudes() {
        return tablaSolicitudes;
    }

    public Button getBotonConfirmar() {
        return botonConfirmar;
    }

    public Button getBotonConsultarTecnicos() {
        return botonConsultarTecnicos;
    }

    public TextArea getDetalleSolicitud() {
        return detalleSolicitud;
    }
}
