// CAPA DE PRESENTACIÓN (cliente) · Habla con el backend solo por HTTP (/api/...).
// No calcula totales ni valida reglas: eso lo hace la capa de negocio. Aquí solo se envía y se muestra.

const selectProducto = document.getElementById('producto');
const inputCantidad = document.getElementById('cantidad');
const inputCliente = document.getElementById('cliente');
const formulario = document.getElementById('formulario-venta');
const botonRegistrar = document.getElementById('boton-registrar');
const mensaje = document.getElementById('mensaje');
const tablaVentas = document.getElementById('tabla-ventas');

function soles(monto) {
    return 'S/ ' + Number(monto).toFixed(2);
}

// El backend envía la fecha como "2026-10-17T21:20:53"; se muestra como "17/10/2026 21:20".
function formatearFecha(fecha) {
    const partes = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2})/.exec(fecha);
    if (!partes) return fecha;
    const [, anio, mes, dia, hora, minuto] = partes;
    return `${dia}/${mes}/${anio} ${hora}:${minuto}`;
}

function mostrarMensaje(texto, tipo) {
    mensaje.textContent = texto;
    mensaje.className = tipo;
}

async function cargarProductos() {
    console.log('[FRONTEND] GET /api/productos');
    const seleccionado = selectProducto.value;
    const res = await fetch('/api/productos');
    if (!res.ok) throw new Error('No se pudieron cargar los productos');
    const productos = await res.json();

    selectProducto.replaceChildren();
    for (const p of productos) {
        const opcion = document.createElement('option');
        opcion.value = p.id;
        opcion.textContent = `${p.nombre} — ${soles(p.precio)} (stock ${p.stock})`;
        selectProducto.appendChild(opcion);
    }
    // Tras registrar una venta se recargan los productos: se conserva el que estaba elegido.
    if (seleccionado) selectProducto.value = seleccionado;
}

async function cargarVentas() {
    console.log('[FRONTEND] GET /api/ventas');
    const res = await fetch('/api/ventas');
    if (!res.ok) throw new Error('No se pudieron cargar las ventas');
    const ventas = await res.json();

    tablaVentas.replaceChildren();
    if (ventas.length === 0) {
        const fila = tablaVentas.insertRow();
        const celda = fila.insertCell();
        celda.colSpan = 6;
        celda.className = 'vacio';
        celda.textContent = 'Aún no hay ventas registradas.';
        return;
    }
    for (const v of ventas) {
        const fila = tablaVentas.insertRow();
        // textContent (no innerHTML): el nombre del cliente lo escribe el usuario y no debe interpretarse como HTML.
        fila.insertCell().textContent = v.id;
        fila.insertCell().textContent = formatearFecha(v.fecha);
        fila.insertCell().textContent = v.cliente;
        fila.insertCell().textContent = v.producto;
        const cantidad = fila.insertCell();
        cantidad.textContent = v.cantidad;
        cantidad.className = 'numero';
        const total = fila.insertCell();
        total.textContent = soles(v.total);
        total.className = 'numero';
    }
}

async function registrarVenta(evento) {
    evento.preventDefault();

    // Se envían los datos tal cual; el total NO se envía (RN-03: lo calcula el backend).
    const venta = {
        productoId: selectProducto.value === '' ? null : Number(selectProducto.value),
        cliente: inputCliente.value,
        cantidad: inputCantidad.value === '' ? null : Number(inputCantidad.value),
    };

    botonRegistrar.disabled = true;
    console.log('[FRONTEND] POST /api/ventas', venta);
    try {
        const res = await fetch('/api/ventas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(venta),
        });
        const cuerpo = await res.json().catch(() => ({}));

        if (res.ok) {
            mostrarMensaje(`Venta registrada: ${soles(cuerpo.total)}`, 'exito');
            inputCantidad.value = '1';
            inputCliente.value = '';
            await Promise.all([cargarProductos(), cargarVentas()]);
        } else {
            // 400 {"error": "..."}: regla de negocio incumplida, el mensaje viene del backend.
            mostrarMensaje(cuerpo.error ?? `No se pudo registrar la venta (error ${res.status})`, 'error');
        }
    } catch (e) {
        mostrarMensaje('No se pudo conectar con el servidor', 'error');
    } finally {
        botonRegistrar.disabled = false;
    }
}

formulario.addEventListener('submit', registrarVenta);

Promise.all([cargarProductos(), cargarVentas()])
    .catch(() => mostrarMensaje('No se pudo conectar con el servidor', 'error'));
