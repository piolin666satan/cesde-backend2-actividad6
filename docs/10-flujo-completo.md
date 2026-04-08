# 10 — Flujo Completo de una Petición HTTP

## Caso de uso: Crear un pedido

Vamos a seguir paso a paso lo que ocurre cuando el cliente (Postman) hace:

```
POST http://localhost:8080/api/pedidos
Content-Type: application/json

{
  "total": 85.50,
  "mesaId": 2,
  "meseroId": 1
}
```

---

## Paso 1 — Tomcat recibe la petición

Spring Boot incluye un servidor web llamado **Apache Tomcat** que escucha
en el puerto `8080`. Cuando llega una petición HTTP, Tomcat la procesa
y la entrega a Spring MVC para que encuentre el controlador adecuado.

```
Cliente  ──── HTTP POST /api/pedidos ────►  Tomcat (puerto 8080)
                                                     │
                                                     ▼
                                            Spring MVC busca:
                                            ¿Qué controlador maneja
                                            POST /api/pedidos?
```

---

## Paso 2 — Spring encuentra el controlador correcto

Spring MVC analiza:
- Método HTTP: `POST`
- URL: `/api/pedidos`

Y encuentra este método en `PedidoController.java`:

```java
@PostMapping              // ← Coincide con POST
@ResponseStatus(HttpStatus.CREATED)
public Pedido create(@RequestBody Pedido pedido) { // ← Usa la Entidad directamente
```

---

## Paso 3 — Jackson deserializa el JSON en la Entidad

**Jackson** es la librería que Spring usa para convertir JSON ↔ objetos Java.

El JSON del body:
```json
{ "total": 85.50, "mesaId": 2, "meseroId": 1 }
```

Se convierte automáticamente en una instancia de la entidad `Pedido`. Gracias a los métodos `@JsonProperty("mesaId")` y `@JsonProperty("meseroId")` que agregamos en la entidad, Jackson sabe cómo mapear esos IDs planos hacia los campos de relación.

Este proceso se llama **deserialización** y ocurre gracias a `@RequestBody`.

---

## Paso 4 — El controller llama al service

```java
// En PedidoController:
@PostMapping
@ResponseStatus(HttpStatus.CREATED)
public Pedido create(@RequestBody Pedido pedido) {
    return pedidoService.save(pedido);  // ← Pasa la entidad al service
}
```

---

## Paso 5 — El service ejecuta la lógica de negocio

```java
// En PedidoService:
@Transactional
public Pedido save(Pedido pedido) {

    // 1. Spring abre una transacción de base de datos

    // 2. Validar y obtener la Mesa (usando el id que Jackson puso en pedido.getMesa())
    Mesa mesa = mesaService.findEntity(pedido.getMesa().getId());

    // 3. Validar y obtener el Mesero
    Mesero mesero = meseroService.findEntity(pedido.getMesero().getId());

    // 4. Completar los datos de la entidad
    pedido.setEstado(EstadoPedido.PENDIENTE); // Regla de negocio
    pedido.setMesa(mesa);     // Asigna el objeto real cargado de la BD
    pedido.setMesero(mesero);

    // 5. Guardar en la base de datos
    return pedidoRepository.save(pedido);
    // → Hibernate genera y ejecuta el SQL INSERT
    // → La BD asigna el id y las fechas de auditoría
}
```

---

## Paso 6 — Jackson serializa la Entidad a JSON

El objeto `Pedido` (ahora con ID y fechas) se convierte en JSON para enviarlo de vuelta al cliente:

```json
{
  "id": 7,
  "total": 85.50,
  "estado": "PENDIENTE",
  "mesa": {
    "id": 2,
    "numeroMesa": 5,
    "ubicacion": "TERRAZA",
    "fechaRegistro": "2024-03-18T09:00:00"
  },
  "mesero": {
    "id": 1,
    "nombreComp": {
      "nombres": "Carlos",
      "apellidos": "Ruiz"
    },
    "telefono": "3001234567"
  },
  "fechaRegistro": "2024-03-18T15:05:00",
  "fechaModificacion": "2024-03-18T15:05:00"
}
```

---

## Diagrama completo del flujo

```
CLIENTE (Postman)
    │
    │  POST /api/pedidos  { "total":85.50, "mesaId":2, "meseroId":1 }
    ▼
┌─────────────────────────────────────────┐
│  TOMCAT / SPRING MVC                    │
│  Enruta la petición al Controlador      │
└────────────────────┬────────────────────┘
                     │
                     │  Jackson deserializa JSON → Pedido Entity
                     ▼
┌─────────────────────────────────────────┐
│  PedidoController                       │
│  Llama a pedidoService.save(pedido)     │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│  PedidoService  (@Transactional)        │
│  1. Carga objetos reales (Mesa/Mesero)  │──► SQL SELECT
│  2. Aplica lógica (Estado PENDIENTE)    │
│  3. Guarda en BD                        │──► SQL INSERT
│  Retorna la Entidad guardada             │
└────────────────────┬────────────────────┘
                     │
                     ▼
┌─────────────────────────────────────────┐
│  PedidoController                       │
│  Retorna Pedido (201 Created)           │
└────────────────────┬────────────────────┘
                     │
                     │  Jackson serializa Pedido → JSON
                     ▼
CLIENTE recibe: HTTP 201 + { JSON completo del pedido }
```

---

## Resumen de responsabilidades (Arquitectura Simplificada)

| Capa | Responsabilidad | No debe hacer |
|------|----------------|---------------|
| **Controller** | Recibir HTTP, llamar al servicio | Lógica de negocio pesada |
| **Service** | Validaciones, reglas, transacciones | Manejar protocolos HTTP |
| **Repository** | Consultas a BD (SQL/JPQL) | Lógica de negocio |
| **Entity** | Mapeo a BD y ahora también Contrato de API | Tener lógica compleja |

---

## Conceptos clave del proyecto actual

| Concepto | Implementación | Para qué sirve |
|----------|---------------|----------------|
| **Auditoría** | `@CreatedDate` | Fechas automáticas en BaseEntity |
| **Composición** | `@Embeddable` | Agrupar nombres en NombreCompleto |
| **Relaciones** | `@ManyToOne` | Conectar Pedido con Mesa y Mesero |
| **Exposición Directa** | Jackson + Entity | API rápida sin usar DTOs |
| **Mapeo de IDs** | `@JsonProperty` | Recibir "mesaId" y mapearlo al objeto |
| **Inyección** | `@Autowired` | Spring conecta las clases automáticamente |
| **Transacciones** | `@Transactional` | Todo o nada en la base de datos |
