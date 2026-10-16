package com.techsolutions.excepciones;

/**
 * Excepcion base (marcada) de la aplicacion TechSolutions.
 *
 * <p>Se define una raiz comun para todas las excepciones del negocio: asi la
 * interfaz grafica puede capturar errores esperados con un solo
 * {@code catch (TechSolutionsException e)} y mostrar al usuario un mensaje
 * claro, dejando aparte los errores de programacion.</p>
 */
public class TechSolutionsException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje descripcion del problema, pensada para mostrar al usuario
     */
    public TechSolutionsException(String mensaje) {
        super(mensaje);
    }

    /**
     * @param mensaje descripcion del problema
     * @param causa   excepcion original que produjo el error
     */
    public TechSolutionsException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
