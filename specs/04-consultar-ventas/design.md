# Spec 04 · Consultar ventas — Diseño

## Recorrido por capas

```
GET /admin/ventas
  → [PRESENTACIÓN] VentasAdminController.listar()
      → [APLICACIÓN] ConsultaVentasService.listar()
          → [PERSISTENCIA] JdbcVentaDAO.listar()      (o MemoriaVentaDAO con el perfil memoria)
      ← List<VentaListadoDTO> + totales del listado
  → admin/ventas.html
```

## Aplicación

```java
public record VentaListadoDTO(Long id, String numero, LocalDateTime fecha, String clienteNombre,
                              int totalUnidades, BigDecimal total) {
    public static VentaListadoDTO desde(Venta venta)
}

public record ListadoVentasDTO(List<VentaListadoDTO> ventas, int cantidad, BigDecimal sumaTotales) {}
```

`ConsultaVentasService` (creado en la spec 03) agrega:

```java
public ListadoVentasDTO listar()   // ventaDAO.listar() → DTO; suma de totales con BigDecimal
```

El detalle reutiliza `ConsultaVentasService.obtener(id)` y `ComprobanteDTO` (spec 03): no se duplica el mapeo.

## Persistencia

Nada nuevo: `VentaDAO.listar()` y `buscarPorId()` ya existen (spec 03).

## Presentación

```java
/** Presentación · vista del vendedor sobre las ventas registradas. */
@Controller @RequestMapping("/admin/ventas")
public class VentasAdminController {
    VentasAdminController(ConsultaVentasService consultaVentasService)
    @GetMapping            String listar(Model model)            // → admin/ventas
    @GetMapping("/{id}")   String detalle(@PathVariable Long id, Model model)   // → admin/venta-detalle
}
```

### Plantillas

- `admin/ventas.html`: tabla (número como enlace al detalle, fecha `dd/MM/yyyy HH:mm`, cliente, unidades, total) y pie con cantidad y suma.
- `admin/venta-detalle.html`: puede incluir el mismo fragmento de tabla que `checkout/comprobante.html` (definir un fragmento Thymeleaf `fragmentos/detalle-venta.html` y usarlo en ambas para no repetir).

## Pruebas

| Prueba | Tipo | Criterios |
|---|---|---|
| `ConsultaVentasServiceTest` | Aplicación con `MemoriaVentaDAO` | CON-1.1 (orden), CON-1.2 (suma), CON-1.3 (vacío), CON-2.3 |
| `VentasAdminControllerTest` | `@WebMvcTest` | Vistas y atributos del modelo; 404 |
