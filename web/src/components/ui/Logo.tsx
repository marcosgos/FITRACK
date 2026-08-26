import { cn } from '@/lib/utils'

/** Wordmark FITTRACK+ — "FIT" en blanco, "TRACK" y "+" en rojo (Saira italic). */
export function Logo({ className }: { className?: string }) {
  return (
    <span
      className={cn(
        'select-none font-display text-[1.35rem] font-extrabold italic leading-none tracking-tight',
        className,
      )}
      aria-label="FITTRACK+"
    >
      <span className="text-white">FIT</span>
      <span className="text-red">TRACK</span>
      <span className="align-top text-[0.7em] text-red">+</span>
    </span>
  )
}
