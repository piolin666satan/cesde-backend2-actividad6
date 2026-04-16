# Tutorial: Sistema de Gestión de Citas Odontológicas
### Proyecto Integrador - Alquimia Literaria

Este documento sirve como guía técnica y tutorial para el equipo de desarrollo. Aquí aprenderán cómo está construida la API y cómo interactuar con ella.

---

## Equipo de Trabajo
* **Coordinador y Backend Lead:** Santiago Sanchez Rojas.
* **Equipo Frontend:** Josue Restrepo, Santiago Zapata.
* **Equipo Backend-2:** Equipo 6 (Sistema de Gestión de Citas Odontológicas).

---

## Paso 1: Configuración del Entorno
Para que el proyecto funcione en sus máquinas locales, sigan estos pasos:

1. **Base de Datos:** Asegúrense de tener la base de datos en **Prisma.io** (o su instancia de PostgreSQL) corriendo correctamente.
2. **Archivo de Propiedades:** Verifiquen en `src/main/resources/application.properties` que las credenciales de la DB sean correctas (host, usuario y contraseña).
3. **Sincronización:** Ejecuten el proyecto desde su IDE. Hibernate creará automáticamente las tablas `citas`, `pacientes` y `odontologos` basándose en las Entidades.

---

## Paso 2: Entendiendo la Arquitectura (Tutorial de Capas)

Hemos implementado un flujo de datos estándar en la industria para asegurar el orden:

1. **DTO (Data Transfer Object):** Es el "formulario" que el Frontend nos envía. Solo pedimos lo necesario (IDs y fecha), protegiendo la estructura interna de nuestra base de datos.
2. **Controller:** Recibe la petición (GET/POST/DELETE). Es el puente entre el mundo exterior (HTTP) y nuestra lógica interna.
3. **Service (Lógica de Negocio):** Aquí es donde ocurre la validación. El sistema verifica que el odontólogo esté libre y que el paciente cumpla con las reglas de agendamiento.
4. **Repository:** Se encarga de hablar con la base de datos mediante **Spring Data JPA**. Incluye consultas personalizadas para contar citas semanales y revisar disponibilidad sin escribir SQL manual complejo.
5. **Modelos Especializados:** * **Embebidos:** Contamos con `DetalleCita`, vinculado al DTO para manejar de forma limpia el costo y el motivo.
    * **Enums:** Usamos tipos enumerados para el `EstadoCita` y `MotivoCita`, garantizando que la información sea clara y sin errores de escritura.

---

## Paso 3: Probando la API (Manual de Endpoints)
## Posibles Errores al momento de ejecutar primera version entregable del proyecto...

1. **Endpointrror de Llave Foránea: Si al hacer un POST recibes un error de "Foreign Key", asegúrate de que el pacienteId y el odontologoId existan en la base de datos de Prisma Studio.

2. **Error de Unboxing (Null): Si el sistema dice que el costo es null, verifica que el JSON enviado desde el Frontend tenga el nombre exacto de los campos definidos en el DTO.

### A. Obtener todas las citas
* **URL:** `http://localhost:8080/api/citas`
* **Método:** `GET`
* **Resultado:** Un JSON con la lista de todas las citas registradas en el sistema.

### B. Para cancelar una cita
* **URL:** `http://localhost:8080/api/citas/{id}`
* **Método:** `DELETE`
* **NOTA:** `Solo se permite si faltan más de 2 horas para la cita.`


### C. Crear una nueva cita (Importante para Frontend)
* **URL:** `http://localhost:8080/api/citas`
* **Método:** `POST`
* **Cuerpo (JSON):**
```json
{
  "fechaHora": "2026-04-20T10:00:00",
  "motivoCita": "LIMPIEZA",
  "costo": 150000,
  "pacienteId": 1,
  "odontologoId": 1
}


