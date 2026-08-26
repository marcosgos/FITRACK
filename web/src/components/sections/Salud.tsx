import { Section, SectionHeading } from '../layout/Section'
import { Card } from '../ui/Card'
import { SALUD } from '@/data/content'

/** "Por qué moverte" — 4 cards de beneficios de salud. */
export function Salud() {
  return (
    <Section id="salud">
      <SectionHeading eyebrow={SALUD.eyebrow} title={SALUD.title} paragraph={SALUD.paragraph} />

      <div className="mt-12 grid gap-5 sm:grid-cols-2 lg:grid-cols-4">
        {SALUD.features.map(({ icon: Icon, title, text }) => (
          <Card key={title} className="p-6 transition-colors hover:border-red/30">
            <span className="flex h-12 w-12 items-center justify-center rounded-xl bg-red/15 text-red">
              <Icon size={24} />
            </span>
            <h3 className="mt-5 font-sans text-lg font-bold text-white">{title}</h3>
            <p className="mt-2 text-sm leading-relaxed text-silver">{text}</p>
          </Card>
        ))}
      </div>
    </Section>
  )
}
