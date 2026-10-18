package pe.edu.ulima.tiendacapas.negocio;

/** CAPA DE NEGOCIO · Una regla de negocio no se cumplió. Su mensaje se muestra tal cual al usuario. */
public class VentaException extends RuntimeException {

    public VentaException(String mensaje) {
        super(mensaje);
    }
}
