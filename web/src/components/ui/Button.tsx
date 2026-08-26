import { cn } from '@/lib/utils'
import type { ButtonHTMLAttributes, ReactNode } from 'react'

type Variant = 'primary' | 'ghost'
type Size = 'md' | 'lg' | 'block'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant
  size?: Size
  children: ReactNode
}

const variants: Record<Variant, string> = {
  // Botón de marca: rojo con glow, como en el header y las cards de reto
  primary:
    'bg-red text-white shadow-red-glow-sm hover:bg-red-hover hover:shadow-red-glow active:translate-y-px',
  ghost:
    'bg-white/[0.04] text-white border-hairline hover:bg-white/[0.08]',
}

const sizes: Record<Size, string> = {
  md: 'px-5 py-2.5 text-sm',
  lg: 'px-6 py-3 text-base',
  block: 'w-full px-5 py-3 text-sm',
}

export function Button({
  variant = 'primary',
  size = 'md',
  className,
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      className={cn(
        'inline-flex items-center justify-center gap-2 rounded font-bold transition-all duration-200 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red/50',
        variants[variant],
        sizes[size],
        className,
      )}
      {...props}
    >
      {children}
    </button>
  )
}
