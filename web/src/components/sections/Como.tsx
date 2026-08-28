import { Section, SectionHeading } from '../layout/Section'
import { Card } from '../ui/Card'
import { COMO } from '@/data/content'

/** "Cómo funciona" — 3 pasos numerados. */
export function Como() {
  return (
    <Section id="como">
      <SectionHeading eyebrow={COMO.eyebrow} title={COMO.title} center />

      <div className="mt-12 grid gap-5 md:grid-cols-3">
        {COMO.steps.map(({ num, icon: Icon, title, text }) => (
          <Card key={num} className="p-7">
            <div className="flex items-center gap-3">
              <span className="font-display text-4xl font-extrabold italic text-white/15">{num}</span>
              <span className="flex h-11 w-11 items-center justify-center rounded-xl bg-red/15 text-red">
                <Icon size={22} />
              </span>
            </div>
            <h3 className="mt-5 font-sans text-lg font-bold text-white">{title}</h3>
            <p className="mt-2 text-sm leading-relaxed text-silver">{text}</p>
          </Card>
        ))}
      </div>
    </Section>
  )
}
