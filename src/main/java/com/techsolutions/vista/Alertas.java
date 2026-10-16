package com.techsolutions.vista;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;

/**
 * Utilidad para mostrar mensajes al usuario.
 *
 * <p>Centraliza la creacion de alertas de JavaFX para que todas las vistas y
 * controladores muestren la informacion con el mismo estilo (principio DRY).</p>
 */
public final class Alertas {

    /** Clase de utilidades: no se instancia. */
    private Alertas() {
    }

    /**
     * Muestra un mensaje informativo.
     *
     * @param propietario ventana sobre la que se centra el mensaje (puede ser null)
     * @param titulo      titulo del mensaje
     * @param mensaje     contenido del mensaje
     */
    public static void informacion(Window propietario, String titulo, String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, propietario, titulo, mensaje);
    }

    /**
     * Muestra un mensaje de error (por ejemplo cuando falla una validacion).
     *
     * @param propietario ventana sobre la que se centra el mensaje (puede ser null)
     * @param titulo      titulo del mensaje
     * @param mensaje     contenido del mensaje
     */
    public static void error(Window propietario, String titulo, String mensaje) {
        mostrar(Alert.AlertType.ERROR, propietario, titulo, mensaje);
    }

    /**
     * Muestra un mensaje de advertencia.
     *
     * @param propietario ventana sobre la que se centra el mensaje (puede ser null)
     * @param titulo      titulo del mensaje
     * @param mensaje     contenido del mensaje
     */
    public static void advertencia(Window propietario, String titulo, String mensaje) {
        mostrar(Alert.AlertType.WARNING, propietario, titulo, mensaje);
    }

    /** Construye y muestra la alerta con el tipo indicado. */
    private static void mostrar(Alert.AlertType tipo, Window propietario, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.setTitle(titulo);
        alerta.setHeaderText(titulo);
        alerta.setContentText(mensaje);
        if (propietario != null) {
            alerta.initOwner(propietario);
        }
        alerta.showAndWait();
    }
}
