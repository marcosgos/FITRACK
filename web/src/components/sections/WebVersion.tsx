import { Section } from '../layout/Section'
import { Badge } from '../ui/Badge'
import { BrowserMockup } from '../mockups/BrowserMockup'
import { WEB } from '@/data/content'

/** "Toda la app, ahora en tu navegador" — badge próximamente + mockup. */
export function WebVersion() {
  return (
    <Section id="web">
      <div className="mx-auto max-w-2xl text-center">
        <Badge tone="amber">{WEB.badge}</Badge>
        <h2 className="mt-5 text-3xl leading-[1.1] md:text-[2.75rem]">{WEB.title}</h2>
        <p className="mt-5 text-base leading-relaxed text-silver">{WEB.paragraph}</p>
      </div>

      <div className="relative mt-14">
        <div className="absolute inset-x-10 -top-6 h-32 bg-hero-glow blur-2xl" aria-hidden />
        <div className="relative mx-auto max-w-4xl">
          <BrowserMockup />
        </div>
      </div>
    </Section>
  )
}
