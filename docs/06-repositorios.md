# 06 — Repositorios JPA

## ¿Qué es un repositorio?

Un **repositorio** es la capa del proyecto que se comunica directamente con la base de datos.
Es el encargado de ejecutar las consultas (buscar, insertar, actualizar, borrar).

Sin Spring Data JPA tendrías que escribir todo el SQL manualmente:
```java
// Sin Spring: SQL manual con JDBC
Connection con = DriverManager.getConnection("...");
PreparedStatement ps = con.prepareStatement("SELECT * FROM mesas WHERE id = ?");
ps.setLong(1, id);
ResultSet rs = ps.executeQuery();
// ... mapear resultados a objetos...
```

Con Spring Data JPA:
```java
// Con Spring: una línea
Optional<Mesa> mesa = mesaRepository.findById(id);
```

---

## `JpaRepository<T, ID>` — La interfaz mágica

Los 3 repositorios del proyecto extienden `JpaRepository`:

```java
public interface MesaRepository extends JpaRepository<Mesa, Long> { ... }
//                                                    ↑     ↑
//                                              La entidad  El tipo del id
```

`JpaRepository` ya trae **docenas de métodos implementados** listos para usar:

| Método | SQL que ejecuta | Descripción |
|--------|----------------|-------------|
| `findAll()` | `SELECT * FROM mesas` | Traer todos los registros |
| `findById(id)` | `SELECT * FROM mesas WHERE id = ?` | Buscar por id |
| `save(entity)` | `INSERT INTO ...` o `UPDATE ...` | Guardar o actualizar |
| `deleteById(id)` | `DELETE FROM mesas WHERE id = ?` | Eliminar por id |
| `existsById(id)` | `SELECT COUNT(*) FROM mesas WHERE id = ?` | ¿Existe? |
| `count()` | `SELECT COUNT(*) FROM mesas` | Contar registros |

> ✨ No necesitas implementar ninguno de estos métodos. Spring los genera automáticamente.

---

## `MesaRepository.java`

```java
@Repository  // Marca esta interfaz como componente de repositorio (Spring la gestiona)
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // Consulta derivada por ubicación
    List<Mesa> findByUbicacion(UbicacionMesa ubicacion);
    // SQL generado: SELECT * FROM mesas WHERE ubicacion = ?

    // Consulta de existencia por número de mesa
    boolean existsByNumeroMesa(Integer numeroMesa);
    // SQL generado: SELECT COUNT(*) > 0 FROM mesas WHERE numero_mesa = ?
}
```

---

## `MeseroRepository.java`

```java
@Repository
public interface MeseroRepository extends JpaRepository<Mesero, Long> {

    // Consulta sobre el campo apellidos
    List<Mesero> findByApellidosContainingIgnoreCase(String apellidos);
    // SQL generado: SELECT * FROM meseros WHERE apellidos ILIKE '%apellidos%'
}
```

### ¿Cómo funciona `findByApellidosContainingIgnoreCase`?

Spring lee el nombre del método como si fuera un "idioma":

```
findBy  → WHERE
Apellidos            → columna apellidos
Containing           → ILIKE '%valor%' (contiene)
IgnoreCase           → sin importar mayúsculas/minúsculas
```

---

## `PedidoRepository.java`

```java
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByEstado(EstadoPedido estado);
    // SQL: SELECT * FROM pedidos WHERE estado = ?

    List<Pedido> findByMesaId(Long mesaId);
    // SQL: SELECT * FROM pedidos WHERE mesa_id = ?
    // JPA entiende que "Mesa" es la relación y "Id" es su campo id

    List<Pedido> findByMeseroId(Long meseroId);
    // SQL: SELECT * FROM pedidos WHERE mesero_id = ?
}
```

---

## Consultas derivadas — El "idioma" de Spring Data

Spring Data lee el nombre del método y genera el SQL. Partes del nombre:

| Prefijo | Acción SQL |
|---------|-----------|
| `findBy...` | `SELECT ... WHERE` |
| `existsBy...` | `SELECT COUNT(*) > 0 WHERE` |
| `deleteBy...` | `DELETE WHERE` |
| `countBy...` | `SELECT COUNT(*) WHERE` |

| Sufijo de condición | SQL generado |
|--------------------|-------------|
| `Equals` (por defecto) | `= ?` |
| `Containing` | `LIKE '%?%'` |
| `StartingWith` | `LIKE '?%'` |
| `EndingWith` | `LIKE '%?'` |
| `IgnoreCase` | `ILIKE` (sin distinción mayús/minús) |
| `GreaterThan` | `> ?` |
| `LessThan` | `< ?` |
| `Between` | `BETWEEN ? AND ?` |
| `OrderBy...Asc/Desc` | `ORDER BY ... ASC/DESC` |

### Ejemplo combinado
```java
// Buscar pedidos PENDIENTES de una mesa específica, ordenados por fecha
List<Pedido> findByEstadoAndMesaIdOrderByFechaRegistroDesc(
    EstadoPedido estado, Long mesaId
);
```

---

## ¿Por qué es una `interface` y no una `class`?

En Java, una `interface` define **qué** debe hacer algo, sin especificar **cómo**.
Spring Data JPA genera una implementación concreta de la interfaz en tiempo de ejecución,
usando reflexión y proxies de Java. Tú nunca ves ni escribes esa implementación.

```java
// Tú escribes solo esto:
public interface MesaRepository extends JpaRepository<Mesa, Long> { }

// Spring genera en tiempo de ejecución algo equivalente a:
public class MesaRepositoryImpl implements MesaRepository {
    @Override
    public List<Mesa> findAll() {
        // hibernate ejecuta: SELECT * FROM mesas
    }
    // ... todos los demás métodos
}
```

---

## `@Repository` — ¿Es obligatorio?

No es estrictamente obligatorio en interfaces que extienden `JpaRepository`,
porque Spring ya las detecta automáticamente. Sin embargo es una buena práctica porque:

1. Hace el código más **legible** (queda claro que es un repositorio)
2. Permite a Spring traducir excepciones de JDBC a excepciones de Spring (`DataAccessException`)
