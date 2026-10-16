package com.techsolutions;

import com.techsolutions.controlador.ControladorPrincipal;
import com.techsolutions.vista.VistaPrincipal;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada de la aplicacion JavaFX.
 *
 * <p>Su unica responsabilidad es arrancar la interfaz: crea la vista principal,
 * arma el sistema MVC con {@link ControladorPrincipal} y muestra la ventana.
 * No contiene logica de negocio (asi lo exige la arquitectura MVC).</p>
 */
public class Main extends Application {

    /** Titulo de la ventana. */
    private static final String TITULO = "TechSolutions - Gestion de servicios tecnologicos";

    /** Ancho inicial de la ventana en pixeles. */
    private static final double ANCHO = 1180;

    /** Alto inicial de la ventana en pixeles. */
    private static final double ALTO = 760;

    /**
     * Arranca la interfaz grafica.
     *
     * @param escenario ventana principal que entrega JavaFX
     */
    @Override
    public void start(Stage escenario) {
        escenario.setTitle(TITULO);
        escenario.setScene(construirEscena());
        escenario.show();
    }

    /**
     * Construye la escena completa: vista principal mas controladores ya
     * enlazados.
     *
     * <p>Se separa de {@link #start(Stage)} para poder crear la interfaz sin
     * mostrarla (por ejemplo, en una verificacion automatica de arranque).</p>
     *
     * @return escena lista para mostrarse
     */
    public static Scene construirEscena() {
        VistaPrincipal vistaPrincipal = new VistaPrincipal();
        new ControladorPrincipal(vistaPrincipal).iniciar();
        Scene escena = new Scene(vistaPrincipal, ANCHO, ALTO);
        escena.getRoot().setStyle("-fx-font-size: 13px;");
        return escena;
    }

    /**
     * Metodo principal.
     *
     * @param args argumentos de la linea de comandos (no se usan)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
