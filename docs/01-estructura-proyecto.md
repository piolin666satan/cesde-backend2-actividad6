# 01 — Estructura del Proyecto

## ¿Por qué organizamos el código en paquetes?

Imagina que tienes una cocina de restaurante donde todo está mezclado: ingredientes,
platos sucios, recetas, facturas. Sería un caos. En programación pasa lo mismo:
si todo el código está junto, es muy difícil encontrar, entender y modificar cosas.

Los **paquetes** en Java son como las "secciones" o "cajones" de un proyecto.
Cada paquete agrupa clases que tienen una responsabilidad similar.

---

## Estructura completa del proyecto

```
demo_basic/
│
├── pom.xml                          ← Configuración de Maven (dependencias)
├── docs/                            ← Esta carpeta de tutoriales
│
└── src/
    └── main/
        ├── resources/
        │   └── application.properties   ← Configuración de la app (BD, JPA)
        │
        └── java/com/example/demo_basic/
            │
            ├── DemoBasicApplication.java     ← Punto de entrada de la app
            │
            ├── model/                        ← Todo lo relacionado al modelo de datos
            │   ├── entity/                   ← Entidades JPA (tablas)
            │   │   ├── BaseEntity.java
            │   │   ├── Mesa.java
            │   │   ├── Mesero.java
            │   │   └── Pedido.java
            │   │
            │   ├── embeddable/               ← Componentes reutilizables (@Embeddable)
            │   │   └── NombreCompleto.java
            │   │
            │   └── enums/                    ← Enumeraciones (valores fijos)
            │       ├── EstadoPedido.java
            │       └── UbicacionMesa.java
            │
            ├── repository/                   ← Acceso a la base de datos
            │   ├── MesaRepository.java
            │   ├── MeseroRepository.java
            │   └── PedidoRepository.java
            │
            ├── service/                      ← Lógica de negocio
            │   ├── MesaService.java
            │   ├── MeseroService.java
            │   └── PedidoService.java
            │
            ├── controller/                   ← Endpoints HTTP (API REST)
                ├── MesaController.java
                ├── MeseroController.java
                └── PedidoController.java
                ├── MesaController.java
                ├── MeseroController.java
                └── PedidoController.java
```

---

## Responsabilidad de cada capa

### 🏗️ `model/entity` — Las entidades
Son clases Java que representan una **tabla en la base de datos**.
Cada objeto de estas clases es una **fila** en esa tabla.

```
Clase Mesa  →  tabla "mesas" en PostgreSQL
objeto mesa →  fila con { id=1, numero_mesa=5, ubicacion='TERRAZA' }
```

### 🗄️ `repository` — Los repositorios
Son **interfaces** que le dicen a Spring cómo buscar, guardar y borrar datos.
Spring genera el código SQL automáticamente.

### ⚙️ `service` — Los servicios
Contienen la **lógica de negocio**: validaciones, transformaciones, reglas.
Actúan como intermediarios entre el controller y el repository.

### 📦 `model/embeddable` — Componentes
Son clases que agrupan campos relacionados para ser usados dentro de las entidades.
No tienen identidad propia en la base de datos (no tienen ID).

### 🌐 `controller` — Los controladores
Definen los **endpoints** de la API REST. Reciben peticiones HTTP, 
llaman al servicio correspondiente y devuelven la respuesta.

---

## El archivo `DemoBasicApplication.java`

```java
@SpringBootApplication    // Le dice a Spring que esta es la clase principal
@EnableJpaAuditing        // Activa el sistema de fechas automáticas (@CreatedDate, @LastModifiedDate)
public class DemoBasicApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoBasicApplication.class, args); // Arranca la aplicación
    }
}
```

- `@SpringBootApplication` detecta automáticamente todos los componentes del proyecto
  (controllers, services, repositories) sin que tengas que registrarlos manualmente.
- `@EnableJpaAuditing` activa la auditoría automática de fechas que veremos en el próximo capítulo.

---

## El archivo `application.properties`

```properties
# Nombre de la aplicación
spring.application.name=demo_basic

# Conexión a PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/restaurante_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update   # Crea/actualiza tablas automáticamente
spring.jpa.show-sql=true               # Muestra el SQL generado en consola
spring.jpa.properties.hibernate.format_sql=true  # SQL legible con sangría
```

### ¿Qué significa `ddl-auto=update`?
DDL = Data Definition Language (CREATE TABLE, ALTER TABLE...).
Con `update`, Hibernate **crea las tablas si no existen** y las **actualiza si cambian**,
sin borrar los datos que ya hay. Es ideal para desarrollo.

> ⚠️ En producción se usa `ddl-auto=none` y se gestionan los cambios con herramientas
> como Flyway o Liquibase para tener control total sobre el esquema de la BD.
