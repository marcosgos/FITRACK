import { useEffect, useState } from 'react'
import { Menu, X } from 'lucide-react'
import { cn } from '@/lib/utils'
import { Container } from './Section'
import { Logo } from '../ui/Logo'
import { Button } from '../ui/Button'
import { NAV_LINKS } from '@/data/content'

/** Header fijo, translúcido con blur; se vuelve más opaco al hacer scroll. */
export function Header() {
  const [scrolled, setScrolled] = useState(false)
  const [open, setOpen] = useState(false)

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 8)
    onScroll()
    window.addEventListener('scroll', onScroll, { passive: true })
    return () => window.removeEventListener('scroll', onScroll)
  }, [])

  return (
    <header
      className={cn(
        'fixed inset-x-0 top-0 z-50 border-b transition-colors duration-300',
        scrolled ? 'border-white/[0.08] bg-ink/80 backdrop-blur-md' : 'border-transparent',
      )}
    >
      <Container className="flex h-[72px] items-center justify-between">
        <a href="#top" aria-label="Inicio">
          <Logo />
        </a>

        {/* Navegación desktop */}
        <nav className="hidden items-center gap-8 md:flex">
          {NAV_LINKS.map((link) => (
            <a
              key={link.href}
              href={link.href}
              className="text-sm font-semibold text-silver transition-colors hover:text-white"
            >
              {link.label}
            </a>
          ))}
        </nav>

        <div className="hidden md:block">
          <Button size="md">Descargar</Button>
        </div>

        {/* Toggle móvil */}
        <button
          className="text-white md:hidden"
          onClick={() => setOpen((v) => !v)}
          aria-label="Abrir menú"
        >
          {open ? <X size={24} /> : <Menu size={24} />}
        </button>
      </Container>

      {/* Menú móvil desplegable */}
      {open && (
        <div className="border-t border-white/[0.08] bg-ink/95 backdrop-blur-md md:hidden">
          <Container className="flex flex-col gap-1 py-4">
            {NAV_LINKS.map((link) => (
              <a
                key={link.href}
                href={link.href}
                onClick={() => setOpen(false)}
                className="rounded-lg px-2 py-3 text-sm font-semibold text-silver hover:bg-white/[0.04] hover:text-white"
              >
                {link.label}
              </a>
            ))}
            <Button size="block" className="mt-2">
              Descargar
            </Button>
          </Container>
        </div>
      )}
    </header>
  )
}
