# 11 — OpenAPI y Swagger UI

## ¿Qué es OpenAPI?

**OpenAPI** (antes conocido como Swagger) es un estándar para **documentar APIs REST**.
Define cómo describir los endpoints, los parámetros que reciben, los tipos de datos
que devuelven y los posibles códigos de respuesta, todo en un formato legible tanto
por humanos como por máquinas (JSON o YAML).

### Ventajas de usar OpenAPI
- **Documentación viva**: Se genera automáticamente del código fuente.
  Nunca queda desactualizada.
- **Interfaz interactiva**: Swagger UI permite probar los endpoints directamente
  desde el navegador, sin necesidad de Postman.
- **Contratos de API**: Otros equipos o apps pueden leer el JSON de OpenAPI
  para saber exactamente cómo usar tu API.

---

## Librería usada: SpringDoc OpenAPI

Para integrar OpenAPI en Spring Boot 3+ se usa **SpringDoc**:

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.6</version>
</dependency>
```

Con solo agregar esta dependencia, SpringDoc:
1. Escanea todos tus `@RestController` automáticamente
2. Genera el JSON de OpenAPI en `/api-docs`
3. Sirve la interfaz Swagger UI en `/swagger-ui.html`

---

## URLs disponibles al ejecutar la aplicación

| URL | Descripción |
|-----|-------------|
| `http://localhost:8080/swagger-ui.html` | 🌐 Interfaz visual interactiva |
| `http://localhost:8080/api-docs` | 📄 Especificación OpenAPI en JSON |
| `http://localhost:8080/api-docs.yaml` | 📄 Especificación OpenAPI en YAML |

---

## Configuración en `application.properties`

```properties
# Ruta de Swagger UI
springdoc.swagger-ui.path=/swagger-ui.html

# Ruta del JSON OpenAPI
springdoc.api-docs.path=/api-docs

# Ordenar tags y operaciones alfabéticamente
springdoc.swagger-ui.tags-sorter=alpha
springdoc.swagger-ui.operations-sorter=alpha
```

---

## Clase `OpenApiConfig.java`

Esta clase personaliza la información general de la documentación:

```java
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI restauranteOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Restaurante")
                        .description("Descripción de la API...")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("dev@restaurante.com"))
                        .license(new License().name("MIT")));
    }
}
```

Esto aparece como el encabezado en Swagger UI.

---

## Anotaciones en los Controladores

### `@Tag` — Agrupa endpoints en Swagger UI
```java
@Tag(name = "Mesas", description = "Gestión de mesas del restaurante")
public class MesaController { ... }
```
Crea una sección llamada "Mesas" en Swagger UI donde aparecen todos sus endpoints.

### `@Operation` — Describe un endpoint
```java
@Operation(summary = "Crear un pedido", description = "Solo requiere mesaId, meseroId y total")
@PostMapping
public PedidoResponse create(@RequestBody PedidoRequest request) { ... }
```

### `@ApiResponse` / `@ApiResponses` — Documenta las respuestas posibles
```java
@ApiResponses({
    @ApiResponse(responseCode = "201", description = "Mesa creada exitosamente"),
    @ApiResponse(responseCode = "400", description = "Número de mesa duplicado")
})
```

### `@Parameter` — Describe parámetros de la URL o query
```java
public ResponseEntity<MesaResponse> getById(
    @Parameter(description = "ID de la mesa") @PathVariable Long id) { ... }
```

---

## Anotaciones en los DTOs

### `@Schema` — Documenta clases y campos

```java
@Schema(description = "Datos para crear o actualizar una mesa")
public class MesaRequest {

    @Schema(description = "Número identificador de la mesa", example = "5")
    private Integer numeroMesa;

    @Schema(
        description = "Categoría de ubicación",
        example = "TERRAZA",
        allowableValues = {"TERRAZA", "SALON_PRINCIPAL", "VIP", "BAR"}
    )
    private UbicacionMesa ubicacion;
}
```

Swagger UI muestra los ejemplos en el panel "Try it out", facilitando las pruebas.

---

## Cómo usar Swagger UI — Paso a paso

### 1. Inicia la aplicación
```
./mvnw spring-boot:run
```

### 2. Abre el navegador
```
http://localhost:8080/swagger-ui.html
```

### 3. Explora los endpoints
Verás tres secciones (Tags): **Mesas**, **Meseros**, **Pedidos**.
Expande cualquier método para ver sus detalles.

### 4. Prueba un endpoint
1. Haz clic en el método que quieras probar (ej: `POST /api/mesas`)
2. Haz clic en el botón **"Try it out"**
3. Edita el JSON de ejemplo en el campo **Request body**
4. Haz clic en **"Execute"**
5. Observa la respuesta en la sección **"Responses"**

### Ejemplo — Crear una mesa

```json
// Request body (ya aparece pre-rellenado con los @Schema examples):
{
  "numeroMesa": 5,
  "ubicacion": "TERRAZA"
}
```

Respuesta esperada:
```json
// HTTP 201 Created
{
  "id": 1,
  "numeroMesa": 5,
  "ubicacion": "TERRAZA",
  "fechaRegistro": "2024-03-18T15:05:49",
  "fechaModificacion": "2024-03-18T15:05:49"
}
```

---

## Resumen de anotaciones OpenAPI

| Anotación | Dónde se usa | Para qué sirve |
|-----------|-------------|----------------|
| `@Tag` | Clase Controller | Agrupa endpoints en una sección |
| `@Operation` | Método del controller | Describe el endpoint |
| `@ApiResponse` | Método del controller | Documenta un código de respuesta posible |
| `@ApiResponses` | Método del controller | Agrupa múltiples `@ApiResponse` |
| `@Parameter` | Parámetro del método | Describe un `@PathVariable` o `@RequestParam` |
| `@Schema` | Clase o campo DTO | Describe el modelo de datos con ejemplos |
| `@Bean OpenAPI` | Clase `@Configuration` | Configura el encabezado global de la API |
