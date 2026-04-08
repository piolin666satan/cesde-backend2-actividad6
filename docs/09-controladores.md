# 09 — Controladores REST

## ¿Qué es un controlador?

El **controlador** es la puerta de entrada de la aplicación.
Recibe las peticiones HTTP que llegan desde el cliente (Postman, navegador, app móvil),
las delega al servicio correspondiente y devuelve la respuesta HTTP.

---

## ¿Qué es una API REST?

**REST** (Representational State Transfer) es un estilo de arquitectura para construir
servicios web. Define convenciones sobre cómo usar HTTP:

| Método HTTP | Acción | Ejemplo |
|-------------|--------|---------|
| `GET` | Leer datos | Obtener todas las mesas |
| `POST` | Crear un recurso | Crear una nueva mesa |
| `PUT` | Actualizar completo | Actualizar todos los campos de una mesa |
| `PATCH` | Actualizar parcial | Cambiar solo el estado de un pedido |
| `DELETE` | Eliminar | Borrar una mesa |

---

## Anotaciones de los controladores

| Anotación | Significado |
|-----------|------------|
| `@RestController` | Combina `@Controller` + `@ResponseBody`. Indica que todos los métodos devuelven JSON |
| `@RequestMapping("/api/mesas")` | Prefijo base de todos los endpoints de esta clase |
| `@GetMapping` | Maneja peticiones GET |
| `@PostMapping` | Maneja peticiones POST |
| `@PutMapping` | Maneja peticiones PUT |
| `@PatchMapping` | Maneja peticiones PATCH |
| `@DeleteMapping` | Maneja peticiones DELETE |
| `@PathVariable` | Extrae valor de la URL: `/api/mesas/{id}` → `Long id` |
| `@RequestBody` | Deserializa el JSON del body en un objeto Java |
| `@RequestParam` | Extrae parámetros de la URL: `/buscar?apellidos=...` |

---

## `MesaController.java` — Análisis completo

```java
@RestController
@RequestMapping("/api/mesas")   
public class MesaController {

    @Autowired
    private MesaService mesaService;

    // GET /api/mesas
    @GetMapping
    public List<Mesa> getAll() {
        return mesaService.findAll();
    }

    // GET /api/mesas/5
    @GetMapping("/{id}")
    public Mesa getById(@PathVariable Long id) {
        return mesaService.findById(id);
    }

    // POST /api/mesas
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mesa create(@RequestBody Mesa mesa) {
        return mesaService.save(mesa);
    }

    // PUT /api/mesas/5
    @PutMapping("/{id}")
    public ResponseEntity<Mesa> update(@PathVariable Long id, @RequestBody Mesa mesa) {
        return ResponseEntity.ok(mesaService.update(id, mesa));
    }

    // DELETE /api/mesas/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mesaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## `PedidoController.java` — Cambio de estado con `@PatchMapping`

```java
// PATCH /api/pedidos/3/estado/PREPARANDO
@PatchMapping("/{id}/estado/{estado}")
public ResponseEntity<Pedido> cambiarEstado(
        @PathVariable Long id,
        @PathVariable EstadoPedido estado) {  
    return ResponseEntity.ok(pedidoService.cambiarEstado(id, estado));
}
```

---

## Tabla resumen de todos los endpoints

### Mesas — `/api/mesas`
| Método | URL | Body / Params | Respuesta |
|--------|-----|---------------|-----------|
| GET | `/api/mesas` | — | `List<Mesa>` |
| GET | `/api/mesas/{id}` | — | `Mesa` |
| POST | `/api/mesas` | `Mesa` (JSON) | `Mesa` (201) |
| PUT | `/api/mesas/{id}` | `Mesa` (JSON) | `Mesa` |
| DELETE | `/api/mesas/{id}` | — | (204) |

### Meseros — `/api/meseros`
| Método | URL | Body / Params | Respuesta |
|--------|-----|---------------|-----------|
| GET | `/api/meseros` | — | `List<Mesero>` |
| GET | `/api/meseros/{id}` | — | `Mesero` |
| POST | `/api/meseros` | `Mesero` | `Mesero` (201) |
| DELETE | `/api/meseros/{id}` | — | (204) |

### Pedidos — `/api/pedidos`
| Método | URL | Body / Params | Respuesta |
|--------|-----|---------------|-----------|
| GET | `/api/pedidos` | — | `List<Pedido>` |
| GET | `/api/pedidos/{id}` | — | `Pedido` |
| POST | `/api/pedidos` | `Pedido` (JSON) | `Pedido` (201) |
| PATCH | `/api/pedidos/{id}/estado/{estado}` | — | `Pedido` |
| DELETE | `/api/pedidos/{id}` | — | (204) |
