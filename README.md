# Gestión de personas

Aplicación web para registrar, consultar, editar y eliminar personas con su RUT, nombre,
apellido, fecha de nacimiento y dirección. La interfaz también calcula la edad actual a partir
de la fecha de nacimiento.

## 1. Arquitectura

El repositorio contiene una aplicación web React y una API REST Spring Boot. MySQL conserva
los datos y RabbitMQ recibe las solicitudes de creación para procesarlas de forma asíncrona.

```text
Navegador -> Frontend React -> API Spring Boot -> MySQL
                                  |
                                  +-> RabbitMQ -> consumidor -> MySQL
```

`GET`, `PUT` y `DELETE` operan directamente sobre MySQL. En una creación, el backend publica
el mensaje en una cola durable y responde `202 Accepted` con un `solicitudId`. El consumidor
intenta persistir la persona, confirma el mensaje después de completar la transacción y lo
reencola si agota los reintentos. El `solicitudId` y su restricción única permiten procesar una
entrega repetida sin crear duplicados.

## 2. Tecnologías principales

- Backend: Java 21, Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Bean Validation y
  Spring AMQP.
- Frontend: React 19.3.0, React DOM 19.3.0, TypeScript 6.0.3 y Vite 8.3.0.
- Base de datos: MySQL Server 8.0.
- Mensajería: RabbitMQ 4.1 Management mediante Docker Compose.
- Herramientas: Maven Wrapper, npm y Oxlint.

## 3. Estructura del repositorio

- `backend/`: API, persistencia, mensajería, pruebas y configuración de RabbitMQ.
- `frontend/`: interfaz React, componentes, servicios HTTP y estilos.
- [`backend/README.md`](backend/README.md): preparación de MySQL y RabbitMQ, variables,
  ejecución, API, pruebas y límites del backend.
- [`frontend/README.md`](frontend/README.md): instalación, comandos y funcionamiento de la
  interfaz.

## 4. Requisitos previos

- JDK 21.
- Docker Desktop con Docker Compose.
- MySQL Server 8.0.
- Node.js 24.13.0 LTS y npm 11.6.2.
- Acceso a Internet para la descarga inicial de dependencias e imágenes.
- Credenciales locales separadas para MySQL y RabbitMQ.

Las contraseñas se entregan al backend mediante `DB_PASSWORD` y `RABBITMQ_PASSWORD`. No se
almacenan en el repositorio.

## 5. Orden de ejecución

1. Prepara la base `gestion_personas`, la tabla `personas` y la cuenta de aplicación siguiendo
   [la guía del backend](backend/README.md#preparación-de-mysql).
2. Desde `backend/`, define las variables locales según la sección
   [RabbitMQ con Docker Compose](backend/README.md#rabbitmq-con-docker-compose) e inicia el
   broker:

   ```powershell
   docker compose up -d --wait
   docker compose ps
   ```

3. Configura las variables locales requeridas e inicia el backend con JDK 21:

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   La API queda disponible en `http://localhost:8080`. Consulta los pasos completos para
   [IntelliJ IDEA o Maven Wrapper](backend/README.md#ejecución).

4. En otra terminal, instala e inicia el frontend:

   ```powershell
   cd frontend
   npm install
   npm run dev
   ```

   Vite redirige las solicitudes `/api` al backend local. La guía detallada está en
   [`frontend/README.md`](frontend/README.md).

## 6. Funciones disponibles

- Listar personas y consultar sus datos.
- Mostrar la edad actual calculada en el navegador.
- Enviar solicitudes asíncronas de creación.
- Editar personas existentes.
- Eliminar personas después de una confirmación.
- Mostrar estados de carga, lista vacía, solicitud aceptada y errores comprensibles.

Los ejemplos y datos usados para pruebas son ficticios.

## 7. Comprobaciones

Backend, desde `backend/`:

```powershell
.\mvnw.cmd -B -ntp test
```

Las pruebas cubren el contexto de Spring, el procesamiento idempotente, la confirmación del
mensaje después del guardado y el reencolado cuando se agotan los intentos.

Frontend, desde `frontend/`:

```powershell
npm run lint
npm run build
```

También se documentó una prueba manual de caída y recuperación de MySQL en
[`backend/README.md`](backend/README.md#prueba-reproducible-de-caída-y-recuperación).

## 8. Límites actuales

- Solo la creación usa RabbitMQ. Consultar, editar y eliminar requieren MySQL disponible.
- Una respuesta `202 Accepted` confirma la recepción por RabbitMQ, no la inserción en MySQL.
- El frontend muestra el identificador de la solicitud aceptada. Es necesario recargar para
  consultar la lista después de que el consumidor complete la creación.
- Si RabbitMQ no está disponible, una creación nueva responde `503` y debe reintentarse.
- No hay consulta de estado por `solicitudId`, cola de descartes, paginación, autenticación,
  autorización ni documentación OpenAPI.
- La instalación completa desde una copia limpia permanece como comprobación adicional.
