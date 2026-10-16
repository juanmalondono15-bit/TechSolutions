package com.techsolutions.vista;

import com.techsolutions.modelo.Especialidad;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/**
 * Vista de la funcionalidad 4 (parte de integracion): <b>consulta de
 * disponibilidad de tecnicos especializados</b> en la plataforma externa
 * {@code LegacySupport}.
 *
 * <p>La pantalla muestra dos cosas a proposito, para que se vea el trabajo del
 * adaptador:</p>
 * <ol>
 *   <li>La <b>respuesta cruda</b> del sistema externo, con su formato de texto
 *       separado por barras (por ejemplo {@code TEC-105|DISPONIBLE|30000}).</li>
 *   <li>La <b>informacion estructurada</b> que recibe el sistema nuevo
 *       (identificador del tecnico, especialidad, disponibilidad y costo de
 *       desplazamiento).</li>
 * </ol>
 */
public class VistaTecnicos extends VBox {

    private final ComboBox<Especialidad> comboEspecialidades = new ComboBox<>();
    private final Button botonConsultar = new Button("Consultar disponibilidad");
    private final TextArea areaRespuestaCruda = new TextArea();
    private final TextArea areaInformacion = new TextArea();

    /**
     * Construye la vista de consulta de tecnicos.
     */
    public VistaTecnicos() {
        setSpacing(10);
        setPadding(new Insets(10));

        Label explicacion = new Label(
                "El sistema consulta por nombre de especialidad. El adaptador (patron Adapter) traduce esa "
                        + "consulta al codigo numerico que entiende LegacySupport y convierte su respuesta de texto "
                        + "en informacion estructurada.");
        explicacion.setWrapText(true);

        comboEspecialidades.setMaxWidth(Double.MAX_VALUE);

        areaRespuestaCruda.setEditable(false);
        areaRespuestaCruda.setPrefRowCount(4);
        areaRespuestaCruda.setPromptText("Respuesta cruda del proveedor externo");

        areaInformacion.setEditable(false);
        areaInformacion.setPrefRowCount(8);
        areaInformacion.setPromptText("Informacion estructurada para el sistema nuevo");
        VBox.setVgrow(areaInformacion, Priority.ALWAYS);

        getChildren().addAll(
                new TitledPane("Consulta de tecnicos especializados (LegacySupport)", new VBox(8,
                        new Label("Especialidad:"), comboEspecialidades, botonConsultar)),
                new TitledPane("Respuesta cruda de LegacySupport", areaRespuestaCruda),
                new TitledPane("Informacion estructurada recibida por el sistema", areaInformacion),
                explicacion);
    }

    public ComboBox<Especialidad> getComboEspecialidades() {
        return comboEspecialidades;
    }

    public Button getBotonConsultar() {
        return botonConsultar;
    }

    public TextArea getAreaRespuestaCruda() {
        return areaRespuestaCruda;
    }

    public TextArea getAreaInformacion() {
        return areaInformacion;
    }
}
