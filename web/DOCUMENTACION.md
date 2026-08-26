# FITTRACK⁺ — Documentación del código de la landing

Explicación completa de **por qué** está construida así la web: stack, arquitectura,
sistema de diseño, configuración, y cada componente con el razonamiento detrás de la
sintaxis. Está escrita pensando en alguien que viene de **Java/JavaScript** y se está
metiendo en **React + TypeScript + Tailwind**.

---

## 1. Visión general y stack

La landing es una **SPA estática** (Single Page Application): un único HTML que React
rellena en el navegador. No hay backend propio en esta carpeta; es puro front, así que
se despliega como archivos estáticos.

| Pieza                | Para qué                                                         | Por qué esta y no otra                                                                 |
| -------------------- | --------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| **React 18**         | Construir la UI con componentes reutilizables                   | Estándar del sector; el diseño se descompone natural en secciones/tarjetas            |
| **TypeScript**       | JavaScript con tipos                                            | Detecta errores al escribir (como el compilador de Java), autocompletado y contratos  |
| **Vite**             | Servidor de desarrollo + empaquetador (bundler)                | Arranque instantáneo y HMR; build muy rápido. Sustituye a Webpack/CRA                  |
| **Tailwind CSS**     | Estilos mediante clases utilitarias en el propio JSX           | Rapidez y consistencia; el sistema de tokens del diseño se mapea 1:1                   |
| **lucide-react**     | Iconos (corazón, rayo, escudo…)                                | Set de iconos "line" idéntico al usado en Figma, como componentes React               |
| **clsx + tw-merge**  | Componer clases CSS condicionales sin conflictos               | Patrón estándar (el que usa shadcn/ui)                                                 |

> **Analogía Java:** piensa en cada componente React como una clase que devuelve
> "vista". Las `props` son como los parámetros del constructor, y el `estado` (useState)
> como atributos de instancia que, al cambiar, hacen que la vista se vuelva a pintar.

---

## 2. Arquitectura y flujo de datos

```
index.html ──► src/main.tsx ──► <App/>
                                   │
             ┌─────────────────────┼─────────────────────┐
        <Header/>              <main> (secciones)      <Footer/>
                                   │
   Hero · Salud · Como · Retos · Aprender · Testimonios · WebVersion · CTA · FAQ
                                   │
                        cada sección lee de  ►  src/data/content.ts
                        y compone            ►  ui/ · layout/ · mockups/
```

**Regla de oro del proyecto: separar datos de presentación.**
Todo el texto y los datos (features, retos, ranking, testimonios, FAQ, footer…) viven
en un único archivo, `src/data/content.ts`. Los componentes **no** llevan textos "a
pelo": reciben o importan datos y solo se ocupan de *cómo se ven*. Ventajas:

- Cambiar una frase o un dato se hace en un sitio, sin tocar la maquetación.
- El día de mañana esos datos pueden venir de la API de FITRACK en vez de un archivo:
  solo cambia el origen, no los componentes.
- Es más fácil de leer y de documentar.

**Flujo unidireccional:** los datos bajan de padre a hijo por `props`. No hay estado
global (no hace falta): la única lógica con estado es local (el menú móvil del header y
el acordeón de FAQ).

---

## 3. Sistema de diseño (design tokens)

Los valores visuales se extrajeron **directamente del Figma** (vía el conector de
Figma) y se centralizaron para no repetir "números mágicos".

### Colores

| Token        | Hex / valor              | Uso                                  |
| ------------ | ------------------------ | ------------------------------------ |
| `ink`        | `#0B0B0B`                | Fondo principal (negro)              |
| `red`        | `#E11D22`                | Color de marca (CTA, acentos, logo)  |
| `silver`     | `#B8B8B8`                | Texto secundario                     |
| `surface`    | `#121212` (variable CSS) | Tarjetas sobre el negro              |
| borde        | blanco al 8%             | Bordes sutiles (`border-hairline`)   |
| acentos      | ámbar/azul/verde/violeta/rosa | Gamificación (retos y ranking)  |

### Tipografías

- **Saira** (itálica, condensada, ExtraBold): titulares y logo → `font-display`.
- **Hanken Grotesk** (400–800): texto y UI → `font-sans`.

Se cargan desde Google Fonts en `index.html`. En `index.css` se aplica automáticamente
`font-display italic uppercase` a todos los `h1`/`h2` para reproducir el estilo del
diseño sin repetirlo en cada titular.

Todo esto se declara en **`tailwind.config.js`** dentro de `theme.extend`. A partir de
ahí se usan como clases: `bg-ink`, `text-red`, `text-silver`, `rounded-card`,
`shadow-red-glow`, etc.

---

## 4. Configuración del proyecto (qué hace cada archivo)

- **`package.json`** — dependencias y scripts (`dev`, `build`, `preview`, `lint`).
  `dependencies` = lo que necesita en el navegador; `devDependencies` = solo para
  desarrollar/compilar.
- **`vite.config.ts`** — registra el plugin de React y define el alias `@` → `src/`,
  para importar con `@/data/content` en vez de rutas relativas frágiles
  (`../../data/content`). El alias se resuelve de forma **ESM-safe**:

  ```ts
  alias: { '@': fileURLToPath(new URL('./src', import.meta.url)) }
  ```

  (Se usa `import.meta.url` y no `__dirname` porque el proyecto es ESM —`type:
  module`— y ahí `__dirname` no existe.)
- **`tsconfig.json` / `tsconfig.node.json`** — configuración de TypeScript. `strict:
  true` activa todas las comprobaciones (incluye `noUnusedLocals`, que es el que saltó y
  nos obligó a limpiar imports sin usar). Hay dos archivos porque el código de la app y
  el de la config de Vite se compilan con ajustes distintos (*project references*).
- **`tailwind.config.js`** — el `content` le dice a Tailwind qué archivos escanear para
  saber qué clases generar (así el CSS final solo incluye lo que usas). `theme.extend`
  añade los tokens del diseño.
- **`postcss.config.js`** — encadena Tailwind + Autoprefixer (añade prefijos de
  navegador automáticamente).

---

## 5. Estilos globales — `src/index.css`

```css
@tailwind base;        /* reset + estilos base de Tailwind */
@tailwind components;  /* nuestras clases de componente (@layer components) */
@tailwind utilities;   /* las utilidades (bg-red, px-4, flex…) */
```

Dentro se usan tres recursos clave:

- **`@layer base`** — estilos por defecto: fondo negro, fuente, y el degradado rojo
  sutil del hero. También se fuerza el estilo de `h1/h2` (Saira itálica en mayúsculas).
- **Variables CSS** (`--surface`, `--border`) — permiten tematizar como en shadcn/ui.
  Se guardan como valores RGB "en crudo" (`18 18 18`) para poder aplicarles opacidad con
  la sintaxis de Tailwind `bg-surface/60`.
- **`@layer components`** — clases reutilizables propias:
  - `.border-hairline` → borde blanco al 8% (se repite por toda la web).
  - `.eyebrow` → el pequeño rótulo en mayúsculas de cada sección.

---

## 6. Utilidad `cn()` — `src/lib/utils.ts`

```ts
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
```

Combina dos librerías:

- **clsx** — construye la cadena de clases juntando condicionales:
  `cn('base', isOpen && 'rotate-45')`.
- **tailwind-merge** — si dos clases de Tailwind chocan (p. ej. `px-2` y `px-4`), se
  queda con la última, evitando resultados impredecibles.

Es el patrón estándar para hacer componentes con variantes sin pelearte con el CSS.

---

## 7. Datos — `src/data/content.ts`

Aquí está **toda** la copy, fuertemente tipada. Ejemplo de contrato con `interface`
(como una interfaz de Java, pero solo para describir la forma del dato):

```ts
export interface Feature {
  icon: LucideIcon   // el propio componente de icono, no un string
  title: string
  text: string
}
```

Detalles de praxis:

- Cada bloque de datos se exporta como constante (`HERO`, `SALUD`, `RETOS`…).
- Los iconos se guardan como **el componente** (`Heart`, `Zap`…), no como texto: así el
  componente que pinta la tarjeta solo hace `<Icon />` sin un `switch` de por medio.
- `ACCENT` es un **`Record<AccentKey, …>`**: un mapa tipado que traduce cada color de
  acento (`'red' | 'amber' | …`, un *union type*) a sus clases de Tailwind. Evita
  `if/else` de color repartidos por el código.
- Las respuestas 2–5 del FAQ están **redactadas por nosotros** (en Figma solo era
  visible la primera) y marcadas con un comentario, para que quede claro qué es diseño y
  qué es relleno coherente.

---

## 8. Primitivos de UI — `src/components/ui/`

Piezas pequeñas y reutilizables (la base del "design system").

### `Button.tsx` — patrón de variantes

```tsx
const variants: Record<Variant, string> = {
  primary: 'bg-red text-white shadow-red-glow-sm hover:bg-red-hover …',
  ghost:   'bg-white/[0.04] text-white border-hairline …',
}
```

En vez de crear tres botones distintos, hay **uno** con props `variant` y `size`. La
sintaxis `...props` reenvía cualquier atributo nativo (`onClick`, `type`…) al `<button>`
real. `ButtonHTMLAttributes<HTMLButtonElement>` hace que TypeScript conozca esos
atributos.

### `Badge.tsx`

La píldora "eyebrow" (p. ej. `SALUD EN MOVIMIENTO`, `EN VIVO`, `PRÓXIMAMENTE`). Acepta
`dot` (punto de color) y `live` (le añade la animación `animate-pulse-dot` definida en
la config).

### `Card.tsx`

La superficie base de todas las tarjetas: `rounded-card` + `border-hairline` + fondo
translúcido con blur. Todo lo demás (padding, contenido) lo pone quien la usa.

### `Logo.tsx`

El wordmark **FITTRACK⁺**: "FIT" en blanco, "TRACK" y "+" en rojo, en Saira itálica.
Es texto (no imagen), igual que en el diseño, así escala perfecto y pesa 0 KB.

### `StoreButtons.tsx`

Los botones de App Store y Google Play. Los logos son **SVG inline** (dibujados en el
propio componente) para no depender de imágenes externas.

---

## 9. Layout — `src/components/layout/`

### `Section.tsx`

Tres ayudantes de maquetación:

- **`Container`** — centra el contenido y lo limita a `max-w-container` (1200 px, el
  ancho del diseño) con padding lateral.
- **`Section`** — un `<section>` semántico con `id` (para el scroll del menú) y
  separación vertical estándar. `scroll-mt-24` deja hueco bajo el header fijo al saltar
  a un ancla.
- **`SectionHeading`** — el patrón repetido *eyebrow rojo + h2 + párrafo*.

### `Header.tsx` — estado y efectos

Es el primer componente con lógica:

```tsx
const [scrolled, setScrolled] = useState(false)
useEffect(() => {
  const onScroll = () => setScrolled(window.scrollY > 8)
  window.addEventListener('scroll', onScroll, { passive: true })
  return () => window.removeEventListener('scroll', onScroll)
}, [])
```

- **`useState`** guarda si se ha hecho scroll (para volver opaco el header) y si el menú
  móvil está abierto.
- **`useEffect`** se suscribe al evento `scroll` al montar el componente y **devuelve una
  función de limpieza** que quita el listener al desmontar (evita fugas de memoria). El
  `[]` final significa "ejecútalo una sola vez".
- La navegación se genera con `NAV_LINKS.map(...)` — nada hardcodeado.

### `Footer.tsx`

Marca + tagline + redes + tres columnas de enlaces, todas generadas con `.map()` desde
`FOOTER`. Los iconos sociales llevan `aria-label` para accesibilidad.

---

## 10. Mockups — `src/components/mockups/`

- **`PhoneMockup.tsx`** — la maqueta del móvil del hero (saludo, botón rojo "Registrar
  entrenamiento", barra de progreso de pasos, lista de entrenos). Reproduce con divs y
  Tailwind lo que en Figma era la pantalla de la app.
- **`BrowserMockup.tsx`** — la ventana de navegador de la sección "versión web":
  semáforo, barra de URL, sidebar con navegación, tarjetas de stats y un gráfico de
  barras (las alturas salen del array `WEB.chart`).

---

## 11. Secciones — `src/components/sections/`

Cada una lee su bloque de `content.ts` y usa los primitivos. Patrones destacables:

- **Renderizado de listas con `.map()`** (React necesita una `key` única por elemento):

  ```tsx
  {SALUD.features.map(({ icon: Icon, title, text }) => (
    <Card key={title}> … </Card>
  ))}
  ```

  Fíjate en `{ icon: Icon }`: se **desestructura y renombra** la prop `icon` a `Icon`
  (con mayúscula) porque JSX exige que los componentes empiecen por mayúscula.

- **`Retos.tsx`** — la sección más rica: a la izquierda las tarjetas de reto (chip de
  color por acento vía el mapa `ACCENT`), a la derecha la tabla de clasificación con
  avatares de colores, barras de progreso (`width` calculado por `pct`) y la fila 1
  resaltada con un degradado.

- **`FAQ.tsx`** — acordeón accesible sin librerías:

  ```tsx
  const [open, setOpen] = useState<number | null>(0)   // 1ª abierta
  …
  <div className={isOpen ? 'grid-rows-[1fr]' : 'grid-rows-[0fr]'}>
  ```

  El truco de animar `grid-rows` de `0fr` a `1fr` permite una transición de altura
  suave sin conocer la altura exacta del contenido. El `aria-expanded` comunica el
  estado a lectores de pantalla.

---

## 12. Responsive

Tailwind es **mobile-first**: las clases sin prefijo aplican en móvil y los prefijos
`md:` / `lg:` a partir de cierto ancho. Ejemplos:

- `grid md:grid-cols-2` → una columna en móvil, dos en escritorio.
- El header muestra la navegación con `hidden md:flex` y el botón hamburguesa con
  `md:hidden`.

---

## 13. Accesibilidad y semántica

- Etiquetas HTML con sentido: `<header>`, `<main>`, `<section>`, `<footer>`, `<nav>`.
- `aria-label` en iconos/enlaces sin texto; `aria-expanded` en el acordeón.
- Contraste alto (blanco/plata sobre negro) y foco visible en botones
  (`focus-visible:ring`).

---

## 14. Convenciones / praxis del proyecto

- **Un componente = un archivo**, con nombre en PascalCase.
- **Datos fuera de la vista** (todo en `content.ts`).
- **TypeScript estricto**: nada de `any`; interfaces para cada estructura.
- **Composición sobre repetición**: primitivos (`Button`, `Card`, `Badge`) reutilizados
  en todas las secciones.
- **Sin CSS suelto**: todo con utilidades Tailwind + tokens; lo repetido se sube a
  `@layer components`.
- **Sin números mágicos de color/espaciado**: se usan los tokens del diseño.

---

## 15. Ejecutar y desplegar

```bash
cd web
npm install
npm run dev       # desarrollo → http://localhost:5173
npm run build     # producción → dist/
npm run preview   # prueba local del build
```

**Deploy** (SPA estática): Vercel o Netlify con *root directory* = `web`, build
`npm run build`, output `dist`. Para GitHub Pages, ajustar `base` en `vite.config.ts`.

---

## 16. Notas de fidelidad al diseño

- Los iconos son de **lucide-react** (glifos equivalentes a los del Figma), no assets
  exportados: evita URLs que caducan y mantiene todo como código.
- Las respuestas 2–5 del FAQ son **redacción propia** coherente con la copy (en el
  diseño solo estaba la primera).
- Colores, tipografías, radios y sombras salen de los tokens leídos del archivo de
  Figma.
