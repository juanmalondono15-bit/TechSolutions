package com.techsolutions.servicio;

import com.techsolutions.excepciones.ValidacionException;
import com.techsolutions.fabricas.CatalogoComplementos;
import com.techsolutions.fabricas.FabricaComplemento;
import com.techsolutions.modelo.ComponenteServicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicacion que atiende la parte de <b>personalizacion</b> de la
 * primera funcionalidad: incorporar una o varias caracteristicas
 * complementarias a un servicio contratado.
 *
 * <p>Aqui se ve con claridad el patron <b>Decorator</b> desde la logica de la
 * aplicacion: cada complemento se aplica <i>envolviendo</i> el componente
 * anterior, de manera que se pueden acumular varios complementos
 * ({@code servicio -> +prioritaria -> +soporte -> +domicilio}) y el costo crece
 * sumando incrementos.</p>
 *
 * <p><b>El catalogo nunca se modifica:</b> los servicios personalizados se
 * guardan en esta lista aparte, y el objeto del catalogo se conserva intacto
 * (asi lo exige el enunciado: "la incorporacion de caracteristicas
 * complementarias no debera modificar la definicion original del servicio").</p>
 */
public class ServicioPersonalizacion {

    /** Registro (unico) de fabricas de complementos. */
    private final CatalogoComplementos complementos;

    /** Servicios personalizados creados por el usuario (no estan en el catalogo). */
    private final List<ComponenteServicio> personalizados = new ArrayList<>();

    /** Crea el servicio tomando el registro compartido de complementos. */
    public ServicioPersonalizacion() {
        this.complementos = CatalogoComplementos.obtenerInstancia();
    }

    /** @return complementos que se pueden aplicar */
    public List<FabricaComplemento> listarComplementosDisponibles() {
        return complementos.listar();
    }

    /** @return servicios personalizados creados en esta sesion */
    public List<ComponenteServicio> listarPersonalizados() {
        return List.copyOf(personalizados);
    }

    /**
     * Aplica un complemento a un componente, devolviendo un componente nuevo
     * (decorado) sin modificar el original.
     *
     * @param componente componente base (servicio del catalogo o personalizado)
     * @param fabrica    complemento que se desea incorporar
     * @return componente con el complemento incorporado
     * @throws ValidacionException si falta el componente o el complemento
     */
    public ComponenteServicio aplicarComplemento(ComponenteServicio componente, FabricaComplemento fabrica)
            throws ValidacionException {
        if (componente == null) {
            throw new ValidacionException("Debe seleccionar un servicio para personalizar.");
        }
        if (fabrica == null) {
            throw new ValidacionException("Debe seleccionar la caracteristica complementaria a incorporar.");
        }
        return fabrica.aplicarA(componente);
    }

    /**
     * Guarda un servicio personalizado para que pueda contratarse en paquetes y
     * solicitudes.
     *
     * @param personalizado componente decorado con uno o varios complementos
     * @throws ValidacionException si ya existe un servicio personalizado igual
     */
    public void guardarPersonalizado(ComponenteServicio personalizado) throws ValidacionException {
        if (personalizado == null) {
            throw new ValidacionException("No hay un servicio personalizado para guardar.");
        }
        boolean repetido = personalizados.stream()
                .anyMatch(p -> p.getNombre().equals(personalizado.getNombre()));
        if (repetido) {
            throw new ValidacionException("Ese servicio personalizado ya esta guardado.");
        }
        personalizados.add(personalizado);
    }
}
