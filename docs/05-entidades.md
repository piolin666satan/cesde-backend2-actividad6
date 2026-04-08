# 05 — Entidades JPA: Mesa, Mesero y Pedido

## ¿Qué es una entidad JPA?

Una **entidad** es una clase Java que representa una **tabla en la base de datos**.
JPA convierte automáticamente:
- La clase → tabla
- Los campos → columnas
- Los objetos → filas

Para que JPA reconozca una clase como entidad, debe llevar la anotación `@Entity`.

---

## Entidad `Mesa`

```java
@Entity                    // Esta clase es una tabla en la BD
@Table(name = "mesas")     // El nombre exacto de la tabla será "mesas"
@Getter                    // Lombok: genera todos los getters
@Setter                    // Lombok: genera todos los setters
@NoArgsConstructor         // Lombok: genera constructor vacío (JPA lo requiere)
@AllArgsConstructor        // Lombok: genera constructor con todos los campos
public class Mesa extends BaseEntity {
//                 ↑ Hereda: id, fechaRegistro, fechaModificacion

    @Column(name = "numero_mesa", nullable = false, unique = true)
    //                                               ↑ No puede haber dos mesas con el mismo número
    private Integer numeroMesa;

    @Enumerated(EnumType.STRING)           // Guarda "TERRAZA" en vez de 0
    @Column(name = "ubicacion", nullable = false, length = 30)
    private UbicacionMesa ubicacion;
}
```

### Tabla generada en PostgreSQL
```sql
CREATE TABLE mesas (
    id                 BIGSERIAL PRIMARY KEY,
    fecha_registro     TIMESTAMP NOT NULL,
    fecha_modificacion TIMESTAMP,
    numero_mesa        INTEGER NOT NULL UNIQUE,
    ubicacion          VARCHAR(30) NOT NULL
);
```

---

## Entidad `Mesero`

```java
@Entity
@Table(name = "meseros")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mesero extends BaseEntity {
//                    ↑ Hereda: id, fechaRegistro, fechaModificacion

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "telefono", length = 20)
    private String telefono;
}
```

### Tabla generada en PostgreSQL
```sql
CREATE TABLE meseros (
    id                 BIGSERIAL PRIMARY KEY,
    fecha_registro     TIMESTAMP NOT NULL,
    fecha_modificacion TIMESTAMP,
    nombres            VARCHAR(100) NOT NULL,
    apellidos          VARCHAR(100) NOT NULL,
    telefono           VARCHAR(20)
);
```

---

## Entidad `Pedido` y las relaciones `@ManyToOne`

Esta es la entidad más compleja porque tiene **relaciones** con otras tablas.

```java
@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido extends BaseEntity {

    @Column(name = "total", nullable = false)
    private Double total;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPedido estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesero_id", nullable = false)
    private Mesero mesero;
}
```

---

## Explicación detallada de `@ManyToOne`

### ¿Qué significa Many-To-One?
Piensa en la relación entre Pedidos y Mesas:
- **Una Mesa** puede tener **muchos Pedidos** (a lo largo del día)
- **Un Pedido** pertenece a **una sola Mesa**

Esto es una relación "Muchos a Uno" visto desde el lado del Pedido:
**Muchos** Pedidos → **Una** Mesa

```
Mesa 1 ──┬── Pedido 1
         ├── Pedido 2
         └── Pedido 3

Mesa 2 ──┬── Pedido 4
         └── Pedido 5
```

### `@JoinColumn` — La llave foránea (FK)
```java
@JoinColumn(name = "mesa_id", nullable = false)
```
- `name = "mesa_id"`: JPA crea una columna llamada `mesa_id` en la tabla `pedidos`
- Esta columna almacena el `id` de la mesa correspondiente
- `nullable = false`: todo pedido DEBE tener una mesa asignada

### `FetchType.LAZY` — Carga perezosa
```java
@ManyToOne(fetch = FetchType.LAZY)
```
Por defecto (`EAGER`), cuando cargas un Pedido, JPA también carga automáticamente
el Mesero completo y la Mesa completa. Si tienes 1000 pedidos, son 3000 consultas.

Con `LAZY`, JPA **no carga la Mesa ni el Mesero** hasta que los necesites:
```java
Pedido p = pedidoRepository.findById(1L);
// Aquí Mesa y Mesero NO están cargados aún (solo tiene sus ids)

String ubicacion = p.getMesa().getUbicacion(); // ← AQUÍ sí se carga la Mesa
```
Esto mejora el rendimiento considerablemente.

---

## Tabla generada para `Pedido`

```sql
CREATE TABLE pedidos (
    id                 BIGSERIAL PRIMARY KEY,
    fecha_registro     TIMESTAMP NOT NULL,
    fecha_modificacion TIMESTAMP,
    total              DOUBLE PRECISION NOT NULL,
    estado             VARCHAR(20) NOT NULL,
    mesa_id            BIGINT NOT NULL REFERENCES mesas(id),    -- FK
    mesero_id          BIGINT NOT NULL REFERENCES meseros(id)   -- FK
);
```

---

## Diagrama completo de relaciones

```
┌───────────────┐            ┌──────────────────────────────────┐
│    meseros    │            │             pedidos              │
├───────────────┤    1:N     ├──────────────────────────────────┤
│ id (PK)       │◄───────────│ id (PK)                          │
│ fecha_registro│            │ fecha_registro                   │
│ fecha_modif.  │            │ fecha_modificacion               │
│ nombres       │            │ total                            │
│ apellidos     │            │ estado (VARCHAR)                 │
│ telefono      │            │ mesa_id    (FK) ────────────────►│
└───────────────┘            │ mesero_id  (FK) ────────────────►│
                             └──────────────────────────────────┘
┌───────────────┐                              ↑
│     mesas     │                              │
├───────────────┤                              │
│ id (PK)       │◄─────────────────────────────┘
│ fecha_registro│
│ fecha_modif.  │
│ numero_mesa   │
│ ubicacion     │
└───────────────┘
```

### Anotaciones JPA más comunes

| Anotación | Significado |
|-----------|------------|
| `@Entity` | La clase es una tabla |
| `@Table(name="...")` | Nombre personalizado de la tabla |
| `@Column(...)` | Configuración de una columna |
| `@Id` | Llave primaria |
| `@GeneratedValue` | Id auto-generado por la BD |
| `@Enumerated` | Cómo guardar un enum |
| `@ManyToOne` | Relación muchos a uno |
| `@JoinColumn` | Define el nombre de la columna FK |
