import { useState } from 'react'
import { Plus } from 'lucide-react'
import { Section, SectionHeading } from '../layout/Section'
import { cn } from '@/lib/utils'
import { FAQ as FAQ_DATA } from '@/data/content'

/** Acordeón de preguntas frecuentes (la primera abierta por defecto). */
export function FAQ() {
  const [open, setOpen] = useState<number | null>(0)

  return (
    <Section id="faq">
      <SectionHeading eyebrow={FAQ_DATA.eyebrow} title={FAQ_DATA.title} center />

      <div className="mx-auto mt-12 max-w-3xl space-y-3">
        {FAQ_DATA.items.map((item, i) => {
          const isOpen = open === i
          return (
            <div key={item.q} className="overflow-hidden rounded-card border-hairline bg-surface/60">
              <button
                onClick={() => setOpen(isOpen ? null : i)}
                className="flex w-full items-center justify-between gap-4 px-5 py-4 text-left"
                aria-expanded={isOpen}
              >
                <span className="text-base font-semibold text-white">{item.q}</span>
                <Plus
                  size={20}
                  className={cn(
                    'shrink-0 text-red transition-transform duration-300',
                    isOpen && 'rotate-45',
                  )}
                />
              </button>
              <div
                className={cn(
                  'grid transition-all duration-300 ease-out',
                  isOpen ? 'grid-rows-[1fr] opacity-100' : 'grid-rows-[0fr] opacity-0',
                )}
              >
                <div className="overflow-hidden">
                  <p className="px-5 pb-5 text-sm leading-relaxed text-silver">{item.a}</p>
                </div>
              </div>
            </div>
          )
        })}
      </div>
    </Section>
  )
}
