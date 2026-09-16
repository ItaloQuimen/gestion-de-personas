# Backend de gestión de personas

API REST para crear, consultar, actualizar y eliminar personas. La creación se recibe de
forma asíncrona mediante RabbitMQ: el backend acepta la solicitud, la publica en una cola
durable y la guarda en MySQL cuando la base está disponible. Consulta, actualización y
eliminación continúan operando directamente sobre MySQL.

El proyecto se mantiene como un monolito Spring Boot. RabbitMQ actúa como intermediario
durable del flujo de creación; no hay Spring Cloud, Kafka ni microservicios adicionales.

## Tecnologías y requisitos

- Java 21. Versión local comprobada: Eclipse Temurin 21.0.12.1.
- Maven Wrapper incluido. Versión efectiva: Maven 3.9.16.
- Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Bean Validation y Spring AMQP.
- MySQL Server 8.0. Versión local comprobada previamente: MySQL 8.0.46.
- Docker Desktop con Docker Compose para RabbitMQ 4.1 Management.
- Acceso a Internet la primera vez que se descarguen dependencias e imágenes.

## Preparación de MySQL

La aplicación espera la base `gestion_personas`, con codificación `utf8mb4` y colación
`utf8mb4_0900_ai_ci`. Ejecuta la preparación con una cuenta administrativa o de gestión
del esquema, no con la cuenta de la aplicación.

### Instalación nueva

```sql
CREATE DATABASE IF NOT EXISTS gestion_personas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE gestion_personas;

CREATE TABLE IF NOT EXISTS personas (
    id BIGINT NOT NULL AUTO_INCREMENT,
    solicitud_id VARCHAR(36) NOT NULL,
    rut VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NOT NULL,
    calle VARCHAR(150) NOT NULL,
    comuna VARCHAR(100) NOT NULL,
    region VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_personas_solicitud_id UNIQUE (solicitud_id)
);
```

`solicitud_id` identifica la solicitud de creación. Su restricción única permite reconocer
un mensaje que RabbitMQ entregue nuevamente y evita insertar la misma solicitud dos veces.

### Actualización de la tabla existente

Si `personas` ya existe con registros y todavía no tiene `solicitud_id`, ejecuta una sola
vez:

```sql
USE gestion_personas;

ALTER TABLE personas ADD COLUMN solicitud_id VARCHAR(36) NULL AFTER id;
UPDATE personas SET solicitud_id = UUID() WHERE solicitud_id IS NULL;
ALTER TABLE personas MODIFY solicitud_id VARCHAR(36) NOT NULL;
ALTER TABLE personas
    ADD CONSTRAINT uk_personas_solicitud_id UNIQUE (solicitud_id);
```

La aplicación mantiene desactivada la modificación automática del esquema mediante
`spring.jpa.hibernate.ddl-auto=none` y `spring.sql.init.mode=never`.

### Cuenta de aplicación

La cuenta prevista es `personas_app@localhost`, con autenticación
`caching_sha2_password`. Configura su contraseña directamente en el cliente local y limita
sus permisos a:

```sql
GRANT SELECT, INSERT, UPDATE, DELETE
ON gestion_personas.*
TO 'personas_app'@'localhost';

SHOW GRANTS FOR 'personas_app'@'localhost';
```

La cuenta no necesita `CREATE`, `ALTER`, permisos administrativos ni capacidad de conceder
privilegios.

## RabbitMQ con Docker Compose

El archivo `compose.yaml` crea un único contenedor RabbitMQ, publica AMQP en `5672`, la
interfaz de administración en `15672` y conserva sus datos en el volumen
`rabbitmq_data`. La contraseña es obligatoria y no tiene valor predeterminado.

En PowerShell, desde `backend`, introdúcela sin escribirla en el comando:

```powershell
$claveRabbit = Read-Host 'Contraseña local de RabbitMQ' -AsSecureString
$env:RABBITMQ_PASSWORD = [System.Net.NetworkCredential]::new('', $claveRabbit).Password
$env:RABBITMQ_USERNAME = 'personas_app'

docker compose up -d --wait
docker compose ps
```

La interfaz queda disponible en `http://localhost:15672`. Usa las mismas credenciales de
RabbitMQ. Esta cuenta pertenece al broker y es independiente de `personas_app@localhost`
en MySQL, aunque el nombre local sea el mismo.

Para detener el broker sin eliminar los mensajes:

```powershell
docker compose stop
```

`docker compose down` elimina el contenedor y la red, pero conserva el volumen mientras no
se agregue `--volumes`.

## Configuración externa

- `DB_URL`: opcional. Conexión MySQL. El valor predeterminado es
  `jdbc:mysql://localhost:3306/gestion_personas?sslMode=REQUIRED`.
- `DB_USERNAME`: opcional. Usuario MySQL. El valor predeterminado es `personas_app`.
- `DB_PASSWORD`: obligatoria y sin valor predeterminado. Contraseña MySQL.
- `RABBITMQ_HOST`: opcional. Host del broker. El valor predeterminado es `localhost`.
- `RABBITMQ_PORT`: opcional. Puerto AMQP. El valor predeterminado es `5672`.
- `RABBITMQ_USERNAME`: opcional. Usuario del broker. El valor predeterminado es
  `personas_app`.
- `RABBITMQ_PASSWORD`: obligatoria y sin valor predeterminado. Contraseña del broker.
- `RABBITMQ_CONNECTION_TIMEOUT`: opcional. Espera de conexión con el broker, en
  milisegundos. El valor predeterminado es `5000`.
- `RABBITMQ_CONFIRM_TIMEOUT`: opcional. Espera de confirmación del broker, en
  milisegundos. El valor predeterminado es `5000`.
- `MYSQL_RETRY_MAX_ATTEMPTS`: opcional. Intentos por entrega antes de reencolar. El valor
  predeterminado es `3`.
- `MYSQL_RETRY_INITIAL_INTERVAL`: opcional. Espera inicial entre intentos, en milisegundos.
  El valor predeterminado es `1000`.
- `MYSQL_RETRY_MULTIPLIER`: opcional. Multiplicador de la espera. El valor predeterminado
  es `2.0`.
- `MYSQL_RETRY_MAX_INTERVAL`: opcional. Espera máxima, en milisegundos. El valor
  predeterminado es `5000`.

`DB_PASSWORD` y `RABBITMQ_PASSWORD` deben permanecer en la configuración local de
IntelliJ o en la sesión de terminal. No las guardes en el repositorio. La URL MySQL exige
TLS mediante `sslMode=REQUIRED`, aunque esa modalidad no valida la identidad del
certificado del servidor.

## Ejecución

### IntelliJ IDEA

1. Inicia RabbitMQ con Docker Compose.
2. Abre `backend` como proyecto Maven y selecciona JDK 21.
3. Configura `DB_PASSWORD` y `RABBITMQ_PASSWORD` en las variables de entorno de
   `BackendApplication`. Agrega las demás variables solo si no usarás sus valores locales.
4. Ejecuta `BackendApplication`.

Configura las mismas variables en `BackendApplicationTests` para ejecutar la prueba de
contexto.

### Maven Wrapper

```powershell
$env:JAVA_HOME = 'C:/ruta/a/jdk-21'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
& "$env:JAVA_HOME\bin\java.exe" -version
.\mvnw.cmd -v

$claveMySql = Read-Host 'Contraseña de personas_app en MySQL' -AsSecureString
$claveRabbit = Read-Host 'Contraseña local de RabbitMQ' -AsSecureString
try {
    $env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $claveMySql).Password
    $env:RABBITMQ_PASSWORD = [System.Net.NetworkCredential]::new('', $claveRabbit).Password
    .\mvnw.cmd -B -ntp test
    .\mvnw.cmd -B -ntp spring-boot:run
}
finally {
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:RABBITMQ_PASSWORD -ErrorAction SilentlyContinue
    $claveMySql.Dispose()
    $claveRabbit.Dispose()
}
```

El servidor HTTP usa el puerto `8080` y se detiene con Ctrl+C.

## API REST

Base URL local: `http://localhost:8080`.

- `GET /api/personas`: lista personas y responde `200 OK`.
- `GET /api/personas/{id}`: devuelve una persona y responde `200 OK`.
- `POST /api/personas`: acepta una creación y responde `202 Accepted`.
- `PUT /api/personas/{id}`: actualiza una persona y responde `200 OK`.
- `DELETE /api/personas/{id}`: elimina una persona y responde `204 No Content`.

### Crear una persona

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

El `202 Accepted` confirma que RabbitMQ recibió la solicitud; todavía no confirma la
inserción en MySQL:

```json
{
  "solicitudId": "8f691b2b-8eab-431d-9729-728c539d9328",
  "estado": "PENDIENTE"
}
```

El consumidor procesa una entrega de a una, intenta guardar hasta tres veces con espera
creciente y confirma manualmente el mensaje solo después de que la transacción MySQL se
complete. Si los intentos se agotan, rechaza y reencola el mensaje para conservarlo.

### Consultar, actualizar y eliminar

`GET /api/personas` permite comprobar cuándo terminó una creación. `PUT` usa el mismo
formato de persona en `/api/personas/{id}`. El identificador se toma de la URL. `DELETE`
no devuelve cuerpo cuando termina correctamente.

## Validaciones y errores

RUT, nombre, apellido, calle, comuna y región no pueden estar vacíos. La fecha de
nacimiento y la dirección son obligatorias, y la fecha debe ser anterior a hoy. Aún no se
valida el dígito verificador del RUT, catálogos de comuna o región ni la unicidad del RUT.

Los errores de validación responden `400`; una persona inexistente responde `404`. Si la
publicación no puede ser confirmada por RabbitMQ, la creación responde `503`:

```json
{
  "estado": 503,
  "mensaje": "RabbitMQ no está disponible para recibir la solicitud",
  "fechaHora": "2026-09-15T13:30:00"
}
```

## Prueba reproducible de caída y recuperación

1. Prepara `solicitud_id`, inicia MySQL, RabbitMQ y el backend.
2. Detén solo MySQL. En Windows, una consola con permisos administrativos puede usar
   `Stop-Service MySQL80`; confirma primero el nombre real con `Get-Service *mysql*`.
3. Envía un `POST /api/personas`. Debe responder `202` porque RabbitMQ sigue disponible.
4. Revisa `personas.creacion` en `http://localhost:15672`. Mientras MySQL no responde, el
   mensaje no se confirma y vuelve a quedar disponible por redelivery.
5. Inicia MySQL (`Start-Service MySQL80`).
6. Consulta `GET /api/personas` hasta encontrar el RUT enviado. Debe existir una sola fila.

Para observar redelivery de forma explícita, detén el backend mientras MySQL continúa
caído y vuelve a iniciarlo antes de recuperar MySQL. RabbitMQ entrega nuevamente la misma
solicitud. El consumidor consulta `solicitud_id` antes de insertar y la restricción única
de la tabla refuerza la protección frente a duplicados.

Las pruebas unitarias `PersonaCreationConsumerTests` comprueban el `ack` posterior al
guardado y el `nack` con reencolado tras agotar los intentos. `PersonaServiceTests`
comprueba que una solicitud ya persistida no se inserte otra vez.

## Supuestos y límites

- Solo la creación usa RabbitMQ. Consulta, actualización y eliminación requieren MySQL
  disponible y conservan su comportamiento síncrono.
- El frontend muestra la solicitud aceptada y su identificador. Recarga la página para
  consultar la lista después de que el consumidor termine.
- La cola, el exchange y los mensajes son durables. La persistencia real del broker depende
  además del volumen Docker y del almacenamiento local.
- Si RabbitMQ también está caído, no existe un destino durable para una creación nueva: el
  backend espera la confirmación y responde `503`; el cliente debe reintentar más tarde.
- Un mensaje que contiene datos válidos pero que nunca puede persistirse se seguirá
  reencolando. No se implementó una cola de descartes ni una operación para consultar el
  estado por `solicitudId`.
- No hay paginación, autenticación, autorización ni documentación OpenAPI.

## Referencias técnicas

- [Configuración externa de Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html)
- [Spring AMQP: confirmaciones del publicador](https://docs.spring.io/spring-amqp/reference/amqp/template.html)
- [Spring AMQP: contenedores de listeners y confirmación manual](https://docs.spring.io/spring-amqp/reference/amqp/containerAttributes.html)
- [RabbitMQ: confirmaciones, acknowledgements y redelivery](https://www.rabbitmq.com/docs/confirms)
- [RabbitMQ: fiabilidad](https://www.rabbitmq.com/docs/reliability)
- [Seguridad de MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html)
- [GRANT de MySQL](https://dev.mysql.com/doc/refman/8.0/en/grant.html)
