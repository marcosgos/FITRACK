import { Section } from '../layout/Section'
import { Card } from '../ui/Card'
import { APRENDER } from '@/data/content'

/** "Aprende con la app" — texto + stats a la izquierda, tarjetas a la derecha. */
export function Aprender() {
  const { icon: TipIcon } = APRENDER.tip
  return (
    <Section id="aprender">
      <div className="grid items-center gap-12 lg:grid-cols-2">
        {/* Izquierda: copy + stats */}
        <div className="max-w-lg">
          <p className="eyebrow text-red">{APRENDER.eyebrow}</p>
          <h2 className="mt-3 text-3xl leading-[1.1] md:text-[2.75rem]">{APRENDER.title}</h2>
          <p className="mt-5 text-base leading-relaxed text-silver">{APRENDER.paragraph}</p>

          <div className="mt-8 grid grid-cols-2 gap-4">
            {APRENDER.stats.map((s) => (
              <Card key={s.label} className="p-5">
                <p className="font-display text-4xl font-extrabold italic text-red">{s.value}</p>
                <p className="mt-2 text-sm text-silver">{s.label}</p>
              </Card>
            ))}
          </div>
        </div>

        {/* Derecha: consejo del día + 2 tarjetas */}
        <div className="space-y-4">
          <Card className="flex items-start gap-4 p-5">
            <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-amber/15 text-amber">
              <TipIcon size={20} />
            </span>
            <div>
              <p className="text-xs font-semibold uppercase tracking-wider text-amber">
                {APRENDER.tip.label}
              </p>
              <p className="mt-1 text-sm text-white">{APRENDER.tip.text}</p>
            </div>
          </Card>

          <div className="grid gap-4 sm:grid-cols-2">
            {APRENDER.cards.map(({ icon: Icon, title, text }) => (
              <Card key={title} className="p-5 transition-colors hover:border-red/30">
                <span className="flex h-10 w-10 items-center justify-center rounded-xl bg-red/15 text-red">
                  <Icon size={20} />
                </span>
                <h3 className="mt-4 font-sans text-base font-bold text-white">{title}</h3>
                <p className="mt-1 text-sm text-silver">{text}</p>
              </Card>
            ))}
          </div>
        </div>
      </div>
    </Section>
  )
}
