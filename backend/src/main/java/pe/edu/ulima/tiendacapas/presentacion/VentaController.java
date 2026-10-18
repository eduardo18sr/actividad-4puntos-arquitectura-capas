package pe.edu.ulima.tiendacapas.presentacion;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import pe.edu.ulima.tiendacapas.modelo.Producto;
import pe.edu.ulima.tiendacapas.modelo.Venta;
import pe.edu.ulima.tiendacapas.negocio.VentaException;
import pe.edu.ulima.tiendacapas.negocio.VentaService;

/** CAPA DE PRESENTACIÓN · Recibe las peticiones HTTP del frontend y responde JSON. No valida reglas: todo pasa al negocio. */
@RestController
@RequestMapping("/api")
public class VentaController {

    private static final Logger log = LoggerFactory.getLogger(VentaController.class);

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @GetMapping("/productos")
    public List<Producto> listarProductos() {
        log.info("[PRESENTACIÓN] GET /api/productos");
        return ventaService.listarProductos();
    }

    @GetMapping("/ventas")
    public List<Venta> listarVentas() {
        log.info("[PRESENTACIÓN] GET /api/ventas");
        return ventaService.listarVentas();
    }

    @PostMapping("/ventas")
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrarVenta(@RequestBody VentaRequest request) {
        log.info("[PRESENTACIÓN] POST /api/ventas {}", request);
        return ventaService.registrarVenta(request.productoId(), request.cliente(), request.cantidad());
    }

    /** Convierte una regla de negocio incumplida en 400 {"error": "mensaje"} para que el frontend lo muestre. */
    @ExceptionHandler(VentaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> manejarVentaException(VentaException e) {
        log.info("[PRESENTACIÓN] 400 {}", e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
