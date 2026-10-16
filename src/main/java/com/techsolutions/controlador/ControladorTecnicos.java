package com.techsolutions.controlador;

import com.techsolutions.modelo.Especialidad;
import com.techsolutions.modelo.ProveedorTecnicos;
import com.techsolutions.modelo.TecnicoEspecializado;
import com.techsolutions.util.Formato;
import com.techsolutions.vista.Alertas;
import com.techsolutions.vista.VistaTecnicos;
import javafx.stage.Window;

import java.util.function.Supplier;

/**
 * Controlador de la funcionalidad 4 (integracion): <b>consulta de
 * disponibilidad de tecnicos especializados</b>.
 *
 * <p>Capa <b>Controlador</b> del MVC. Depende de la interfaz
 * {@link ProveedorTecnicos}, no del adaptador concreto: si manana cambia el
 * proveedor externo, este controlador no se modifica (principio de inversion de
 * dependencias).</p>
 *
 * <p>La cadena cruda de {@code LegacySupport} se obtiene mediante la funcion
 * {@code respuestaCruda}, que se inyecta desde el punto de armado de la
 * aplicacion ({@code ControladorPrincipal}); asi el controlador no necesita
 * conocer las clases de la capa de integracion.</p>
 */
public class ControladorTecnicos {

    private final VistaTecnicos vista;
    private final ProveedorTecnicos proveedorTecnicos;
    private final Supplier<String> respuestaCruda;

    /**
     * @param vista              vista de la pestana de tecnicos
     * @param proveedorTecnicos  proveedor de tecnicos a consultar
     * @param respuestaCruda     funcion que devuelve la ultima respuesta cruda del sistema externo
     */
    public ControladorTecnicos(VistaTecnicos vista, ProveedorTecnicos proveedorTecnicos,
                               Supplier<String> respuestaCruda) {
        this.vista = vista;
        this.proveedorTecnicos = proveedorTecnicos;
        this.respuestaCruda = respuestaCruda;
    }

    /**
     * Enlaza los eventos y carga las especialidades disponibles.
     */
    public void iniciar() {
        vista.getComboEspecialidades().getItems().setAll(Especialidad.values());
        vista.getComboEspecialidades().getSelectionModel().selectFirst();
        vista.getBotonConsultar().setOnAction(evento -> consultar());

        if (vista.getComboEspecialidades().getValue() != null) {
            consultar();
        }
    }

    /** Consulta la disponibilidad del tecnico de la especialidad elegida. */
    private void consultar() {
        Especialidad especialidad = vista.getComboEspecialidades().getValue();
        if (especialidad == null) {
            Alertas.error(ventana(), "Consulta de tecnicos", "Seleccione una especialidad.");
            return;
        }

        // El sistema pide por NOMBRE de especialidad; el adaptador hace la traduccion.
        TecnicoEspecializado tecnico = proveedorTecnicos.consultar(especialidad);

        vista.getAreaRespuestaCruda().setText(
                "Especialidad solicitada por el sistema: " + especialidad.getNombreVisible() + "\n"
                        + "Codigo numerico enviado a LegacySupport: " + especialidad.getCodigoLegacy() + "\n"
                        + "Respuesta recibida (texto separado por '|'): " + respuestaCruda.get());

        vista.getAreaInformacion().setText(
                "Identificador del tecnico : " + tecnico.getIdentificador() + "\n"
                        + "Especialidad              : " + tecnico.getEspecialidad().getNombreVisible() + "\n"
                        + "Disponible                : " + (tecnico.isDisponible() ? "Si" : "No") + "\n"
                        + "Costo de desplazamiento   : " + Formato.moneda(tecnico.getCostoDesplazamiento()) + "\n\n"
                        + (tecnico.isDisponible()
                        ? "El sistema puede confirmar solicitudes que requieran esta especialidad."
                        : "El sistema informara que no hay tecnico disponible y evitara confirmar "
                        + "las solicitudes que requieran esta especialidad."));
    }

    /** @return la ventana actual (puede ser null si la vista aun no se muestra) */
    private Window ventana() {
        return vista.getScene() == null ? null : vista.getScene().getWindow();
    }
}
