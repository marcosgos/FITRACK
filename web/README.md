# FITTRACK⁺ · Landing web

Landing page de **FITTRACK⁺** reconstruida desde el diseño de Figma con
**React + TypeScript + Vite + Tailwind CSS**. Vive como carpeta hermana de la
app iOS y el backend dentro del repo `FITRACK`.

## Requisitos

- Node.js 18+ (probado con Node 22)
- npm 9+

## Puesta en marcha (local)

```bash
cd web
npm install        # instala dependencias
npm run dev        # servidor de desarrollo → http://localhost:5173
```

## Scripts

| Script            | Qué hace                                             |
| ----------------- | ---------------------------------------------------- |
| `npm run dev`     | Servidor de desarrollo con HMR (recarga en caliente) |
| `npm run build`   | Type-check (`tsc`) + build de producción en `dist/`  |
| `npm run preview` | Sirve en local el build de `dist/` para probarlo     |
| `npm run lint`    | Comprobación de tipos sin emitir                     |

## Estructura

```
web/
├─ index.html                 # HTML raíz + carga de fuentes (Saira, Hanken Grotesk)
├─ vite.config.ts             # Config de Vite + alias "@" → src
├─ tailwind.config.js         # Design tokens (colores, tipos, radios, sombras)
├─ src/
│  ├─ main.tsx                # Punto de entrada React
│  ├─ index.css               # Capas Tailwind + variables CSS + estilos base
│  ├─ App.tsx                 # Composición de la página
│  ├─ lib/utils.ts            # helper cn() (clsx + tailwind-merge)
│  ├─ data/content.ts         # TODA la copy y los datos, tipados
│  └─ components/
│     ├─ ui/                  # Primitivos: Button, Badge, Card, Logo, StoreButtons
│     ├─ layout/              # Header, Footer, Section/Container
│     ├─ mockups/             # PhoneMockup, BrowserMockup
│     └─ sections/            # Hero, Salud, Como, Retos, Aprender,
│                             #   Testimonios, WebVersion, CTA, FAQ
```

## Deploy

Es una SPA estática (solo `dist/`). Opciones:

- **Vercel / Netlify**: conecta el repo, *root directory* = `web`, build
  `npm run build`, output `dist`.
- **GitHub Pages**: pon `base: '/FITRACK/'` en `vite.config.ts`, build y publica
  `dist/`.

Ver `DOCUMENTACION.md` para la explicación completa del código.
