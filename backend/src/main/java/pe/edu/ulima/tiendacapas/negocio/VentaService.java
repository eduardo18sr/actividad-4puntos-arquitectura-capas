package pe.edu.ulima.tiendacapas.negocio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.ulima.tiendacapas.datos.ProductoRepository;
import pe.edu.ulima.tiendacapas.datos.VentaRepository;
import pe.edu.ulima.tiendacapas.modelo.Producto;
import pe.edu.ulima.tiendacapas.modelo.Venta;

/** CAPA DE NEGOCIO · Aplica las reglas RN-01 a RN-03 y coordina el registro de la venta en una transacción. */
@Service
public class VentaService {

    private static final Logger log = LoggerFactory.getLogger(VentaService.class);

    private static final int MAX_LARGO_CLIENTE = 80;

    private final ProductoRepository productoRepository;
    private final VentaRepository ventaRepository;

    public VentaService(ProductoRepository productoRepository, VentaRepository ventaRepository) {
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
    }

    public List<Producto> listarProductos() {
        log.info("[NEGOCIO] listarProductos");
        return productoRepository.listar();
    }

    public List<Venta> listarVentas() {
        log.info("[NEGOCIO] listarVentas");
        return ventaRepository.listar();
    }

    /**
     * Registra una venta: valida, calcula el total, descuenta el stock y la guarda.
     * Si algo falla se lanza {@link VentaException} y la transacción deshace todo.
     */
    @Transactional
    public Venta registrarVenta(Long productoId, String cliente, Integer cantidad) {
        log.info("[NEGOCIO] registrarVenta productoId={}, cliente={}, cantidad={}", productoId, cliente, cantidad);

        // RN-01: datos de la venta
        if (cantidad == null || cantidad < 1) {
            throw new VentaException("La cantidad debe ser al menos 1");
        }
        if (cliente == null || cliente.isBlank()) {
            throw new VentaException("Ingresa el nombre del cliente");
        }
        String clienteLimpio = cliente.trim();
        if (clienteLimpio.length() > MAX_LARGO_CLIENTE) {
            throw new VentaException("El nombre del cliente no puede superar " + MAX_LARGO_CLIENTE + " caracteres");
        }

        Producto producto = productoRepository.buscarPorId(productoId)
                .orElseThrow(() -> new VentaException("El producto no existe"));

        // RN-02: no vender más que el stock disponible
        if (cantidad > producto.stock()) {
            throw new VentaException("Stock insuficiente: disponible " + producto.stock());
        }

        // RN-03: el total lo calcula el backend
        BigDecimal total = producto.precio()
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);

        // RN-02: si otra venta se llevó el stock entre la lectura y este punto, el UPDATE no descuenta nada
        if (!productoRepository.descontarStock(productoId, cantidad)) {
            int stockActual = productoRepository.buscarPorId(productoId).map(Producto::stock).orElse(0);
            throw new VentaException("Stock insuficiente: disponible " + stockActual);
        }

        Venta venta = new Venta(null, productoId, producto.nombre(), clienteLimpio, cantidad, total,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Venta registrada = ventaRepository.guardar(venta);
        log.info("[NEGOCIO] venta registrada id={}, total=S/ {}", registrada.id(), registrada.total());
        return registrada;
    }
}
