import { cn } from '@/lib/utils'
import type { ReactNode } from 'react'

interface BadgeProps {
  children: ReactNode
  /** muestra un punto a la izquierda */
  dot?: boolean
  /** color del punto y del acento (clase de texto/fondo) */
  tone?: 'green' | 'red' | 'amber'
  live?: boolean
  className?: string
}

const dotColor: Record<NonNullable<BadgeProps['tone']>, string> = {
  green: 'bg-emerald',
  red: 'bg-red',
  amber: 'bg-amber',
}

/** Píldora "eyebrow" con borde translúcido (badges de hero, "En vivo", etc.) */
export function Badge({ children, dot, tone = 'green', live, className }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center gap-2 rounded-pill border-hairline bg-white/[0.04] px-3.5 py-1.5 text-xs font-semibold uppercase tracking-[0.16em] text-silver',
        tone === 'amber' && 'border-amber/30 text-amber',
        className,
      )}
    >
      {dot && (
        <span className={cn('h-1.5 w-1.5 rounded-full', dotColor[tone], live && 'animate-pulse-dot')} />
      )}
      {children}
    </span>
  )
}
