# Pathora Frontend

SPA built with Svelte 5, TypeScript, and Vite, integrated with the Pathora API via native `fetch`.

```powershell
npm install
npm run dev
```

Copy `.env.example` to `.env` if you need to change the backend URL.

## Structure

- `src/lib/components`: shared components by layer (`auth`, `brand`, `cards`, `form`, `layout`, `ui`).
- `src/lib/api`: HTTP client, error handling, and transport contracts.
- `src/lib/services`: domain-level backend integrations.
- `src/lib/hooks`: reusable reactive state, resources, and infinite pagination.
- `src/lib/stores`: global stores for auth and UI state.
- `src/lib/types.ts`: global domain types.
- `src/modules`: pages and flows organized by feature.

Navigation uses `@dvcol/svelte-simple-router` in History API mode, with clean URLs and a 404 page. Internal imports use aliases such as `@components`, `@modules`, `@lib`, `@data`, and `@stores`.

In production, the server must apply a frontend route fallback to `index.html` so that direct visits to `/careers/1`, `/reset-password`, or an unknown route reach the Svelte router. `vercel.json` configures this fallback for Vercel and preserves query parameters such as password-reset tokens.

## Scripts

| Command | Description |
|---|---|
| `npm run dev` | Start Vite dev server |
| `npm run build` | Production build to `dist/` |
| `npm run preview` | Preview production build locally |
| `npm run check` | Run svelte-check and TypeScript type-checking |
