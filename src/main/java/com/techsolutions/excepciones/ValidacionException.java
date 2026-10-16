package com.techsolutions.excepciones;

/**
 * Se lanza cuando los datos que ingresa el usuario no cumplen las reglas de
 * negocio: campos obligatorios vacios, costos negativos, codigos repetidos,
 * correos sin formato valido, etc.
 *
 * <p>Cumplir esta validacion es lo que garantiza que <b>no sea posible
 * registrar una solicitud sin la informacion esencial</b>.</p>
 */
public class ValidacionException extends TechSolutionsException {

    private static final long serialVersionUID = 1L;

    /**
     * @param mensaje explicacion del dato invalido
     */
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
