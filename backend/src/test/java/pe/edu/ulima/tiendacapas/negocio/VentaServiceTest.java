package pe.edu.ulima.tiendacapas.negocio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.ulima.tiendacapas.modelo.Producto;
import pe.edu.ulima.tiendacapas.modelo.Venta;

/** Pruebas de la CAPA DE NEGOCIO contra la BD H2 con los datos semilla. Cada prueba revierte sus cambios. */
@SpringBootTest
@Transactional
class VentaServiceTest {

    @Autowired
    private VentaService ventaService;

    @Test
    void registraVentaYDescuentaStock() {
        Producto mouse = producto("Mouse");
        assertThat(mouse.stock()).isEqualTo(10);

        Venta venta = ventaService.registrarVenta(mouse.id(), "Ana", 3);

        assertThat(venta.id()).isNotNull();
        assertThat(venta.total()).isEqualByComparingTo(new BigDecimal("75.00"));
        assertThat(producto("Mouse").stock()).isEqualTo(7);
        assertThat(ventaService.listarVentas()).extracting(Venta::id).contains(venta.id());
    }

    @Test
    void rechazaStockInsuficiente() {
        Producto monitor = producto("Monitor 24\"");

        assertThatThrownBy(() -> ventaService.registrarVenta(monitor.id(), "Ana", 3))
                .isInstanceOf(VentaException.class)
                .hasMessage("Stock insuficiente: disponible 2");
        assertThat(producto("Monitor 24\"").stock()).isEqualTo(2);
        assertThat(ventaService.listarVentas()).isEmpty();
    }

    @Test
    void rechazaCantidadCero() {
        Producto mouse = producto("Mouse");

        assertThatThrownBy(() -> ventaService.registrarVenta(mouse.id(), "Ana", 0))
                .isInstanceOf(VentaException.class)
                .hasMessage("La cantidad debe ser al menos 1");
    }

    @Test
    void rechazaClienteMuyLargo() {
        Producto mouse = producto("Mouse");
        String cliente = "a".repeat(81);

        assertThatThrownBy(() -> ventaService.registrarVenta(mouse.id(), cliente, 1))
                .isInstanceOf(VentaException.class)
                .hasMessage("El nombre del cliente no puede superar 80 caracteres");
    }

    private Producto producto(String nombre) {
        return ventaService.listarProductos().stream()
                .filter(p -> p.nombre().equals(nombre))
                .findFirst()
                .orElseThrow();
    }
}
