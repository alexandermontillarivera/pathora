# Pathora Frontend

Aplicación construida con Svelte 5, TypeScript y Vite, integrada con la API de Pathora mediante `fetch` nativo.

```powershell
npm install
npm run dev
```

Copia `.env.example` como `.env` si necesitas cambiar la URL del backend.

## Estructura

- `src/lib/components`: componentes compartidos por capa (`auth`, `brand`, `cards`, `form`, `layout`, `ui`).
- `src/lib/data`: datos mock que reflejan los contratos del backend.
- `src/lib/api`: cliente HTTP, errores y contratos transportables.
- `src/lib/services`: integración por dominio con el backend.
- `src/lib/hooks`: estado reactivo reutilizable, recursos y paginación infinita.
- `src/lib/types.ts`: tipos globales del dominio.
- `src/modules`: páginas y flujos organizados por funcionalidad.

La navegación usa `@dvcol/svelte-simple-router` en modo History API, con URLs limpias y una página 404. Los imports internos usan alias como `@components`, `@modules`, `@lib`, `@data` y `@stores`.

En producción, el servidor debe aplicar un fallback de las rutas del frontend hacia `index.html` para que una recarga directa de `/careers/1` funcione correctamente.

## Verificación

```powershell
npm run check
npm run build
```
