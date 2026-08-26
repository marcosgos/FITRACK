import { Container } from '../layout/Section'
import { StoreButtons } from '../ui/StoreButtons'
import { CTA as CTA_DATA } from '@/data/content'

/** Llamada a la acción final, sobre una tarjeta con glow rojo. */
export function CTA() {
  return (
    <Container className="py-8">
      <div className="relative overflow-hidden rounded-card border-hairline bg-surface/60 px-6 py-16 text-center">
        {/* Glow de fondo */}
        <div className="absolute left-1/2 top-0 h-64 w-[40rem] -translate-x-1/2 bg-red/20 blur-[100px]" aria-hidden />
        <div className="relative mx-auto max-w-2xl">
          <h2 className="text-4xl leading-[1.05] md:text-5xl">{CTA_DATA.title}</h2>
          <p className="mt-5 text-base leading-relaxed text-silver">{CTA_DATA.paragraph}</p>
          <StoreButtons className="mt-8" center />
        </div>
      </div>
    </Container>
  )
}
