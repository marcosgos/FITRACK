import { Container } from '../layout/Section'
import { Badge } from '../ui/Badge'
import { StoreButtons } from '../ui/StoreButtons'
import { PhoneMockup } from '../mockups/PhoneMockup'
import { HERO } from '@/data/content'

export function Hero() {
  return (
    <section id="top" className="relative overflow-hidden pt-[72px]">
      <Container className="grid items-center gap-12 py-16 md:grid-cols-2 md:py-24">
        {/* Columna de texto */}
        <div className="animate-fade-up">
          <Badge dot tone="green">
            {HERO.badge}
          </Badge>

          <h1 className="mt-6 text-5xl leading-[0.95] md:text-7xl">
            {HERO.titleLines.map((line) => (
              <span key={line} className="block text-white">
                {line}
              </span>
            ))}
            <span className="block text-red">{HERO.titleAccent}</span>
          </h1>

          <p className="mt-6 max-w-lg text-lg leading-relaxed text-silver">{HERO.paragraph}</p>

          <StoreButtons className="mt-8" />

          {/* Fila de stats */}
          <div className="mt-10 flex flex-wrap items-center gap-x-8 gap-y-4">
            {HERO.stats.map((stat, i) => (
              <div key={stat.label} className="flex items-center gap-8">
                <div>
                  <p
                    className={`font-display text-3xl font-extrabold italic ${
                      stat.accent ? 'text-red' : 'text-white'
                    }`}
                  >
                    {stat.value}
                  </p>
                  <p className="mt-1 text-xs text-silver">{stat.label}</p>
                </div>
                {i < HERO.stats.length - 1 && <span className="hidden h-10 w-px bg-white/10 sm:block" />}
              </div>
            ))}
          </div>
        </div>

        {/* Columna del mockup */}
        <div className="animate-fade-up [animation-delay:150ms]">
          <PhoneMockup />
        </div>
      </Container>
    </section>
  )
}
