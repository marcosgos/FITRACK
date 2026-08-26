import { Users, Clock } from 'lucide-react'
import { Section, SectionHeading } from '../layout/Section'
import { Card } from '../ui/Card'
import { Badge } from '../ui/Badge'
import { Button } from '../ui/Button'
import { cn } from '@/lib/utils'
import { RETOS, ACCENT } from '@/data/content'

/** Tabla de clasificación "en vivo". */
function Leaderboard() {
  const { ranking } = RETOS
  return (
    <Card className="p-5 md:p-6">
      {/* Cabecera */}
      <div className="flex items-start justify-between">
        <div>
          <h3 className="font-display text-xl font-extrabold italic uppercase tracking-tight text-white">
            {ranking.title}
          </h3>
          <p className="mt-1 text-xs text-silver">{ranking.subtitle}</p>
        </div>
        <Badge dot live tone="red" className="border-red/30 text-red">
          En vivo
        </Badge>
      </div>

      {/* Filas */}
      <div className="mt-5 space-y-1">
        {ranking.rows.map((row) => {
          const a = ACCENT[row.accent]
          const top = row.pos === 1
          return (
            <div
              key={row.pos}
              className={cn(
                'flex items-center gap-3 rounded-xl px-3 py-2.5',
                top && 'bg-gradient-to-r from-red/15 to-transparent ring-1 ring-inset ring-red/20',
              )}
            >
              <span className={cn('w-4 text-center font-display text-base font-extrabold italic', a.text)}>
                {row.pos}
              </span>
              <span
                className={cn(
                  'flex h-9 w-9 shrink-0 items-center justify-center rounded-full text-xs font-bold text-white',
                  a.bar,
                )}
              >
                {row.initials}
              </span>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-semibold text-white">{row.name}</p>
                <div className="mt-1.5 h-1.5 w-full overflow-hidden rounded-full bg-white/10">
                  <div className={cn('h-full rounded-full', a.bar)} style={{ width: `${row.pct}%` }} />
                </div>
              </div>
              <span className="text-sm font-semibold tabular-nums text-white">{row.score}</span>
            </div>
          )
        })}
      </div>

      {/* Fila "TÚ" */}
      <div className="mt-3 flex items-center gap-3 border-t border-white/[0.08] pt-4">
        <span className="flex h-9 w-9 items-center justify-center rounded-full bg-red text-xs font-bold text-white">
          TÚ
        </span>
        <div className="flex-1">
          <p className="text-sm font-semibold text-white">Tu posición aparecerá aquí</p>
          <p className="text-xs text-silver">Apúntate al reto y empieza a sumar pasos</p>
        </div>
        <span className="text-silver">—</span>
      </div>
    </Card>
  )
}

export function Retos() {
  return (
    <Section id="retos">
      <SectionHeading eyebrow={RETOS.eyebrow} title={RETOS.title} paragraph={RETOS.paragraph} />

      <div className="mt-12 grid gap-6 lg:grid-cols-2">
        {/* Cards de reto */}
        <div className="space-y-4">
          {RETOS.challenges.map((c) => {
            const a = ACCENT[c.accent]
            return (
              <Card key={c.title} className="p-5">
                <div className="flex items-start gap-4">
                  <span
                    className={cn(
                      'flex h-12 w-12 shrink-0 items-center justify-center rounded-xl font-display text-base font-extrabold italic',
                      a.softBg,
                      a.text,
                    )}
                  >
                    {c.badge}
                  </span>
                  <div className="flex-1">
                    <h3 className="font-sans text-base font-bold text-white">{c.title}</h3>
                    <p className="mt-0.5 text-sm text-silver">{c.text}</p>
                    <div className="mt-2 flex items-center gap-4 text-xs text-silver">
                      <span className="flex items-center gap-1.5">
                        <Users size={14} /> {c.people}
                      </span>
                      <span className="flex items-center gap-1.5">
                        <Clock size={14} /> {c.days}
                      </span>
                    </div>
                  </div>
                </div>
                <Button size="block" className="mt-4">
                  Apuntarme al reto
                </Button>
              </Card>
            )
          })}
        </div>

        {/* Ranking */}
        <Leaderboard />
      </div>
    </Section>
  )
}
