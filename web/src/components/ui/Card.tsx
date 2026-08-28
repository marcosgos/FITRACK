import { cn } from '@/lib/utils'
import type { HTMLAttributes } from 'react'

/** Superficie base: negro elevado + borde blanco al 8% + radio de card. */
export function Card({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return (
    <div
      className={cn(
        'rounded-card border-hairline bg-surface/60 backdrop-blur-sm',
        className,
      )}
      {...props}
    />
  )
}
