import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * `cn` — helper estándar (convención shadcn/ui) para componer clases de
 * Tailwind resolviendo conflictos. Combina clsx (condicionales) con
 * tailwind-merge (deduplica utilidades que chocan, p.ej. px-2 + px-4).
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}
