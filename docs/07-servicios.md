# 07 — Capa de Servicio

## ¿Qué es un servicio?

La **capa de servicio** contiene la **lógica de negocio** de la aplicación.
Es el intermediario entre el Controller (quien recibe las peticiones HTTP)
y el Repository (quien accede a la BD).

### Analogía del restaurante
Piensa en el sistema real de un restaurante:
- El **mesero** (Controller) toma el pedido del cliente
- El **encargado** (Service) verifica si hay mesa disponible, valida el pedido, lo registra
- El **sistema de cocina** (Repository) guarda la orden en la BD

El servicio es donde viven las **reglas**, como:
- "No puede haber dos mesas con el mismo número"
- "Un pedido nuevo siempre empieza en estado PENDIENTE"
- "Para borrar un mesero, primero verifica que existe"

---

## Anotaciones clave de los servicios

### `@Service`
Marca la clase como un componente de servicio de Spring.
Spring lo detecta automáticamente y lo gestiona como un **bean** (objeto administrado).

### `@Autowired`
Inyecta automáticamente la dependencia en el campo. Spring busca un bean del tipo
correspondiente y lo asigna al campo marcado.

```java
@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository; // Spring inyecta esto automáticamente
```

### `@Transactional`
Envuelve el método en una **transacción de base de datos**.
Si algo falla a la mitad (ej: error de red), todos los cambios se revierten
automáticamente. Es como el "deshacer" de la base de datos.

```java
@Transactional   // Si falla algo, nada se guarda
public MesaResponse save(MesaRequest request) {
    // operaciones en la BD...
}
```

---

## `MesaService.java` — Análisis completo

```java
@Service
public class MesaService {

    @Autowired
    private MesaRepository mesaRepository;

    // ── SOLO LECTURA ──────────────────────────────────────────────────────

    public List<Mesa> findAll() {
        return mesaRepository.findAll();   // Trae todos los registros de la BD
    }

    public Mesa findById(Long id) {
        return findEntity(id); // findEntity lanza excepción si no existe
    }

    // ── ESCRITURA (con @Transactional para garantizar atomicidad) ──────────

    @Transactional
    public Mesa save(Mesa mesa) {
        // Regla de negocio: no duplicar números de mesa
        if (mesaRepository.existsByNumeroMesa(mesa.getNumeroMesa())) {
            throw new IllegalArgumentException(
                    "Ya existe una mesa con el número: " + mesa.getNumeroMesa());
        }
        return mesaRepository.save(mesa);
    }

    @Transactional
    public Mesa update(Long id, Mesa request) {
        Mesa mesa = findEntity(id);         // Lanza excepción si no existe
        mesa.setNumeroMesa(request.getNumeroMesa());
        mesa.setUbicacion(request.getUbicacion());
        return mesaRepository.save(mesa);
    }

    @Transactional
    public void delete(Long id) {
        findEntity(id);                    // Valida que existe antes de borrar
        mesaRepository.deleteById(id);
    }

    // ── MÉTODOS INTERNOS ───────────────────────────────────────────────────

    public Mesa findEntity(Long id) {
        return mesaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mesa no encontrada con id: " + id));
    }
}
```

---

## `PedidoService.java` — Relaciones entre Entidades

```java
@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private MesaService mesaService;      

    @Autowired
    private MeseroService meseroService;  

    @Transactional
    public Pedido save(Pedido pedido) {
        // Busca y valida que la Mesa y el Mesero existen usando los IDs del JSON
        Mesa mesa = mesaService.findEntity(pedido.getMesa().getId());
        Mesero mesero = meseroService.findEntity(pedido.getMesero().getId());

        pedido.setEstado(EstadoPedido.PENDIENTE); // ← Regla: siempre empieza PENDIENTE
        pedido.setMesa(mesa);
        pedido.setMesero(mesero);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido cambiarEstado(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = findEntity(id);
        pedido.setEstado(nuevoEstado);      
        return pedidoRepository.save(pedido);
    }

    public Pedido findEntity(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido no encontrado con id: " + id));
    }
}
```

---

## ¿Qué es la Inyección de Dependencias?

**Inyección de Dependencias (DI)** es cuando Spring se encarga de crear y
proporcionar los objetos que una clase necesita, en vez de que tú los
instancies manualmente con `new`.

```java
// Sin DI (mal enfoque, crea acoplamiento):
public class MesaService {
    private MesaRepository mesaRepository = new MesaRepository(); // ❌
}

// Con DI (@Autowired):
public class MesaService {
    @Autowired
    private MesaRepository mesaRepository; // Spring inyecta esto ✅
}
```

**Ventajas:**
1. Más fácil de **testear** (puedes usar versiones falsas / mocks)
2. Menos **acoplamiento** entre clases
3. Spring gestiona el **ciclo de vida** del objeto (singleton por defecto)
