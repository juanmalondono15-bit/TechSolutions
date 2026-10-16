package com.techsolutions.modelo;

/**
 * Interfaz (puerto) que define <b>lo que el sistema espera de un proveedor de
 * tecnicos</b>, expresado en el lenguaje del negocio: se consulta por nombre de
 * especialidad y se recibe informacion estructurada.
 *
 * <p>Esta interfaz materializa dos principios SOLID:</p>
 * <ul>
 *   <li><b>Inversion de dependencias (D):</b> el sistema y la capa de
 *       aplicacion dependen de esta abstraccion, no de la plataforma externa
 *       {@code LegacySupport}.</li>
 *   <li><b>Segregacion de interfaces (I):</b> solo declara la operacion que el
 *       sistema realmente necesita.</li>
 * </ul>
 *
 * <p>El puente hacia la plataforma externa lo construye
 * {@code com.techsolutions.integracion.AdaptadorLegacySupport}, que implementa
 * esta interfaz (patron <b>Adapter</b>). Si la empresa cambia de proveedor,
 * basta con escribir otra implementacion de esta interfaz.</p>
 */
public interface ProveedorTecnicos {

    /**
     * Consulta el tecnico disponible para una especialidad.
     *
     * @param especialidad especialidad requerida por el servicio
     * @return informacion del tecnico, indicando si esta disponible y el costo
     *         de desplazamiento reportado por el proveedor
     */
    TecnicoEspecializado consultar(Especialidad especialidad);
}
