import {
  Heart,
  Zap,
  ShieldCheck,
  Flame,
  Timer,
  Footprints,
  LineChart,
  Lightbulb,
  Dumbbell,
  Target,
  Instagram,
  Twitter,
  Youtube,
  type LucideIcon,
} from 'lucide-react'

/* Tipos compartidos ------------------------------------------------------- */

export interface NavLink {
  label: string
  href: string
}

export interface Feature {
  icon: LucideIcon
  title: string
  text: string
}

export interface Step {
  num: string
  icon: LucideIcon
  title: string
  text: string
}

export type AccentKey = 'red' | 'amber' | 'sky' | 'emerald' | 'violet' | 'rose'

export interface Challenge {
  badge: string
  accent: AccentKey
  title: string
  text: string
  people: string
  days: string
}

export interface RankRow {
  pos: number
  initials: string
  name: string
  score: string
  /** ancho de la barra de progreso en % (relativo al líder) */
  pct: number
  accent: AccentKey
}

export interface Testimonial {
  quote: string
  initials: string
  name: string
  meta: string
}

export interface Faq {
  q: string
  a: string
}

/* Navegación -------------------------------------------------------------- */

export const NAV_LINKS: NavLink[] = [
  { label: 'Salud', href: '#salud' },
  { label: 'Cómo funciona', href: '#como' },
  { label: 'Retos', href: '#retos' },
  { label: 'Aprender', href: '#aprender' },
  { label: 'Versión web', href: '#web' },
]

/* Hero -------------------------------------------------------------------- */

export const HERO = {
  badge: 'Salud en movimiento',
  titleLines: ['Haz que', 'cada día'],
  titleAccent: 'cuente.',
  paragraph:
    'FITTRACK⁺ registra tus entrenamientos, cuenta tus pasos y te reta contra miles de personas. Salud, constancia y un punto de competición sana — todo en tu bolsillo.',
  stats: [
    { value: '12.400', label: 'personas compitiendo', accent: false },
    { value: '1.324', label: 'ejercicios en la guía', accent: false },
    { value: '10.000', label: 'pasos, tu meta diaria', accent: true },
  ],
}

/* Sección Salud ("por qué moverte") -------------------------------------- */

export const SALUD = {
  eyebrow: 'Por qué moverte',
  title: 'El deporte es la mejor inversión en ti',
  paragraph:
    'Moverte a diario mejora tu corazón, tu descanso y tu ánimo. FITTRACK⁺ hace que sea fácil empezar y difícil abandonar.',
  features: [
    {
      icon: Heart,
      title: 'Corazón más fuerte',
      text: 'Cumplir tus 10.000 pasos y sumar sesiones cardio cuida tu salud cardiovascular día a día.',
    },
    {
      icon: Zap,
      title: 'Más energía',
      text: 'La constancia te da mejor descanso y más vitalidad. Sesiones cortas cuentan tanto como las largas.',
    },
    {
      icon: ShieldCheck,
      title: 'Menos lesiones',
      text: 'Consejos de calentamiento y estiramiento en cada sesión para que entrenar sume, no reste.',
    },
    {
      icon: Flame,
      title: 'Motivación real',
      text: 'Retos y rankings en vivo: la competición sana es el mejor empujón para no faltar a tu cita.',
    },
  ] as Feature[],
}

/* Sección Cómo funciona --------------------------------------------------- */

export const COMO = {
  eyebrow: 'Cómo funciona',
  title: 'Tres pasos y a moverse',
  steps: [
    {
      num: '01',
      icon: Timer,
      title: 'Registra tu entreno',
      text: 'Correr, ciclismo, natación, fuerza, CrossFit o libre. Con cronómetro o a mano, en segundos.',
    },
    {
      num: '02',
      icon: Footprints,
      title: 'Sigue tus pasos',
      text: 'Tu meta diaria de 10.000 pasos siempre a la vista, con el progreso de la semana al instante.',
    },
    {
      num: '03',
      icon: LineChart,
      title: 'Revisa tu historial',
      text: 'Cada sesión guardada construye tu racha. Mira tu evolución y celebra cada nuevo récord.',
    },
  ] as Step[],
}

/* Sección Retos + ranking ------------------------------------------------- */

export const RETOS = {
  eyebrow: 'Retos & competición',
  title: 'Elige tu reto. Sube en el ranking.',
  paragraph:
    'Apúntate a los retos que quieras y compite con toda la comunidad. El ranking se actualiza en vivo con los resultados de cada persona — como la racha de quién anda más pasos.',
  challenges: [
    {
      badge: '10K',
      accent: 'red',
      title: 'Racha de pasos',
      text: 'Suma más pasos cada día que la comunidad',
      people: '3.482',
      days: '6 días',
    },
    {
      badge: '100',
      accent: 'amber',
      title: 'Reto 100 km',
      text: 'Acumula 100 km corriendo este mes',
      people: '1.905',
      days: '18 días',
    },
    {
      badge: '30',
      accent: 'sky',
      title: '30 días seguidos',
      text: 'Entrena todos los días, sin fallar uno',
      people: '2.241',
      days: '30 días',
    },
  ] as Challenge[],
  ranking: {
    title: 'Racha de pasos',
    subtitle: 'Ranking general · esta semana',
    rows: [
      { pos: 1, initials: 'LM', name: 'Lucía M.', score: '83.970', pct: 100, accent: 'red' },
      { pos: 2, initials: 'JP', name: 'Javier P.', score: '80.383', pct: 96, accent: 'amber' },
      { pos: 3, initials: 'NS', name: 'Nerea S.', score: '78.332', pct: 93, accent: 'sky' },
      { pos: 4, initials: 'AT', name: 'Adrián T.', score: '74.474', pct: 89, accent: 'emerald' },
      { pos: 5, initials: 'PG', name: 'Paula G.', score: '72.503', pct: 86, accent: 'violet' },
      { pos: 6, initials: 'MV', name: 'Marco V.', score: '70.843', pct: 84, accent: 'rose' },
    ] as RankRow[],
  },
}

/* Sección Aprender -------------------------------------------------------- */

export const APRENDER = {
  eyebrow: 'Aprende con la app',
  title: 'No solo cuentas. Aprendes.',
  paragraph:
    'Una biblioteca enorme de ejercicios con técnica correcta, más un consejo diario para entrenar mejor y cuidarte. Progresa con criterio, no a ciegas.',
  stats: [
    { value: '1.324', label: 'ejercicios con guía de técnica' },
    { value: '1/día', label: 'consejo para mejorar y no lesionarte' },
  ],
  tip: {
    icon: Lightbulb,
    label: 'Consejo del día',
    text: 'Tras correr, 5 minutos de estiramientos reducen las agujetas del día siguiente.',
  },
  cards: [
    { icon: Dumbbell, title: 'Explorar ejercicios', text: 'Por grupo muscular y material' },
    { icon: Target, title: 'Técnica correcta', text: 'Pasos y errores a evitar' },
  ],
}

/* Testimonios ------------------------------------------------------------- */

export const TESTIMONIOS = {
  eyebrow: 'La comunidad',
  title: 'Gente que ya no para',
  items: [
    {
      quote:
        'El ranking en vivo me tiene enganchada. Salgo a andar solo por no perder puestos — y he mejorado un montón.',
      initials: 'ML',
      name: 'Marta L.',
      meta: 'Valencia · 5 meses',
    },
    {
      quote:
        'Registro correr, natación y fuerza sin cambiar de app. Ver la racha semanal es lo que me hace volver.',
      initials: 'DR',
      name: 'Diego R.',
      meta: 'Sevilla · 8 meses',
    },
    {
      quote:
        'Los consejos diarios me han quitado dolores de espalda. Ahora entreno con cabeza y sin miedo a lesionarme.',
      initials: 'CA',
      name: 'Carla A.',
      meta: 'Bilbao · 3 meses',
    },
  ] as Testimonial[],
}

/* Sección Versión web (mockup navegador) ---------------------------------- */

export const WEB = {
  badge: 'Próximamente',
  title: 'Toda la app, ahora en tu navegador',
  paragraph:
    'Muy pronto podrás entrar a tu perfil, revisar tu historial completo, gestionar retos y ver los rankings desde cualquier ordenador. Tu progreso, sincronizado en todas partes.',
  url: 'app.fittrack.plus/perfil',
  user: { initials: 'ML', name: 'Marta L.', level: 'Nivel · Constante' },
  nav: ['Resumen', 'Entrenamientos', 'Retos y ranking', 'Ejercicios', 'Ajustes'],
  stats: [
    { label: 'Esta semana', value: '4', sub: 'entrenamientos', accent: false },
    { label: 'Pasos hoy', value: '8.420', sub: '84% de la meta', accent: true },
    { label: 'Racha', value: '12', sub: 'días seguidos', accent: 'amber' as const },
  ],
  // Alturas relativas (%) de las barras del gráfico del dashboard
  chart: [38, 57, 33, 71, 49, 61, 78],
}

/* CTA final --------------------------------------------------------------- */

export const CTA = {
  title: 'Tu primer paso empieza hoy',
  paragraph:
    'Descarga FITTRACK⁺ gratis, apúntate a tu primer reto y únete a las 12.400 personas que ya no lo dejan.',
}

/* FAQ --------------------------------------------------------------------- */
// Nota: en Figma solo la 1ª respuesta era visible; las demás se han redactado
// para ser coherentes con el resto de la copy.

export const FAQ = {
  eyebrow: 'Preguntas frecuentes',
  title: 'Lo que sueles preguntar',
  items: [
    {
      q: '¿FITTRACK⁺ es gratis?',
      a: 'Sí. Puedes registrar entrenamientos, seguir tus pasos, apuntarte a retos y ver los rankings sin pagar nada. Descárgala y empieza hoy mismo.',
    },
    {
      q: '¿Qué deportes puedo registrar?',
      a: 'Correr, ciclismo, natación, fuerza, CrossFit y un modo libre para cualquier actividad. Puedes cronometrar la sesión o añadirla a mano en segundos.',
    },
    {
      q: '¿Cómo funcionan los retos entre usuarios?',
      a: 'Te apuntas al reto que quieras y tu progreso suma automáticamente al ranking, que se actualiza en vivo. Compites con toda la comunidad y ves tu posición en tiempo real.',
    },
    {
      q: '¿Necesito una pulsera o sensor?',
      a: 'No. FITTRACK⁺ funciona solo con tu móvil usando el contador de pasos del teléfono. Si tienes wearable, más adelante podrás conectarlo.',
    },
    {
      q: '¿Habrá versión web?',
      a: 'Sí, está en camino. Muy pronto podrás acceder a tu perfil, historial, retos y rankings desde cualquier navegador, sincronizado con la app.',
    },
  ] as Faq[],
}

/* Footer ------------------------------------------------------------------ */

export const FOOTER = {
  tagline:
    'Entrena, compite y mejora tu salud. La app que convierte moverse en un hábito que no querrás dejar.',
  socials: [
    { icon: Instagram, label: 'Instagram', href: '#' },
    { icon: Twitter, label: 'X', href: '#' },
    { icon: Youtube, label: 'YouTube', href: '#' },
  ],
  columns: [
    {
      title: 'Producto',
      links: ['Salud', 'Cómo funciona', 'Retos y ranking', 'Aprender'],
    },
    {
      title: 'Compañía',
      links: ['Sobre nosotros', 'Versión web', 'Blog', 'Contacto'],
    },
    {
      title: 'Legal',
      links: ['Privacidad', 'Términos', 'Cookies'],
    },
  ],
  copyright: '© 2026 FITTRACK⁺. Todos los derechos reservados.',
  madeFor: 'Hecho para moverte.',
}

/** Mapa de acentos → clases utilitarias (texto / fondo suave / barra). */
export const ACCENT: Record<AccentKey, { text: string; softBg: string; bar: string; ring: string }> = {
  red: { text: 'text-red', softBg: 'bg-red/15', bar: 'bg-red', ring: 'ring-red/30' },
  amber: { text: 'text-amber', softBg: 'bg-amber/15', bar: 'bg-amber', ring: 'ring-amber/30' },
  sky: { text: 'text-sky', softBg: 'bg-sky/15', bar: 'bg-sky', ring: 'ring-sky/30' },
  emerald: { text: 'text-emerald', softBg: 'bg-emerald/15', bar: 'bg-emerald', ring: 'ring-emerald/30' },
  violet: { text: 'text-violet', softBg: 'bg-violet/15', bar: 'bg-violet', ring: 'ring-violet/30' },
  rose: { text: 'text-rose', softBg: 'bg-rose/15', bar: 'bg-rose', ring: 'ring-rose/30' },
}
