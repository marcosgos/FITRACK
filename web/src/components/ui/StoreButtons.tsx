import { cn } from '@/lib/utils'

/* Logos de tienda como SVG inline (sin assets externos que caduquen). */

function AppleLogo({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 24 24" className={className} fill="currentColor" aria-hidden>
      <path d="M16.365 1.43c0 1.14-.417 2.2-1.11 3.02-.75.9-1.98 1.6-3.18 1.5-.14-1.12.42-2.29 1.09-3.02.75-.83 2.06-1.45 3.2-1.5.03.34.03.68 0 .99v.01ZM20.5 17.4c-.6 1.38-.88 1.99-1.65 3.2-1.08 1.69-2.6 3.8-4.48 3.81-1.67.02-2.1-1.09-4.37-1.08-2.27.01-2.74 1.1-4.42 1.09-1.88-.02-3.32-1.9-4.4-3.6-3.02-4.73-3.34-10.28-1.47-13.23C6.03 6.4 7.9 5.65 9.66 5.65c1.79 0 2.92 1.09 4.4 1.09 1.44 0 2.31-1.09 4.38-1.09 1.57 0 3.23.86 4.42 2.34-3.88 2.13-3.25 7.67 1.24 9.4-.06.02-.06.02-.01.01Z" />
    </svg>
  )
}

function GooglePlayLogo({ className }: { className?: string }) {
  return (
    <svg viewBox="0 0 24 24" className={className} aria-hidden>
      <path d="M3.6 2.3c-.2.2-.3.5-.3.9v17.6c0 .4.1.7.3.9l.1.1L13.5 12v-.2L3.7 2.2l-.1.1Z" fill="#00C3FF" />
      <path d="M17 15.3l-3.5-3.5v-.2L17 8.1l.1.1 4.1 2.3c1.2.7 1.2 1.8 0 2.5L17 15.3Z" fill="#FFCE00" />
      <path d="M17.1 15.2 13.5 11.7 3.6 21.7c.4.4 1 .5 1.7.1l11.8-6.6Z" fill="#FF3D3D" />
      <path d="M17.1 8.2 5.3 1.6C4.6 1.2 4 1.3 3.6 1.7l9.9 9.9 3.6-3.4Z" fill="#00E676" />
    </svg>
  )
}

interface StoreButtonsProps {
  className?: string
  center?: boolean
}

export function StoreButtons({ className, center }: StoreButtonsProps) {
  return (
    <div className={cn('flex flex-wrap gap-3', center && 'justify-center', className)}>
      {/* App Store — fondo blanco, texto negro */}
      <a
        href="#descarga"
        className="group inline-flex items-center gap-3 rounded-xl bg-white px-4 py-2.5 text-black transition-transform hover:-translate-y-0.5"
      >
        <AppleLogo className="h-6 w-6" />
        <span className="flex flex-col leading-tight text-left">
          <span className="text-[0.62rem] font-medium text-black/70">Descárgalo en la</span>
          <span className="text-base font-bold">App Store</span>
        </span>
      </a>

      {/* Google Play — fondo oscuro, texto blanco */}
      <a
        href="#descarga"
        className="group inline-flex items-center gap-3 rounded-xl border-hairline bg-white/[0.04] px-4 py-2.5 text-white transition-transform hover:-translate-y-0.5"
      >
        <GooglePlayLogo className="h-6 w-6" />
        <span className="flex flex-col leading-tight text-left">
          <span className="text-[0.62rem] font-medium text-silver">Disponible en</span>
          <span className="text-base font-bold">Google Play</span>
        </span>
      </a>
    </div>
  )
}
