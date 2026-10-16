package com.techsolutions.excepciones;

/**
 * Se lanza cuando se intenta agregar un paquete dentro de si mismo, de forma
 * directa o indirecta.
 *
 * <p>Sin esta validacion el calculo recursivo del costo de un paquete entraria
 * en un ciclo infinito.</p>
 */
public class PaqueteCiclicoException extends TechSolutionsException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje descripcion del ciclo detectado
     */
    public PaqueteCiclicoException(String mensaje) {
        super(mensaje);
    }
}
