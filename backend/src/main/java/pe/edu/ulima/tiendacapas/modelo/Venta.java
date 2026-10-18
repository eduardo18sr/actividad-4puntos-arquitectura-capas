package pe.edu.ulima.tiendacapas.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * MODELO (compartido) · Venta registrada. Sin lógica.
 * {@code producto} es el nombre del producto, para que el frontend no tenga que buscarlo.
 */
public record Venta(Long id, Long productoId, String producto, String cliente,
                    int cantidad, BigDecimal total, LocalDateTime fecha) {
}
