import { LayoutGrid } from 'lucide-react'
import { cn } from '@/lib/utils'
import { WEB } from '@/data/content'

/** Maqueta de ventana de navegador con el dashboard web (sección #web). */
export function BrowserMockup() {
  return (
    <div className="overflow-hidden rounded-card border-hairline bg-surface/60 shadow-card">
      {/* Barra del navegador */}
      <div className="flex items-center gap-4 border-b border-white/[0.08] px-4 py-3">
        <div className="flex gap-2">
          <span className="h-3 w-3 rounded-full bg-red" />
          <span className="h-3 w-3 rounded-full bg-amber" />
          <span className="h-3 w-3 rounded-full bg-emerald" />
        </div>
        <div className="flex-1">
          <span className="inline-flex rounded-md bg-white/[0.06] px-3 py-1 text-xs text-silver">
            {WEB.url}
          </span>
        </div>
      </div>

      {/* Cuerpo: sidebar + contenido */}
      <div className="grid gap-4 p-4 sm:grid-cols-[200px_1fr]">
        {/* Sidebar */}
        <aside className="space-y-2">
          <div className="flex items-center gap-3 rounded-xl border-hairline bg-white/[0.03] p-3">
            <span className="flex h-8 w-8 items-center justify-center rounded-full bg-red text-xs font-bold text-white">
              {WEB.user.initials}
            </span>
            <span className="flex flex-col leading-tight">
              <span className="text-sm font-semibold text-white">{WEB.user.name}</span>
              <span className="text-[0.7rem] text-silver">{WEB.user.level}</span>
            </span>
          </div>
          <nav className="space-y-1">
            {WEB.nav.map((item, i) => (
              <div
                key={item}
                className={cn(
                  'flex items-center gap-2 rounded-lg px-3 py-2 text-sm',
                  i === 0
                    ? 'bg-red font-semibold text-white'
                    : 'text-silver hover:bg-white/[0.04]',
                )}
              >
                {i === 0 && <LayoutGrid size={15} />}
                {item}
              </div>
            ))}
          </nav>
        </aside>

        {/* Contenido principal */}
        <div className="space-y-4">
          {/* Stat cards */}
          <div className="grid grid-cols-3 gap-3">
            {WEB.stats.map((s) => (
              <div key={s.label} className="rounded-xl border-hairline bg-white/[0.03] p-3">
                <p className="text-[0.65rem] uppercase tracking-wider text-silver">{s.label}</p>
                <p
                  className={cn(
                    'mt-1 font-display text-3xl font-extrabold italic',
                    s.accent === true && 'text-red',
                    s.accent === 'amber' && 'text-amber',
                    s.accent === false && 'text-white',
                  )}
                >
                  {s.value}
                </p>
                <p className="mt-1 text-[0.7rem] text-silver">{s.sub}</p>
              </div>
            ))}
          </div>

          {/* Gráfico de barras */}
          <div className="flex h-40 items-end justify-between gap-2 rounded-xl border-hairline bg-white/[0.03] p-4">
            {WEB.chart.map((h, i) => (
              <div
                key={i}
                className={cn(
                  'w-full rounded-t-md',
                  h > 65 ? 'bg-red' : 'bg-red/40',
                )}
                style={{ height: `${h}%` }}
              />
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
