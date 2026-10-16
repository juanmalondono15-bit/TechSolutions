package com.techsolutions.controlador;

import com.techsolutions.excepciones.PaqueteCiclicoException;
import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.modelo.ComponenteServicio;
import com.techsolutions.modelo.PaqueteTecnologico;
import com.techsolutions.servicio.ServicioCatalogo;
import com.techsolutions.servicio.ServicioPaquetes;
import com.techsolutions.servicio.ServicioPersonalizacion;
import com.techsolutions.util.Formato;
import com.techsolutions.vista.Alertas;
import com.techsolutions.vista.VistaPaquetes;
import javafx.collections.FXCollections;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la funcionalidad 2: <b>configuracion de paquetes
 * tecnologicos</b>.
 *
 * <p>Capa <b>Controlador</b> del MVC. Traduce los clics del usuario en llamadas
 * a {@link ServicioPaquetes} y refresca la vista. La validacion del ciclo y el
 * calculo de costos los hace el modelo, por lo que este controlador es corto y
 * sin logica de negocio.</p>
 */
public class ControladorPaquetes {

    private final VistaPaquetes vista;
    private final ServicioCatalogo servicioCatalogo;
    private final ServicioPersonalizacion servicioPersonalizacion;
    private final ServicioPaquetes servicioPaquetes;

    /**
     * @param vista                   vista de la pestana de paquetes
     * @param servicioCatalogo        servicios del catalogo
     * @param servicioPersonalizacion servicios personalizados guardados
     * @param servicioPaquetes        servicio de aplicacion de paquetes
     */
    public ControladorPaquetes(VistaPaquetes vista, ServicioCatalogo servicioCatalogo,
                               ServicioPersonalizacion servicioPersonalizacion,
                               ServicioPaquetes servicioPaquetes) {
        this.vista = vista;
        this.servicioCatalogo = servicioCatalogo;
        this.servicioPersonalizacion = servicioPersonalizacion;
        this.servicioPaquetes = servicioPaquetes;
    }

    /**
     * Enlaza los eventos y carga los datos iniciales.
     */
    public void iniciar() {
        vista.getBotonCrear().setOnAction(evento -> crearPaquete());
        vista.getBotonAgregar().setOnAction(evento -> agregarComponente());
        vista.getTablaPaquetes().getSelectionModel().selectedItemProperty()
                .addListener((observable, anterior, nuevo) -> mostrarComposicion(nuevo));

        actualizarTablaPaquetes();
        actualizarElementosDisponibles();
    }

    // ------------------------------ acciones --------------------------------

    /** Crea un paquete tecnologico vacio. */
    private void crearPaquete() {
        try {
            PaqueteTecnologico paquete = servicioPaquetes.crear(
                    vista.getCampoCodigo().getText(),
                    vista.getCampoNombre().getText(),
                    vista.getCampoDescripcion().getText());

            actualizarTablaPaquetes();
            actualizarElementosDisponibles();
            vista.getTablaPaquetes().getSelectionModel().select(paquete);
            vista.getCampoCodigo().clear();
            vista.getCampoNombre().clear();
            vista.getCampoDescripcion().clear();

            Alertas.informacion(ventana(), "Paquete creado",
                    "Se creo el paquete \"" + paquete.getNombre() + "\". "
                            + "Ahora agregue sus componentes.");
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        }
    }

    /** Agrega el componente seleccionado al paquete seleccionado. */
    private void agregarComponente() {
        PaqueteTecnologico paquete = vista.getTablaPaquetes().getSelectionModel().getSelectedItem();
        ComponenteServicio componente = vista.getComboElementos().getValue();
        try {
            servicioPaquetes.agregarComponente(paquete, componente);
            actualizarTablaPaquetes();
            mostrarComposicion(paquete);
            vista.getTablaPaquetes().getSelectionModel().select(paquete);
        } catch (PaqueteCiclicoException e) {
            // Regla del enunciado: impedir que un paquete se contenga a si mismo.
            Alertas.advertencia(ventana(), "Operacion no permitida", e.getMessage());
        } catch (ValidacionException e) {
            mostrarError(e.getMessage());
        }
    }

    // --------------------------- actualizar la vista -------------------------

    /** Recarga la tabla de paquetes. */
    private void actualizarTablaPaquetes() {
        vista.getTablaPaquetes().setItems(FXCollections.observableArrayList(servicioPaquetes.listar()));
    }

    /**
     * Recarga la lista de componentes que se pueden agregar a un paquete:
     * servicios del catalogo, servicios personalizados y paquetes ya creados.
     */
    private void actualizarElementosDisponibles() {
        List<ComponenteServicio> elementos = new ArrayList<>();
        elementos.addAll(servicioCatalogo.listarDisponibles());
        elementos.addAll(servicioPersonalizacion.listarPersonalizados());
        elementos.addAll(servicioPaquetes.listar());

        vista.getComboElementos().setItems(FXCollections.observableArrayList(elementos));
        vista.getComboElementos().getSelectionModel().selectFirst();
    }

    /** Muestra la composicion y el costo total del paquete seleccionado. */
    private void mostrarComposicion(PaqueteTecnologico paquete) {
        if (paquete == null) {
            vista.getAreaComposicion().clear();
            vista.getLabelCostoTotal().setText("Costo total: -");
            return;
        }
        List<String> lineas = servicioPaquetes.describirComposicion(paquete);
        vista.getAreaComposicion().setText(String.join("\n", lineas));
        vista.getLabelCostoTotal().setText(
                "Costo total del paquete: " + Formato.moneda(paquete.calcularCosto())
                        + "   |   Componentes: " + paquete.getComponentes().size()
                        + "   |   Requiere atencion presencial: "
                        + (paquete.requiereAtencionPresencial() ? "Si" : "No"));
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
