import { Plus, SlidersHorizontal, BatteryFull } from 'lucide-react'
import { Logo } from '../ui/Logo'

/** Maqueta del móvil con la app FITTRACK+ (columna derecha del hero). */
export function PhoneMockup() {
  return (
    <div className="relative mx-auto w-[300px] max-w-full">
      {/* Glow rojo detrás del teléfono */}
      <div className="absolute -inset-8 rounded-full bg-red/20 blur-3xl" aria-hidden />

      <div className="relative rounded-[2.5rem] border border-white/10 bg-black p-2 shadow-2xl">
        <div className="overflow-hidden rounded-[2rem] bg-ink">
          {/* Barra de estado */}
          <div className="flex items-center justify-between px-6 pt-3 text-[0.7rem] text-white">
            <span className="font-semibold">9:41</span>
            <BatteryFull size={18} className="text-white" />
          </div>

          <div className="px-4 pb-5 pt-2">
            {/* Cabecera app */}
            <div className="flex items-center justify-between py-2">
              <Logo className="text-lg" />
              <SlidersHorizontal size={17} className="text-silver" />
            </div>

            {/* Saludo */}
            <p className="mt-3 text-[0.65rem] uppercase tracking-wider text-silver">Esta semana</p>
            <h3 className="font-sans text-xl font-bold normal-case not-italic tracking-normal text-white">
              Buenas, Marta
            </h3>
            <p className="mt-1 text-sm text-silver">
              <span className="text-2xl font-bold text-white">4</span> entrenamientos · 2 h 15 min
            </p>

            {/* CTA registrar */}
            <button className="mt-4 flex w-full items-center gap-3 rounded-2xl bg-red px-4 py-3 text-left text-white shadow-red-glow-sm">
              <span className="flex h-8 w-8 items-center justify-center rounded-full bg-white/20">
                <Plus size={18} />
              </span>
              <span className="text-sm font-bold leading-tight">
                Registrar
                <br />
                entrenamiento
              </span>
            </button>

            {/* Pasos de hoy */}
            <div className="mt-4 rounded-2xl border-hairline bg-white/[0.04] p-4">
              <div className="flex items-center justify-between">
                <span className="text-sm font-semibold text-white">Pasos de hoy</span>
                <span className="text-[0.65rem] font-semibold text-red">84% meta</span>
              </div>
              <p className="mt-2">
                <span className="text-2xl font-bold text-white">8.420</span>
                <span className="text-sm text-silver"> / 10.000 pasos</span>
              </p>
              <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-white/10">
                <div className="h-full w-[84%] rounded-full bg-red" />
              </div>
            </div>

            {/* Lista de entrenamientos */}
            <div className="mt-4 space-y-2">
              {[
                { dot: 'bg-red', name: 'Correr', when: 'Hoy · 07:15', dur: '27 min' },
                { dot: 'bg-sky', name: 'Natación', when: 'Ayer · 18:00', dur: '45 min' },
              ].map((w) => (
                <div
                  key={w.name}
                  className="flex items-center gap-3 rounded-2xl border-hairline bg-white/[0.04] px-4 py-3"
                >
                  <span className={`h-2.5 w-2.5 rounded-full ${w.dot}`} />
                  <span className="flex flex-col leading-tight">
                    <span className="text-sm font-semibold text-white">{w.name}</span>
                    <span className="text-xs text-silver">{w.when}</span>
                  </span>
                  <span className="ml-auto text-sm font-semibold text-white">{w.dur}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
