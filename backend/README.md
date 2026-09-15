# Backend de gestión de personas

API REST para crear, consultar, actualizar y eliminar personas. El backend contiene el
modelo JPA de `Persona` y `Direccion`, validaciones Bean Validation, persistencia con
Spring Data JPA, DTOs de entrada y salida, endpoints CRUD y un manejador global para los
errores de validación y de personas inexistentes.

La conservación automática de registros durante caídas de MySQL todavía está pendiente.

## Requisitos

- Java 21. La versión comprobada es Eclipse Temurin 21.0.12.1.
- Maven Wrapper incluido en `backend`. La versión efectiva comprobada es Maven 3.9.16.
- MySQL Server 8.0. La instalación local comprobada es MySQL 8.0.46.
- Acceso a Internet para descargar dependencias Maven cuando no estén en caché.

El proyecto usa Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Bean Validation y
MySQL Connector/J. Las versiones y dependencias efectivas están declaradas en `pom.xml`.

## Preparación de MySQL

La aplicación espera la base `gestion_personas`, con codificación `utf8mb4` y colación
`utf8mb4_0900_ai_ci`. La cuenta de aplicación prevista es `personas_app@localhost`, con
autenticación `caching_sha2_password` y únicamente estos permisos sobre la base:

- `SELECT`
- `INSERT`
- `UPDATE`
- `DELETE`

La cuenta de aplicación no debe tener permisos administrativos, de creación de tablas ni
de concesión de privilegios. La contraseña se define localmente en MySQL y nunca se escribe
en este README.

### Crear la base y la tabla

Ejecuta estas sentencias con una cuenta administrativa o de preparación del esquema, no
con `personas_app`:

```sql
CREATE DATABASE IF NOT EXISTS gestion_personas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE gestion_personas;

CREATE TABLE IF NOT EXISTS personas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    rut VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    calle VARCHAR(150) NOT NULL,
    comuna VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);
```

Las longitudes del ejemplo coinciden con la tabla `personas` preparada actualmente.
La entidad Java no fija estas longitudes mediante `@Column(length = ...)`; deben mantenerse
coordinadas con el esquema cuando se modifique.

### Crear o revisar la cuenta de aplicación

Configura `personas_app@localhost` desde MySQL Workbench o con una cuenta administrativa,
estableciendo la contraseña directamente en el cliente local. No escribas esa contraseña
en archivos, comandos registrados, capturas ni el repositorio.

Después concede únicamente los permisos de aplicación y comprueba el resultado:

```sql
GRANT SELECT, INSERT, UPDATE, DELETE
ON gestion_personas.*
TO 'personas_app'@'localhost';

SHOW GRANTS FOR 'personas_app'@'localhost';
```

La creación de la base, la tabla y la cuenta es una preparación administrativa separada.
La aplicación no crea ni modifica tablas: `spring.jpa.hibernate.ddl-auto=none` y
`spring.sql.init.mode=never` están configurados para impedirlo.

## Configuración externa

La aplicación lee estas variables de entorno:

- `DB_URL`: opcional. Valor local predeterminado:
  `jdbc:mysql://localhost:3306/gestion_personas?sslMode=REQUIRED`.
- `DB_USERNAME`: opcional. Valor local predeterminado: `personas_app`.
- `DB_PASSWORD`: obligatoria y sin valor predeterminado.

`sslMode=REQUIRED` mantiene TLS habilitado. Esta opción exige cifrado, pero no configura
la verificación de identidad del certificado del servidor.

Ejemplo de variables no secretas en PowerShell:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/gestion_personas?sslMode=REQUIRED'
$env:DB_USERNAME = 'personas_app'
```

Configura `DB_PASSWORD` únicamente en la configuración local de IntelliJ o en la sesión
de terminal que vaya a ejecutar Maven. No la incluyas en el repositorio.

## Ejecución desde IntelliJ IDEA

1. Abre la carpeta `backend` como proyecto Maven.
2. Selecciona el JDK 21 del proyecto.
3. En la configuración de `BackendApplication`, define `DB_PASSWORD` como variable de
   entorno local. Puedes definir también `DB_URL` y `DB_USERNAME` si no usarás los valores
   predeterminados.
4. Ejecuta `BackendApplication`.

Para comprobar el contexto, configura la misma variable `DB_PASSWORD` en la ejecución de
`BackendApplicationTests` y ejecuta la prueba generada `contextLoads`.

## Ejecución desde Maven Wrapper

Desde la carpeta `backend`, configura Java 21 y las variables necesarias en la sesión
actual. Introduce la contraseña de forma local y evita escribirla en el comando:

```powershell
$env:JAVA_HOME = 'C:/ruta/a/jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
& "$env:JAVA_HOME\bin\java.exe" -version
.\mvnw.cmd -v

$clavePersonas = Read-Host 'Contraseña de personas_app' -AsSecureString
try {
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $clavePersonas).Password
    .\mvnw.cmd -B -ntp test
    .\mvnw.cmd -B -ntp spring-boot:run
}
finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    $clavePersonas.Dispose()
    Remove-Variable clavePersonas -ErrorAction SilentlyContinue
}
```

La prueba disponible es `BackendApplicationTests.contextLoads`. El servidor HTTP usa el
puerto `8080` por defecto y se detiene con Ctrl+C.

## API REST

La base URL local es `http://localhost:8080`.

| Método | Ruta | Resultado esperado |
| --- | --- | --- |
| GET | `/api/personas` | Lista personas, `200 OK` |
| GET | `/api/personas/{id}` | Devuelve una persona, `200 OK` |
| POST | `/api/personas` | Crea una persona, `201 Created` |
| PUT | `/api/personas/{id}` | Actualiza una persona, `200 OK` |
| DELETE | `/api/personas/{id}` | Elimina una persona, `204 No Content` |

### Crear una persona

Solicitud:

```http
POST http://localhost:8080/api/personas
Content-Type: application/json

{
  "rut": "12.345.678-5",
  "nombre": "Ana",
  "apellido": "Pérez",
  "fechaNacimiento": "1990-05-12",
  "direccion": {
    "calle": "Av. Central 123",
    "comuna": "Santiago",
    "region": "Metropolitana"
  }
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "rut": "12.345.678-5",
  "nombre": "Ana",
  "apellido": "Pérez",
  "fechaNacimiento": "1990-05-12",
  "direccion": {
    "calle": "Av. Central 123",
    "comuna": "Santiago",
    "region": "Metropolitana"
  }
}
```

### Actualizar una persona

Usa el mismo formato de entrada con `PUT /api/personas/{id}`. El identificador de la URL
determina la persona que se actualiza; el cuerpo no incluye `id`.

### Errores y validaciones

El backend valida que RUT, nombre, apellido, calle, comuna y región no estén vacíos; que
fecha de nacimiento y dirección existan; y que la fecha de nacimiento sea anterior a hoy.
Todavía no valida el dígito verificador del RUT, catálogos de comuna o región, ni la
unicidad del RUT en la base de datos.

Los errores de validación responden `400 Bad Request`. Una persona inexistente responde
`404 Not Found`. El formato es:

```json
{
  "estado": 400,
  "mensaje": "nombre: no debe estar vacío",
  "fechaHora": "2026-09-15T13:30:00"
}
```

La fecha y hora se generan en el servidor. Otros errores todavía usan el manejo estándar
de Spring y requieren una decisión específica.

## Supuestos, observaciones y límites

- La cuenta `personas_app` se limita a operaciones CRUD sobre `gestion_personas`.
- El esquema no se genera ni se modifica automáticamente por la aplicación.
- El ejemplo de tabla usa los nombres derivados del mapeo JPA actual, incluida
  `fecha_nacimiento` y las columnas embebidas de `Direccion`.
- La conexión TLS está configurada con `sslMode=REQUIRED`; la validación del certificado
  del servidor queda pendiente de una configuración específica.
- No existe todavía paginación, autenticación, autorización ni documentación OpenAPI.
- No se han añadido pruebas HTTP funcionales en esta tarea.
- La conservación y el guardado automático de registros cuando MySQL está caído todavía
  están pendientes; no deben considerarse implementados ni comprobados.

## Referencias técnicas

- [Configuración externa de Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Validación en Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-validation.html)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/reference/)
- [Inicialización de bases de datos en Spring Boot](https://docs.spring.io/spring-boot/how-to/data-initialization.html)
- [Seguridad de MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html)
- [CREATE TABLE de MySQL](https://dev.mysql.com/doc/refman/8.0/en/create-table.html)
- [GRANT de MySQL](https://dev.mysql.com/doc/refman/8.0/en/grant.html)
- [SHOW GRANTS de MySQL](https://dev.mysql.com/doc/refman/8.0/en/show-grants.html)
