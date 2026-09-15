# Frontend de gestión de personas

Frontend de la aplicación de gestión de personas. Actualmente muestra una pantalla inicial
con un listado local de personas ficticias. La conexión con la API y las operaciones CRUD del
frontend están pendientes.

## Tecnologías

- React 19.3.0.
- React DOM 19.3.0.
- Vite 8.3.0.
- TypeScript 6.0.3.
- Oxlint 1.83.0, usado por el script de lint.

Las versiones anteriores corresponden a las versiones efectivas registradas en
`package-lock.json`.

## Requisitos previos

- Node.js 24.13.0 LTS.
- npm 11.6.2.

## Instalación

Desde esta carpeta, instala las dependencias del proyecto:

```bash
npm install
```

## Comandos disponibles

Iniciar el servidor de desarrollo con recarga automática:

```bash
npm run dev
```

Revisar el código con Oxlint:

```bash
npm run lint
```

Comprobar tipos y generar la compilación de producción:

```bash
npm run build
```

Previsualizar localmente la compilación generada:

```bash
npm run preview
```

## Estructura actual

- `src/App.tsx`: composición de la pantalla inicial.
- `src/components/ListaPersonas.tsx`: decide si muestra la lista o el estado vacío.
- `src/components/TarjetaPersona.tsx`: muestra los datos de una persona.
- `src/components/EmptyState.tsx`: mensaje reutilizable para una lista vacía.
- `src/data/samplePeople.ts`: datos ficticios locales en memoria.
- `src/types/persona.ts`: tipos `Persona` y `Direccion`.
- `src/App.css` y `src/index.css`: estilos de la pantalla.
- `vite.config.ts`: configuración de Vite y su plugin de React.

## Pantalla inicial

La pantalla muestra el título “Personas” y una tarjeta por cada registro local. Cada tarjeta
incluye nombre, apellido, RUT, fecha de nacimiento, calle, comuna y región.

Cuando la colección de personas está vacía, `ListaPersonas` muestra el mensaje “No hay personas
registradas” mediante `EmptyState`.

Los datos actuales son ficticios y viven únicamente en memoria. No se guardan en una base de
datos ni se recuperan desde un servicio externo.

La conexión con la API, la consulta remota y las operaciones de creación, edición y eliminación
del frontend se implementarán en tareas posteriores.

## Documentación oficial

- [React](https://react.dev/learn).
- [Vite](https://vite.dev/guide/).
- [TypeScript](https://www.typescriptlang.org/docs/).
- [npm](https://docs.npmjs.com/).
