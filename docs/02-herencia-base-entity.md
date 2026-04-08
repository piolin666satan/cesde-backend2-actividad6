# 02 — Herencia JPA con `@MappedSuperclass`

## ¿Qué es la herencia en programación?

La **herencia** es uno de los pilares de la Programación Orientada a Objetos (POO).
Permite que una clase "hijo" tome todos los campos y comportamientos de una clase "padre".

Imagina que tienes tres formularios de papel distintos (Mesa, Mesero, Pedido).
Todos ellos comparten un encabezado común: `Número de registro` y `Fecha`.
En lugar de imprimir ese encabezado en cada formulario por separado, lo pones
en una plantilla base y cada formulario la hereda.

En Java esto se hace con la palabra `extends`:

```java
public class Mesa extends BaseEntity { ... }
//     ↑ hijo          ↑ padre/plantilla
```

---

## El problema de la duplicación sin herencia

Sin herencia, tendrías que repetir estos campos en CADA entidad:

```java
// En Mesa.java
private Long id;
private LocalDateTime fechaRegistro;
private LocalDateTime fechaModificacion;

// En Mesero.java  ← ¡Mismo código repetido!
private Long id;
private LocalDateTime fechaRegistro;
private LocalDateTime fechaModificacion;

// En Pedido.java  ← ¡De nuevo!
private Long id;
private LocalDateTime fechaRegistro;
private LocalDateTime fechaModificacion;
```

Esto viola el principio **DRY** (Don't Repeat Yourself = No te repitas).
Si mañana quieres cambiar el nombre de `fechaRegistro`, tendrías que cambiarlo
en todos los archivos. Con herencia, lo cambias en un solo lugar.

---

## `@MappedSuperclass` — La clave

La anotación `@MappedSuperclass` le dice a JPA:

> "Esta clase define campos comunes para otras entidades, pero **no crees una tabla
> propia para ella**. Sus campos aparecerán directamente en las tablas de los hijos."

### Comparación

| Anotación | ¿Crea tabla? | ¿Sus campos van a los hijos? |
|-----------|-------------|------------------------------|
| `@Entity` | ✅ Sí | No (tiene su propia tabla) |
| `@MappedSuperclass` | ❌ No | ✅ Sí, en la tabla de cada hijo |

---

## El código de `BaseEntity.java`

```java
@MappedSuperclass                           // No crea tabla propia
@EntityListeners(AuditingEntityListener.class) // Activa la auditoría automática
@Getter                                     // Lombok genera getXxx()
@Setter                                     // Lombok genera setXxx()
public abstract class BaseEntity {          // abstract = no puedes instanciar esta clase directamente

    @Id                                     // Este campo es la llave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY = la BD asigna el id automáticamente (secuencia autoincremental de PostgreSQL)
    private Long id;

    @CreatedDate                            // Spring asigna este valor automáticamente al hacer INSERT
    @Column(name = "fecha_registro",
            nullable = false,              // No puede ser null en la BD
            updatable = false)             // Una vez asignado, nunca cambia (ni en UPDATE)
    private LocalDateTime fechaRegistro;

    @LastModifiedDate                       // Spring actualiza este valor automáticamente en cada UPDATE
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
```

### ¿Qué hace `abstract`?
La palabra `abstract` en la clase significa que **no puedes crear un objeto BaseEntity directamente**:

```java
BaseEntity b = new BaseEntity(); // ❌ Error de compilación
Mesa m = new Mesa();             // ✅ Correcto, Mesa sí es concreta
```

Esto tiene sentido porque BaseEntity es solo una plantilla, no representa nada del restaurante.

---

## Auditoría automática con `@CreatedDate` y `@LastModifiedDate`

### ¿Qué es la auditoría?
Auditar significa **registrar automáticamente cuándo se crean o modifican los datos**.
En un restaurante, quieres saber: ¿cuándo se creó este pedido? ¿cuándo fue modificado?

### ¿Cómo funciona?

**Paso 1:** `@EnableJpaAuditing` en la clase principal activa el sistema.

**Paso 2:** `@EntityListeners(AuditingEntityListener.class)` en `BaseEntity` le dice
a JPA que observe los eventos de esta entidad.

**Paso 3:** Spring detecta automáticamente:
- Cuando haces `repository.save(entity)` por primera vez → rellena `@CreatedDate` Y `@LastModifiedDate`.
- Cuando haces `repository.save(entity)` de nuevo (update) → solo actualiza `@LastModifiedDate`.

### Antes (con `@PrePersist` manual)
```java
@PrePersist
protected void onCreate() {
    this.fechaRegistro = LocalDateTime.now(); // Tú lo asignas a mano
}
```

### Ahora (con auditoría de Spring Data)
```java
@CreatedDate
private LocalDateTime fechaRegistro; // Spring lo asigna automáticamente
```

La ventaja de la segunda forma es que Spring también gestiona `fechaModificacion`
automáticamente en cada update, cosa que con `@PrePersist` tendrías que hacer
con una anotación adicional `@PreUpdate`.

---

## ¿Qué tablas se generan?

`BaseEntity` **no genera ninguna tabla**. Sus campos aparecen directamente en las tablas
de las entidades hijas:

```
Tabla "mesas":
  ┌──────┬───────────────┬─────────────────────┬──────────────────────┐
  │  id  │ numero_mesa   │   fecha_registro     │  fecha_modificacion  │
  ├──────┼───────────────┼─────────────────────┼──────────────────────┤
  │  1   │      5        │ 2024-03-18 10:00:00  │ 2024-03-18 10:05:00  │
  └──────┴───────────────┴─────────────────────┴──────────────────────┘
         ↑ de Mesa.java      ↑↑↑ heredados de BaseEntity ↑↑↑
```
