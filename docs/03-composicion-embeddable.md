# 03 — Composición con @Embeddable

## ¿Qué es la composición en JPA?

La **composición** permite agrupar campos relacionados en una clase separada (el componente) 
para ser reutilizados o simplemente para organizar mejor el código.

En este proyecto, hemos implementado este patrón en la entidad `Mesero`.

---

## El Componente: `NombreCompleto` (@Embeddable)

En lugar de tener los campos sueltos, los agrupamos en una clase dedicada:

```java
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class NombreCompleto {
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;
}
```

---

## Uso en la Entidad: `Mesero` (@Embedded)

La entidad `Mesero` ahora incluye el componente de forma limpia:

```java
@Entity
@Table(name = "meseros")
public class Mesero extends BaseEntity {

    @Embedded  
    private NombreCompleto nombreComp;

    @Column(name = "telefono", length = 20)
    private String telefono;
}
```

### Ventajas de este enfoque:
1. **Organización**: El modelo de objetos es más coherente y legible.
2. **Reutilización**: Podríamos usar `NombreCompleto` en otras entidades (ej: `Cliente`) sin repetir código.
3. **Mantenimiento**: Si el concepto de "nombre" cambia (ej: agregar segundo nombre), se cambia en un solo lugar.

---

## Tabla en Base de Datos

Lo más importante es que **en la base de datos la estructura sigue siendo plana**.
Hibernate "incrusta" los campos del componente directamente en la tabla de la entidad:

```
Tabla "meseros":
  ┌──────┬──────────────────────┬───────────────┬──────────────┬──────────────┐
  │  id  │   fecha_registro     │    nombres    │  apellidos   │   telefono   │
  ├──────┼──────────────────────┼───────────────┼──────────────┼──────────────┤
  │  1   │ 2024-03-18 10:00:00  │    Carlos     │    Ruiz      │  3001234567  │
  └──────┴──────────────────────┴───────────────┴──────────────┴──────────────┘
    ↑ de BaseEntity                                ↑↑↑ De NombreCompleto ↑↑↑
```

### Nota sobre el JSON
Aunque internamente usamos composición, en el JSON de la API verás un objeto anidado:
```json
{
  "id": 1,
  "nombreComp": {
    "nombres": "Carlos",
    "apellidos": "Ruiz"
  },
  "telefono": "3001234567"
}
```
Esto es porque hemos removido la anotación `@JsonUnwrapped` para favorecer una estructura de objetos clara.
