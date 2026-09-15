# Backend de gestión de personas

API base para la gestión de personas. Actualmente contiene el proyecto Spring Boot,
la configuración de conexión a MySQL y una prueba de carga del contexto. El modelo de
personas, el esquema de tablas, los endpoints, las validaciones de negocio y la
conservación ante caídas se implementarán en tareas posteriores.

## 1. Requisitos

- JDK 21. La versión comprobada es Eclipse Temurin 21.0.12.1.
- Maven Wrapper 3.9.16, incluido en el proyecto.
- MySQL Server 8.0 con una base preparada y conexión TLS disponible.
- Acceso a Internet para descargar dependencias de Maven cuando no estén en caché.

El proyecto usa Spring Boot 4.1.1, Spring Web MVC, Spring Data JPA, Validation y MySQL
Connector/J. No se han añadido dependencias para el esquema ni para funcionalidades de
personas.

## 2. Preparación de MySQL

La base de desarrollo se llama `gestion_personas` y utiliza `utf8mb4` con colación
`utf8mb4_0900_ai_ci`. La cuenta de aplicación es `personas_app@localhost`, con
autenticación `caching_sha2_password`.

La cuenta debe conservar únicamente los permisos mínimos `SELECT`, `INSERT`, `UPDATE` y
`DELETE` sobre `gestion_personas`. No requiere permisos administrativos, de creación de
tablas ni de concesión de privilegios. Antes de preparar otra instalación, comprobar que
la base y la cuenta no existan ya y revisar los permisos efectivos con `SHOW GRANTS`.

El esquema de tablas se definirá en una tarea separada. Esta aplicación no crea ni modifica
tablas automáticamente.

## 3. Configuración externa

La configuración usa estas variables de entorno:

- `DB_URL`: opcional; por defecto `jdbc:mysql://localhost:3306/gestion_personas?sslMode=REQUIRED`.
- `DB_USERNAME`: opcional; por defecto `personas_app`.
- `DB_PASSWORD`: obligatoria y sin valor predeterminado.

La contraseña se introduce en la configuración local de ejecución y no se guarda en este
repositorio. No escribirla en archivos versionados, argumentos de comandos, capturas ni
registros.

## 4. Ejecución desde IntelliJ IDEA

En la configuración de `BackendApplication` y en la de `BackendApplicationTests`, añade
la variable de entorno `DB_PASSWORD` con la contraseña local de `personas_app`. Mantén
`DB_URL` y `DB_USERNAME` sin definir para usar sus valores locales predeterminados, o
configúralas si tu instancia utiliza otro host o puerto. Ejecuta cada configuración desde
IntelliJ con el JDK 21 del proyecto.

El arranque correcto debe mostrar `Started BackendApplication`. La prueba correcta debe
finalizar con código 0 y mostrar que el contexto se cargó. Estos resultados comprueban la
conexión y el contexto, no una funcionalidad de personas.

## 5. Ejecución desde terminal

Desde esta carpeta (`backend`), configura el JDK y la contraseña solo en la sesión actual:

```powershell
$env:JAVA_HOME = 'C:\ruta\a\jdk-21'
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
    Remove-Variable clavePersonas
}
```

El comando de prueba disponible es `contextLoads`, en `BackendApplicationTests`. Para
detener el servidor iniciado por `spring-boot:run`, usa Ctrl+C. La aplicación usa el
puerto HTTP 8080 por defecto.

## 6. Supuestos, límites y pendientes

- `spring.jpa.hibernate.ddl-auto=none` impide que Hibernate genere o modifique el esquema.
- `spring.sql.init.mode=never` impide la ejecución automática de scripts SQL.
- La conexión actual exige cifrado mediante `sslMode=REQUIRED`; no configura todavía
  verificación de identidad del certificado.
- La cuenta de aplicación mantiene permisos limitados y no se elevan para arrancar.
- No hay entidades, tablas, endpoints, CRUD, reglas de negocio ni recuperación automática
  ante caídas implementados.
- La ejecución desde una copia limpia y la preparación reproducible del esquema quedan
  pendientes de sus tareas correspondientes.

## 7. Referencias técnicas

- [Configuración externa de Spring Boot](https://docs.spring.io/spring-boot/reference/features/external-config.html).
- [Inicialización de bases de datos en Spring Boot](https://docs.spring.io/spring-boot/how-to/data-initialization.html).
- [Seguridad de MySQL Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html).
- [SHOW GRANTS de MySQL](https://dev.mysql.com/doc/refman/8.0/en/show-grants.html).
