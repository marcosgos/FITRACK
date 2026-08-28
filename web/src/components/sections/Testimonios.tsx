import { Star } from 'lucide-react'
import { Section, SectionHeading } from '../layout/Section'
import { Card } from '../ui/Card'
import { TESTIMONIOS } from '@/data/content'

/** "Gente que ya no para" — 3 testimonios con estrellas. */
export function Testimonios() {
  return (
    <Section>
      <SectionHeading eyebrow={TESTIMONIOS.eyebrow} title={TESTIMONIOS.title} center />

      <div className="mt-12 grid gap-5 md:grid-cols-3">
        {TESTIMONIOS.items.map((t) => (
          <Card key={t.name} className="flex flex-col p-6">
            <div className="flex gap-0.5 text-red">
              {Array.from({ length: 5 }).map((_, i) => (
                <Star key={i} size={16} fill="currentColor" />
              ))}
            </div>
            <p className="mt-4 flex-1 text-sm leading-relaxed text-white/90">"{t.quote}"</p>
            <div className="mt-5 flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-full bg-red/15 text-xs font-bold text-red">
                {t.initials}
              </span>
              <div>
                <p className="text-sm font-semibold text-white">{t.name}</p>
                <p className="text-xs text-silver">{t.meta}</p>
              </div>
            </div>
          </Card>
        ))}
      </div>
    </Section>
  )
}
