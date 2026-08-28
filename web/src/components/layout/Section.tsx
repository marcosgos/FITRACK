import { cn } from '@/lib/utils'
import type { HTMLAttributes, ReactNode } from 'react'

/** Contenedor centrado con el ancho del diseño (1200px) y padding lateral. */
export function Container({ className, ...props }: HTMLAttributes<HTMLDivElement>) {
  return <div className={cn('mx-auto w-full max-w-container px-6 md:px-8', className)} {...props} />
}

interface SectionProps extends HTMLAttributes<HTMLElement> {
  id?: string
  children: ReactNode
}

/** <section> con separación vertical estándar entre bloques. */
export function Section({ id, className, children, ...props }: SectionProps) {
  return (
    <section id={id} className={cn('scroll-mt-24 py-20 md:py-28', className)} {...props}>
      <Container>{children}</Container>
    </section>
  )
}

interface SectionHeadingProps {
  eyebrow: string
  title: ReactNode
  paragraph?: string
  center?: boolean
  className?: string
}

/** Cabecera de sección: eyebrow rojo + h2 (Saira) + párrafo opcional. */
export function SectionHeading({ eyebrow, title, paragraph, center, className }: SectionHeadingProps) {
  return (
    <div className={cn('max-w-2xl', center && 'mx-auto text-center', className)}>
      <p className={cn('eyebrow text-red')}>{eyebrow}</p>
      <h2 className="mt-3 text-3xl leading-[1.1] md:text-[2.75rem]">{title}</h2>
      {paragraph && (
        <p className={cn('mt-5 text-base leading-relaxed text-silver', center && 'mx-auto')}>
          {paragraph}
        </p>
      )}
    </div>
  )
}
