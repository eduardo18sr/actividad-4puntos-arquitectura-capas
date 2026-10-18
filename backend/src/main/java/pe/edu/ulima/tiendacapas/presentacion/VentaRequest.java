package pe.edu.ulima.tiendacapas.presentacion;

/** CAPA DE PRESENTACIÓN · Datos que envía el formulario en POST /api/ventas. No trae el total: lo calcula el negocio. */
public record VentaRequest(Long productoId, String cliente, Integer cantidad) {
}
