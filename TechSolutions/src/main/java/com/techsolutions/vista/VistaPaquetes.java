package com.techsolutions.vista;

import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.PaqueteTecnologico;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Vista de la funcionalidad 2: <b>configuracion de paquetes tecnologicos</b>.
 *
 * <p>Capa <b>Vista</b> del MVC: solo construye controles y los expone. La
 * logica de agregar componentes, detectar ciclos y calcular costos vive en el
 * modelo ({@link PaqueteTecnologico}) y en el servicio de aplicacion
 * ({@code ServicioPaquetes}).</p>
 *
 * <p>Contenido de la pantalla:</p>
 * <ul>
 *   <li><b>Izquierda:</b> formulario para crear paquetes y tabla de paquetes
 *       configurados.</li>
 *   <li><b>Derecha:</b> componentes disponibles (servicios del catalogo,
 *       servicios personalizados y paquetes), boton para agregar al paquete
 *       seleccionado, arbol de composicion en texto y costo total.</li>
 * </ul>
 */
public class VistaPaquetes extends SplitPane {

    // Crear paquete
    private final TextField campoCodigo = new TextField();
    private final TextField campoNombre = new TextField();
    private final TextField campoDescripcion = new TextField();
    private final Button botonCrear = new Button("Crear paquete");

    // Paquetes creados
    private final TableView<PaqueteTecnologico> tablaPaquetes = new TableView<>();

    // Composicion del paquete seleccionado
    private final ComboBox<ComponenteServicio> comboElementos = new ComboBox<>();
    private final Button botonAgregar = new Button("Agregar al paquete");
    private final TextArea areaComposicion = new TextArea();
    private final Label labelCostoTotal = new Label("Costo total: -");

    /**
     * Construye la vista con las dos columnas de la funcionalidad.
     */
    public VistaPaquetes() {
        setPadding(new Insets(10));
        setDividerPositions(0.5);

        VBox columnaIzquierda = new VBox(10, crearPanelCreacion(), crearPanelPaquetes());
        VBox columnaDerecha = new VBox(10, crearPanelComposicion());

        getItems().addAll(columnaIzquierda, columnaDerecha);
    }

    // ------------------------- construccion de paneles ----------------------

    /** Formulario para crear un paquete tecnologico vacio. */
    private TitledPane crearPanelCreacion() {
        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(8);
        formulario.setPadding(new Insets(10));

        ColumnConstraints etiquetas = new ColumnConstraints();
        etiquetas.setMinWidth(90);
        ColumnConstraints campos = new ColumnConstraints();
        campos.setHgrow(Priority.ALWAYS);
        formulario.getColumnConstraints().addAll(etiquetas, campos);

        campoCodigo.setPromptText("Ej: PAQ-003");
        campoNombre.setPromptText("Ej: Paquete empresarial");
        campoDescripcion.setPromptText("Descripcion del paquete");

        formulario.addRow(0, new Label("Codigo:"), campoCodigo);
        formulario.addRow(1, new Label("Nombre:"), campoNombre);
        formulario.addRow(2, new Label("Descripcion:"), campoDescripcion);
        formulario.add(botonCrear, 1, 3);

        TitledPane panel = new TitledPane("Crear paquete tecnologico", formulario);
        panel.setCollapsible(false);
        return panel;
    }

    /** Tabla con los paquetes configurados. */
    private TitledPane crearPanelPaquetes() {
        TableColumn<PaqueteTecnologico, String> colCodigo = new TableColumn<>("Codigo");
        colCodigo.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getCodigo()));
        colCodigo.setPrefWidth(90);

        TableColumn<PaqueteTecnologico, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(d -> new ReadOnlyStringWrapper(d.getValue().getNombre()));
        colNombre.setPrefWidth(200);

        TableColumn<PaqueteTecnologico, String> colComponentes = new TableColumn<>("Componentes");
        colComponentes.setCellValueFactory(d -> new ReadOnlyStringWrapper(
                String.valueOf(d.getValue().getComponentes().size())));
        colComponentes.setPrefWidth(100);

        TableColumn<PaqueteTecnologico, String> colCosto = new TableColumn<>("Costo total");
        colCosto.setCellValueFactory(d -> new ReadOnlyStringWrapper(Formato.moneda(d.getValue().calcularCosto())));
        colCosto.setPrefWidth(120);

        tablaPaquetes.getColumns().addAll(colCodigo, colNombre, colComponentes, colCosto);
        tablaPaquetes.setPlaceholder(new Label("Todavia no hay paquetes configurados"));
        VBox.setVgrow(tablaPaquetes, Priority.ALWAYS);

        TitledPane panel = new TitledPane("Paquetes configurados", tablaPaquetes);
        panel.setCollapsible(false);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    /** Panel para agregar componentes y ver la composicion del paquete. */
    private TitledPane crearPanelComposicion() {
        comboElementos.setMaxWidth(Double.MAX_VALUE);
        comboElementos.setPromptText("Servicio, servicio personalizado o paquete");

        areaComposicion.setEditable(false);
        areaComposicion.setWrapText(false);
        areaComposicion.setPromptText("Seleccione un paquete para ver su composicion");
        VBox.setVgrow(areaComposicion, Priority.ALWAYS);

        Label ayuda = new Label("Agregue servicios, servicios personalizados o paquetes. "
                + "El sistema impide que un paquete se contenga a si mismo.");
        ayuda.setWrapText(true);

        VBox contenido = new VBox(8, comboElementos, botonAgregar, ayuda, labelCostoTotal, areaComposicion);
        contenido.setPadding(new Insets(10));

        TitledPane panel = new TitledPane("Componentes del paquete seleccionado", contenido);
        panel.setCollapsible(false);
        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    // ------------------------------- getters --------------------------------

    public TextField getCampoCodigo() {
        return campoCodigo;
    }

    public TextField getCampoNombre() {
        return campoNombre;
    }

    public TextField getCampoDescripcion() {
        return campoDescripcion;
    }

    public Button getBotonCrear() {
        return botonCrear;
    }

    public TableView<PaqueteTecnologico> getTablaPaquetes() {
        return tablaPaquetes;
    }

    public ComboBox<ComponenteServicio> getComboElementos() {
        return comboElementos;
    }

    public Button getBotonAgregar() {
        return botonAgregar;
    }

    public TextArea getAreaComposicion() {
        return areaComposicion;
    }

    public Label getLabelCostoTotal() {
        return labelCostoTotal;
    }
}
