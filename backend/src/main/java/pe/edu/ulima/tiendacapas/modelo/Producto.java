package pe.edu.ulima.tiendacapas.modelo;

import java.math.BigDecimal;

/** MODELO (compartido) · Producto del catálogo, tal como está en la tabla producto. Sin lógica. */
public record Producto(Long id, String nombre, BigDecimal precio, int stock) {
}
