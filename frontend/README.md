# Frontend de gestión de personas

Aplicación web para consultar y administrar personas mediante una interfaz React. El frontend
permite listar, crear, editar y eliminar personas a través de la API del backend.

## 1. Tecnologías

- React 19.3.0.
- React DOM 19.3.0.
- Vite 8.3.0.
- TypeScript 6.0.3.
- Oxlint 1.83.0.

Las versiones efectivas están registradas en `package-lock.json`.

## 2. Requisitos previos

- Node.js 24.13.0 LTS.
- npm 11.6.2.
- Backend disponible en `http://localhost:8080`.

## 3. Instalación

Desde la carpeta `frontend`, instala las dependencias:

```bash
npm install
```

## 4. Comandos

Iniciar el servidor de desarrollo:

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

Previsualizar la compilación generada:

```bash
npm run preview
```

## 5. Integración con el backend

Durante el desarrollo, Vite redirige las solicitudes que comienzan por `/api` hacia
`http://localhost:8080`. La configuración está en `vite.config.ts` y permite usar la API sin
configurar CORS entre los servidores locales.

El servicio `src/services/personasApi.ts` utiliza las APIs HTTP del navegador para llamar a:

- `GET /api/personas` para listar.
- `POST /api/personas` para crear.
- `PUT /api/personas/{id}` para editar.
- `DELETE /api/personas/{id}` para eliminar.

## 6. Operaciones y validaciones

La pantalla permite:

- Consultar las personas registradas.
- Registrar una persona con sus datos y dirección.
- Editar una persona existente y guardar los cambios.
- Eliminar una persona después de una confirmación.

El formulario valida que todos sus campos estén completos, que el RUT no esté vacío y que la
fecha de nacimiento sea válida y anterior a la fecha actual. Las validaciones del backend siguen
siendo la autoridad para aceptar cada solicitud.

## 7. Estados visibles

- Carga: muestra `Cargando personas...` mientras se consulta el listado.
- Éxito con registros: muestra una tarjeta por persona.
- Lista vacía: muestra `No hay personas registradas`.
- Creación exitosa: muestra `Persona creada correctamente.` y actualiza el listado.
- Edición exitosa: muestra `Persona actualizada correctamente.` y actualiza el listado.
- Eliminación exitosa: quita la tarjeta eliminada del listado.
- Error de consulta: muestra `No fue posible cargar las personas.`.
- Error de creación, edición o eliminación: muestra un mensaje comprensible para la operación
  que falló.

## 8. Estructura relevante

- `src/App.tsx`: coordina el estado de la pantalla y las operaciones.
- `src/components/FormularioPersona.tsx`: formulario reutilizado para crear y editar.
- `src/components/ListaPersonas.tsx`: muestra la lista o el estado vacío.
- `src/components/TarjetaPersona.tsx`: muestra una persona y sus acciones.
- `src/components/EmptyState.tsx`: mensaje para una lista vacía.
- `src/services/personasApi.ts`: funciones de comunicación con la API.
- `src/types/persona.ts`: tipos `Persona` y `Direccion`.
- `src/App.css` y `src/index.css`: estilos de la interfaz.
- `vite.config.ts`: plugin de React y proxy de desarrollo.

Los datos utilizados en ejemplos y pruebas son ficticios. La aplicación depende del backend
activo para consultar y modificar los registros.

## 9. Documentación oficial

- [React](https://react.dev/learn).
- [Vite](https://vite.dev/guide/).
- [TypeScript](https://www.typescriptlang.org/docs/).
- [npm](https://docs.npmjs.com/).
