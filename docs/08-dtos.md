# 08 — Exponiendo Entidades en la API

## ¿Por qué no usamos DTOs en este proyecto?

En el desarrollo de software, un **DTO** (Data Transfer Object) es una clase que se usa únicamente para mover datos entre el cliente (frontend) y el servidor (backend). 

Sin embargo, para este **proyecto básico**, hemos decidido **exponer las Entidades JPA directamente** en los controladores REST.

### Ventajas de este enfoque (para aprender):
1. **Menos Boilerplate**: No tenemos que crear 2 o 3 clases adicionales por cada entidad (`Request`, `Response`).
2. **Sincronización Automática**: Cualquier campo nuevo que agregues a la Entidad aparecerá automáticamente en la API JSON.
3. **Velocidad de Desarrollo**: Te permite centrarte en aprender Spring Boot y JPA sin perderte en capas de mapeo.

---

## Cómo Jackson maneja las Entidades

**Jackson** es la librería que Spring Boot usa para convertir objetos Java a JSON. Cuando devolvemos una `Entidad`, Jackson lee todos sus campos y genera el JSON.

### El reto de las relaciones (@ManyToOne)
Al usar entidades directamente, debemos tener cuidado con las relaciones. Si intentamos serializar un `Pedido` que tiene una `Mesa` cargada de forma perezosa (`LAZY`), podríamos obtener un error.

Para evitar esto, usamos la anotación `@JsonIgnoreProperties` en la entidad para decirle a Jackson qué campos ignorar o cómo manejarlos.

---

## Mapeo de IDs en Peticiones (Request)

Un problema común al no usar DTOs de "Request" es que el cliente tendría que enviar el objeto completo de la relación:

```json
// Incómodo para el cliente:
{
  "total": 50.0,
  "mesa": { "id": 1 }
}
```

En este proyecto, hemos optimizado las Entidades (como `Pedido`) usando la anotación `@JsonProperty` en los métodos **setters** para permitir que el cliente envíe solo el ID de forma plana:

```java
@JsonProperty("mesaId")
public void setMesaId(Long id) {
    this.mesa = new Mesa();
    this.mesa.setId(id);
}
```

**Resultado en la petición HTTP:**
```json
// Mucho más limpio:
{
  "total": 50.0,
  "mesaId": 1,
  "meseroId": 2
}
```

---

## Cuándo SI usar DTOs (Proyectos Reales)

A medida que una aplicación crece, los DTOs se vuelven indispensables por:
1. **Seguridad**: Para no exponer campos sensibles (ej: contraseñas de usuarios).
2. **Desacoplamiento**: Para poder cambiar la base de datos sin afectar a los clientes de la API.
3. **Optimización**: Para enviar solo los datos mínimos necesarios y reducir el tamaño del JSON.

En este nivel de aprendizaje, priorizamos la **visibilidad total del modelo** para que entiendas cómo fluyen los datos desde la base de datos hasta tu navegador.
