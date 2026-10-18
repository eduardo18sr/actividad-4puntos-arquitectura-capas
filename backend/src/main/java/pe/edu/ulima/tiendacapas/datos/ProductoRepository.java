package pe.edu.ulima.tiendacapas.datos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pe.edu.ulima.tiendacapas.modelo.Producto;

/** CAPA DE DATOS · SQL de la tabla producto. Convierte filas en objetos; no decide reglas de negocio. */
@Repository
public class ProductoRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductoRepository.class);

    private static final String SQL_LISTAR =
            "SELECT id, nombre, precio, stock FROM producto ORDER BY id";
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id, nombre, precio, stock FROM producto WHERE id = ?";
    private static final String SQL_DESCONTAR_STOCK =
            "UPDATE producto SET stock = stock - ? WHERE id = ? AND stock >= ?";

    private final JdbcTemplate jdbcTemplate;

    public ProductoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Producto> listar() {
        log.info("[DATOS] {}", SQL_LISTAR);
        return jdbcTemplate.query(SQL_LISTAR, this::mapear);
    }

    public Optional<Producto> buscarPorId(Long id) {
        log.info("[DATOS] {}  -- id={}", SQL_BUSCAR_POR_ID, id);
        return jdbcTemplate.query(SQL_BUSCAR_POR_ID, this::mapear, id).stream().findFirst();
    }

    /**
     * Descuenta el stock solo si alcanza: la comprobación y el descuento son una sola sentencia,
     * así dos ventas simultáneas no pueden dejar el stock negativo.
     *
     * @return {@code true} si se descontó; {@code false} si no había stock suficiente
     */
    public boolean descontarStock(Long id, int cantidad) {
        log.info("[DATOS] {}  -- cantidad={}, id={}", SQL_DESCONTAR_STOCK, cantidad, id);
        return jdbcTemplate.update(SQL_DESCONTAR_STOCK, cantidad, id, cantidad) == 1;
    }

    private Producto mapear(ResultSet rs, int fila) throws SQLException {
        return new Producto(
                rs.getLong("id"),
                rs.getString("nombre"),
                rs.getBigDecimal("precio"),
                rs.getInt("stock"));
    }
}
