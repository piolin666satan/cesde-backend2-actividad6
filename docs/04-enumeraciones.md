# 04 — Enumeraciones con `@Enumerated`

## ¿Qué es una enumeración (enum)?

Una **enumeración** es una lista de valores **fijos y predefinidos**.
Se usa cuando un campo solo puede tener un conjunto limitado de opciones.

### Ejemplos del mundo real
- Estado de un semáforo: ROJO, AMARILLO, VERDE
- Días de la semana: LUNES, MARTES, MIÉRCOLES...
- Talla de ropa: XS, S, M, L, XL

En nuestro restaurante:
- Estado de un pedido: PENDIENTE, PREPARANDO, SERVIDO, PAGADO
- Ubicación de una mesa: TERRAZA, SALON_PRINCIPAL, VIP, BAR

---

## ¿Por qué no usar un String directamente?

Podrías guardar el estado como un texto cualquiera:

```java
private String estado = "pendiente"; // ❌ Problemático
```

**Problemas:**
- Alguien podría escribir `"Pendiente"`, `"PENDIENTE"`, `"pendiente"`, `"pendiante"` (error de tipeo)
- No hay validación automática
- El IDE no te hace sugerencias
- Es fácil cometer errores

Con un enum:
```java
private EstadoPedido estado = EstadoPedido.PENDIENTE; // ✅ Solo puede ser uno de los 4 valores
```

El compilador te avisa si intentas asignar un valor que no existe.

---

## `EstadoPedido.java`

```java
package com.example.demo_basic.model.enums;

public enum EstadoPedido {
    PENDIENTE,    // El pedido fue tomado pero aún no se envió a cocina
    PREPARANDO,   // La cocina está preparando el pedido
    SERVIDO,      // El pedido fue entregado en la mesa
    PAGADO        // El cliente pagó y el pedido está cerrado
}
```

---

## `UbicacionMesa.java`

```java
package com.example.demo_basic.model.enums;

public enum UbicacionMesa {
    TERRAZA,           // Mesas al aire libre
    SALON_PRINCIPAL,   // Salón interior principal
    VIP,               // Sala privada VIP
    BAR                // Área de bar
}
```

---

## `@Enumerated(EnumType.STRING)` — Cómo se guarda en la BD

Esta es la anotación más importante cuando usas enums con JPA.

### Sin `@Enumerated` (comportamiento por defecto)
JPA guarda el enum como un **número entero** (el índice de la lista):
```
PENDIENTE  → 0
PREPARANDO → 1
SERVIDO    → 2
PAGADO     → 3
```

En la base de datos se vería:
```
estado
──────
  0
  2
```

**Problema:** Si mañana cambias el orden de los valores del enum, todos los datos
existentes en la BD quedan mal. Si insertas `URGENTE` en la posición 1, todos
los pedidos que eran `PREPARANDO` pasan a ser `URGENTE`. ¡Corrupción de datos!

### Con `@Enumerated(EnumType.STRING)` ✅
JPA guarda el nombre del enum como texto:
```
PENDIENTE  → "PENDIENTE"
PREPARANDO → "PREPARANDO"
```

En la base de datos se vería:
```
estado
──────────
PENDIENTE
SERVIDO
```

Esto es **legible**, **seguro** y **resistente a cambios** en el orden del enum.

---

## Uso en las entidades

### En `Pedido.java`
```java
@Enumerated(EnumType.STRING)          // Guardar como texto, no como número
@Column(name = "estado",
        nullable = false,
        length = 20)                   // VARCHAR(20) es suficiente para los nombres
private EstadoPedido estado;
```

### En `Mesa.java`
```java
@Enumerated(EnumType.STRING)
@Column(name = "ubicacion", nullable = false, length = 30)
private UbicacionMesa ubicacion;
```

---

## Cómo se ve en la base de datos

```
Tabla "pedidos":
  ┌──────┬────────┬───────────┐
  │  id  │ total  │  estado   │
  ├──────┼────────┼───────────┤
  │  1   │  85.5  │ PENDIENTE │  ← VARCHAR, no número
  │  2   │ 120.0  │ PREPARANDO│
  │  3   │  45.0  │ PAGADO    │
  └──────┴────────┴───────────┘

Tabla "mesas":
  ┌──────┬───────────────┬──────────────────┐
  │  id  │ numero_mesa   │    ubicacion     │
  ├──────┼───────────────┼──────────────────┤
  │  1   │      1        │ SALON_PRINCIPAL  │
  │  2   │      5        │ TERRAZA          │
  │  3   │     10        │ VIP              │
  └──────┴───────────────┴──────────────────┘
```

---

## Recibir y enviar enums desde la API

Cuando el cliente envía una petición:
```json
{ "ubicacion": "TERRAZA" }
```

Spring convierte automáticamente el texto `"TERRAZA"` al valor del enum `UbicacionMesa.TERRAZA`.
Este proceso se llama **deserialización** y lo hace la librería Jackson (incluida en Spring Boot).

Al enviar la respuesta, Jackson convierte el enum de vuelta a texto:
```json
{ "ubicacion": "TERRAZA" }
```

> 💡 El valor enviado y recibido **debe coincidir exactamente** con el nombre
> del valor del enum (respeta mayúsculas y minúsculas).
