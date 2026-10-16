package com.techsolutions.vista;

import com.techsolutions.fabricas.FabricaComplemento;
import com.techsolutions.fabricas.FabricaServicio;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.ServicioTecnico;
import com.techsolutions.util.Formato;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Vista de la funcionalidad 1: <b>gestion y personalizacion de servicios
 * tecnologicos</b>.
 *
 * <p>Esta clase pertenece a la capa <b>Vista</b> del MVC. Su unica
 * responsabilidad es construir los controles de JavaFX y exponerlos con
 * metodos {@code get}; no contiene reglas de negocio ni decide que hacer cuando
 * el usuario hace clic (eso es tarea de
 * {@code com.techsolutions.controlador.ControladorServicios}).</p>
 *
 * <p>Contenido de la pantalla:</p>
 * <ul>
 *   <li><b>Izquierda:</b> formulario para crear servicios nuevos y tabla con el
 *       catalogo de servicios disponibles.</li>
 *   <li><b>Derecha:</b> personalizacion del servicio seleccionado (aplicar
 *       caracteristicas complementarias y ver costo base, incrementos y costo
 *       final) y tabla de servicios personalizados que se hayan guardado.</li>
 * </ul>
 */
public class VistaServicios extends SplitPane {

    // ------------------------------------------------------------------
    //  Controles para crear servicios
    // ------------------------------------------------------------------

    private final ComboBox<FabricaServicio> comboTipos = new ComboBox<>();
    private final TextField campoCodigo = new TextField();
    private final TextField campoNombre = new TextField();
    private final TextField campoDescripcion = new TextField();
    private final TextField campoCosto = new TextField();
    private final Button botonCrear = new Button("Crear servicio");

    // ------------------------------------------------------------------
    //  Tabla del catalogo
    // ------------------------------------------------------------------

    private final TableView<ServicioTecnico> tablaCatalogo = new TableView<>();

    // ------------------------------------------------------------------
    //  Controles de personalizacion
    // ------------------------------------------------------------------

    private final ComboBox<FabricaComplemento> comboComplementos = new ComboBox<>();
    private final Button botonAplicarComplemento = new Button("Aplicar complemento");
    private final Button botonGuardarPersonalizado = new Button("Guardar servicio personalizado");
    private final Button botonReiniciar = new Button("Quitar complementos");
    private final TextArea detallePersonalizacion = new TextArea();
    private final Label labelServicioSeleccionado = new Label("Ningun servicio seleccionado");

    // ------------------------------------------------------------------
    //  Tabla de servicios personalizados
    // ------------------------------------------------------------------

    private final TableView<ComponenteServicio> tablaPersonalizados = new TableView<>();

    /**
     * Construye la vista completa con sus tres secciones.
     */
    public VistaServicios() {
        setPadding(new Insets(10));
        setDividerPositions(0.55);

        VBox columnaIzquierda = new VBox(10, crearPanelCreacion(), crearPanelCatalogo());
        VBox columnaDerecha = new VBox(10, crearPanelPersonalizacion(), crearPanelPersonalizados());

        getItems().addAll(columnaIzquierda, columnaDerecha);
    }

    // ------------------------- construccion de paneles ----------------------

    /** Formulario para crear servicios de los tipos definidos por TechSolutions. */
    private TitledPane crearPanelCreacion() {
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(8);
        formulario.setPadding(new Insets(10));

        ColumnConstraints columnaEtiquetas = new ColumnConstraints();
        columnaEtiquetas.setMinWidth(90);
        ColumnConstraints columnaCampos = new ColumnConstraints();
        columnaCampos.setHgrow(Priority.ALWAYS);
        formulario.getColumnConstraints().addAll(columnaEtiquetas, columnaCampos);

        campoCodigo.setPromptText("Ej: SRV-005");
        campoNombre.setPromptText("Ej: Mantenimiento de impresoras");
        campoDescripcion.setPromptText("Descripcion del servicio");
        campoCosto.setPromptText("Ej: 90000");

        formulario.addRow(0, new Label("Tipo:"), comboTipos);
        formulario.addRow(1, new Label("Codigo:"), campoCodigo);
        formulario.addRow(2, new Label("Nombre:"), campoNombre);
        formulario.addRow(3, new Label("Descripcion:"), campoDescripcion);
        formulario.addRow(4, new Label("Costo base:"), campoCosto);
        formulario.add(botonCrear, 1, 5);

        TitledPane panel = new TitledPane("Crear servicio tecnologico", formulario);
        panel.setCollapsible(false);
        return panel;
    }

    /** Tabla con el catalogo de servicios disponibles. */
    private TitledPane crearPanelCatalogo() {
        TableColumn<ServicioTecnico, String> colCodigo = new TableColumn<>("Codigo");
        colCodigo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCodigo()));
        colCodigo.setPrefWidth(90);

        TableColumn<ServicioTecnico, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNombre()));
        colNombre.setPrefWidth(200);

        TableColumn<ServicioTecnico, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getTipo()));
        colTipo.setPrefWidth(160);

        TableColumn<ServicioTecnico, String> colCosto = new TableColumn<>("Costo base");
        colCosto.setCellValueFactory(d -> new ReadOnlyStringWrapper(Formato.moneda(d.getValue().getCostoBase())));
        colCosto.setPrefWidth(110);

        TableColumn<ServicioTecnico, String> colPresencial = new TableColumn<>("Presencial");
        colPresencial.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().requiereAtencionPresencial() ? "Si" : "No"));
        colPresencial.setPrefWidth(90);

        tablaCatalogo.getColumns().addAll(colCodigo, colNombre, colTipo, colCosto, colPresencial);
        tablaCatalogo.setPlaceholder(new Label("No hay servicios en el catalogo"));
        VBox.setVgrow(tablaCatalogo, Priority.ALWAYS);

        TitledPane panel = new TitledPane("Catalogo de servicios disponibles", tablaCatalogo);
        panel.setCollapsible(false);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    /** Panel para incorporar caracteristicas complementarias al servicio elegido. */
    private TitledPane crearPanelPersonalizacion() {
        comboComplementos.setMaxWidth(Double.MAX_VALUE);

        HBox botones = new HBox(8, botonAplicarComplemento, botonGuardarPersonalizado, botonReiniciar);

        detallePersonalizacion.setEditable(false);
        detallePersonalizacion.setPrefRowCount(9);
        detallePersonalizacion.setWrapText(true);

        VBox contenido = new VBox(8, labelServicioSeleccionado, comboComplementos, botones, detallePersonalizacion);
        contenido.setPadding(new Insets(10));

        TitledPane panel = new TitledPane("Personalizar servicio (caracteristicas complementarias)", contenido);
        panel.setCollapsible(false);
        return panel;
    }

    /** Tabla con los servicios personalizados guardados (base para paquetes y solicitudes). */
    private TitledPane crearPanelPersonalizados() {
        TableColumn<ComponenteServicio, String> colNombre = new TableColumn<>("Servicio personalizado");
        colNombre.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNombre()));
        colNombre.setPrefWidth(320);

        TableColumn<ComponenteServicio, String> colCosto = new TableColumn<>("Costo final");
        colCosto.setCellValueFactory(d -> new ReadOnlyStringWrapper(Formato.moneda(d.getValue().calcularCosto())));
        colCosto.setPrefWidth(110);

        TableColumn<ComponenteServicio, String> colPresencial = new TableColumn<>("Presencial");
        colPresencial.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                d.getValue().requiereAtencionPresencial() ? "Si" : "No"));
        colPresencial.setPrefWidth(90);

        tablaPersonalizados.getColumns().addAll(colNombre, colCosto, colPresencial);
        tablaPersonalizados.setPlaceholder(new Label("Todavia no hay servicios personalizados guardados"));
        VBox.setVgrow(tablaPersonalizados, Priority.ALWAYS);

        TitledPane panel = new TitledPane("Servicios personalizados guardados", tablaPersonalizados);
        panel.setCollapsible(false);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    // ------------------------------- getters --------------------------------

    public ComboBox<FabricaServicio> getComboTipos() {
        return comboTipos;
    }

    public TextField getCampoCodigo() {
        return campoCodigo;
    }

    public TextField getCampoNombre() {
        return campoNombre;
    }

    public TextField getCampoDescripcion() {
        return campoDescripcion;
    }

    public TextField getCampoCosto() {
        return campoCosto;
    }

    public Button getBotonCrear() {
        return botonCrear;
    }

    public TableView<ServicioTecnico> getTablaCatalogo() {
        return tablaCatalogo;
    }

    public ComboBox<FabricaComplemento> getComboComplementos() {
        return comboComplementos;
    }

    public Button getBotonAplicarComplemento() {
        return botonAplicarComplemento;
    }

    public Button getBotonGuardarPersonalizado() {
        return botonGuardarPersonalizado;
    }

    public Button getBotonReiniciar() {
        return botonReiniciar;
    }

    public TextArea getDetallePersonalizacion() {
        return detallePersonalizacion;
    }

    public Label getLabelServicioSeleccionado() {
        return labelServicioSeleccionado;
    }

    public TableView<ComponenteServicio> getTablaPersonalizados() {
        return tablaPersonalizados;
    }
}
