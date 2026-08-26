import { Container } from './Section'
import { Logo } from '../ui/Logo'
import { FOOTER } from '@/data/content'

export function Footer() {
  return (
    <footer className="border-t border-white/[0.08] bg-ink">
      <Container className="py-16">
        <div className="grid gap-12 md:grid-cols-[1.4fr_1fr_1fr_1fr]">
          {/* Marca + tagline + redes */}
          <div className="max-w-xs">
            <Logo />
            <p className="mt-5 text-sm leading-relaxed text-silver">{FOOTER.tagline}</p>
            <div className="mt-6 flex gap-3">
              {FOOTER.socials.map(({ icon: Icon, label, href }) => (
                <a
                  key={label}
                  href={href}
                  aria-label={label}
                  className="flex h-10 w-10 items-center justify-center rounded-full border-hairline bg-white/[0.04] text-silver transition-colors hover:border-red/40 hover:text-white"
                >
                  <Icon size={18} />
                </a>
              ))}
            </div>
          </div>

          {/* Columnas de enlaces */}
          {FOOTER.columns.map((col) => (
            <div key={col.title}>
              <h3 className="text-sm font-bold uppercase tracking-wider text-white">{col.title}</h3>
              <ul className="mt-4 space-y-3">
                {col.links.map((link) => (
                  <li key={link}>
                    <a href="#" className="text-sm text-silver transition-colors hover:text-white">
                      {link}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          ))}
        </div>

        <div className="mt-14 flex flex-col items-center justify-between gap-3 border-t border-white/[0.08] pt-6 text-xs text-silver sm:flex-row">
          <span>{FOOTER.copyright}</span>
          <span>{FOOTER.madeFor}</span>
        </div>
      </Container>
    </footer>
  )
}
