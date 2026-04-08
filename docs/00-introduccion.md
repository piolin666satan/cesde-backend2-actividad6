# 00 — Introducción: ¿Qué es Spring Boot y JPA?

## ¿Qué vamos a construir?

Este proyecto es el **backend** de un sistema para gestionar pedidos en un restaurante.
Un backend es la parte del software que se ejecuta en el servidor y que:

- Recibe peticiones HTTP (como las que envía Postman o un sitio web)
- Procesa la lógica de negocio (validaciones, cálculos)
- Guarda y recupera datos de una base de datos
- Devuelve respuestas en formato JSON

---

## ¿Qué es Spring Boot?

**Spring Boot** es un framework de Java que facilita la creación de aplicaciones web y APIs REST.
Normalmente, configurar un servidor Java requería mucho código y archivos de configuración.
Spring Boot resuelve esto con el principio de **"convención sobre configuración"**: tiene valores
predeterminados inteligentes para que puedas enfocarte en escribir tu lógica de negocio.

### ¿Qué hace Spring Boot por nosotros?
- Incluye un servidor web integrado (Tomcat), no necesitas instalarlo por separado.
- Detecta automáticamente las clases que escribes y las conecta entre sí.
- Gestiona las conexiones a la base de datos.
- Convierte automáticamente los objetos Java a JSON y viceversa.

---

## ¿Qué es JPA e Hibernate?

### JPA (Java Persistence API)
**JPA** es una especificación (un conjunto de reglas) de Java que define **cómo mapear
objetos Java a tablas de una base de datos relacional**.

Sin JPA tendrías que escribir SQL manualmente:
```sql
INSERT INTO mesas (numero_mesa, ubicacion) VALUES (5, 'TERRAZA');
```

Con JPA simplemente haces:
```java
mesaRepository.save(mesa); // Spring genera el SQL por ti
```

### Hibernate
**Hibernate** es la implementación más popular de JPA. Spring Boot lo usa por debajo.
Cuando hablamos de JPA, Hibernate es quien realmente ejecuta el trabajo.

---

## ¿Qué es PostgreSQL?

**PostgreSQL** es el sistema de base de datos relacional que usamos en este proyecto.
Los datos del restaurante (mesas, meseros, pedidos) se guardan en tablas dentro de PostgreSQL.

### Requisitos previos
- Tener PostgreSQL instalado y corriendo en `localhost:5432`
- Crear la base de datos manualmente:
```sql
CREATE DATABASE restaurante_db;
```

---

## Tecnologías del proyecto

| Tecnología | Versión | Rol |
|-----------|---------|-----|
| Java | 21 | Lenguaje de programación |
| Spring Boot | 4.x | Framework de la aplicación |
| Spring Data JPA | (incluido) | Abstracción de la base de datos |
| Hibernate | (incluido) | Implementación de JPA |
| PostgreSQL | cualquiera | Base de datos relacional |
| Lombok | (incluido) | Reduce código repetitivo (getters, setters) |

---

## ¿Qué es Lombok?

**Lombok** es una librería que genera código Java automáticamente en tiempo de compilación.
Evita que tengas que escribir getters, setters, constructores y más.

```java
// Sin Lombok tendrías que escribir esto:
public String getNombres() { return nombres; }
public void setNombres(String nombres) { this.nombres = nombres; }

// Con Lombok solo pones una anotación en la clase:
@Getter
@Setter
public class InformacionPersonal { ... }
```

Anotaciones de Lombok que usamos:

| Anotación | Qué genera |
|-----------|-----------|
| `@Getter` | Métodos `getXxx()` para todos los campos |
| `@Setter` | Métodos `setXxx()` para todos los campos |
| `@NoArgsConstructor` | Constructor sin parámetros |
| `@AllArgsConstructor` | Constructor con todos los parámetros |
| `@RequiredArgsConstructor` | Constructor para campos `final` (usado en servicios) |

---

## Flujo general de la aplicación

```
Cliente (Postman/Frontend)
        │
        │  HTTP Request (JSON)
        ▼
   Controller        ← Recibe y devuelve peticiones HTTP
        │
        ▼
    Service          ← Lógica de negocio
        │
        ▼
   Repository        ← Consultas a la base de datos
        │
        ▼
   Base de datos (PostgreSQL)
```

> En el archivo `10-flujo-completo.md` verás este flujo en detalle con un ejemplo real.
