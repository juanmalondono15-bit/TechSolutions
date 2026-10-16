package com.techsolutions.modelo;

import com.techsolutions.excepciones.ValidacionException;

import java.util.Objects;

/**
 * Cliente de TechSolutions.
 *
 * <p>Se identifica con tres datos obligatorios: documento, nombre completo y
 * correo electronico. En este parcial los clientes pueden estar precargados en
 * el sistema (no se exige su CRUD), pero la clase igual valida sus datos para
 * que el modelo sea consistente.</p>
 */
public class Cliente {

    private final String documento;
    private final String nombreCompleto;
    private final String correo;

    /**
     * Crea un cliente validando sus datos obligatorios.
     *
     * @param documento      numero de documento (no puede estar vacio)
     * @param nombreCompleto nombre y apellidos (no puede estar vacio)
     * @param correo         correo electronico (debe contener "@")
     * @throws ValidacionException si algun dato falta o tiene formato invalido
     */
    public Cliente(String documento, String nombreCompleto, String correo) throws ValidacionException {
        if (esVacio(documento)) {
            throw new ValidacionException("El documento del cliente es obligatorio.");
        }
        if (esVacio(nombreCompleto)) {
            throw new ValidacionException("El nombre completo del cliente es obligatorio.");
        }
        if (esVacio(correo) || !correo.contains("@")) {
            throw new ValidacionException("El correo electronico del cliente no es valido.");
        }
        this.documento = documento.trim();
        this.nombreCompleto = nombreCompleto.trim();
        this.correo = correo.trim();
    }

    private static boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    /** Se usa para mostrar el cliente en los {@code ComboBox} de JavaFX. */
    @Override
    public String toString() {
        return nombreCompleto + " - " + documento;
    }

    /** Dos clientes son iguales si tienen el mismo documento. */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Cliente cliente)) {
            return false;
        }
        return documento.equals(cliente.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documento);
    }
}
