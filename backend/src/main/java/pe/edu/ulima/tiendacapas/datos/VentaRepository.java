package pe.edu.ulima.tiendacapas.datos;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import pe.edu.ulima.tiendacapas.modelo.Venta;

/** CAPA DE DATOS · SQL de la tabla venta. Guarda y lista ventas; no decide reglas de negocio. */
@Repository
public class VentaRepository {

    private static final Logger log = LoggerFactory.getLogger(VentaRepository.class);

    private static final String SQL_INSERTAR =
            "INSERT INTO venta (producto_id, cliente, cantidad, total, fecha) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT v.id, v.producto_id, p.nombre, v.cliente, v.cantidad, v.total, v.fecha "
            + "FROM venta v JOIN producto p ON p.id = v.producto_id ORDER BY v.id DESC";

    private final JdbcTemplate jdbcTemplate;

    public VentaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** Inserta la venta y la devuelve con el id que generó la BD. */
    public Venta guardar(Venta v) {
        log.info("[DATOS] {}  -- productoId={}, cliente={}, cantidad={}, total={}",
                SQL_INSERTAR, v.productoId(), v.cliente(), v.cantidad(), v.total());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conexion -> {
            PreparedStatement ps = conexion.prepareStatement(SQL_INSERTAR, new String[] {"id"});
            ps.setLong(1, v.productoId());
            ps.setString(2, v.cliente());
            ps.setInt(3, v.cantidad());
            ps.setBigDecimal(4, v.total());
            ps.setTimestamp(5, Timestamp.valueOf(v.fecha()));
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        return new Venta(id, v.productoId(), v.producto(), v.cliente(), v.cantidad(), v.total(), v.fecha());
    }

    public List<Venta> listar() {
        log.info("[DATOS] {}", SQL_LISTAR);
        return jdbcTemplate.query(SQL_LISTAR, this::mapear);
    }

    private Venta mapear(ResultSet rs, int fila) throws SQLException {
        return new Venta(
                rs.getLong("id"),
                rs.getLong("producto_id"),
                rs.getString("nombre"),
                rs.getString("cliente"),
                rs.getInt("cantidad"),
                rs.getBigDecimal("total"),
                rs.getTimestamp("fecha").toLocalDateTime());
    }
}
