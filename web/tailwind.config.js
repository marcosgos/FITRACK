/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        // Tokens base extraídos de Figma
        ink: '#0B0B0B', // Cod Gray — fondo principal
        red: {
          DEFAULT: '#E11D22', // Alizarin Crimson — color de marca
          50: '#E11D22',
          hover: '#F5252B',
        },
        silver: '#B8B8B8', // texto secundario
        // Acentos de gamificación (retos / ranking)
        amber: '#F0A93B',
        sky: '#3B9EF0',
        emerald: '#34C77B',
        violet: '#9B7BF0',
        rose: '#F06BA8',
        // Superficies (negro + capas de blanco translúcido) vía variables CSS
        surface: 'rgb(var(--surface) / <alpha-value>)',
        'surface-2': 'rgb(var(--surface-2) / <alpha-value>)',
      },
      fontFamily: {
        // Saira: display condensada e itálica (titulares y logo)
        display: ['Saira', 'system-ui', 'sans-serif'],
        // Hanken Grotesk: texto y UI
        sans: ['"Hanken Grotesk"', 'system-ui', 'sans-serif'],
      },
      borderRadius: {
        DEFAULT: '12px', // radio de botón del diseño
        card: '16px',
        pill: '9999px',
      },
      maxWidth: {
        container: '1200px', // ancho de contenido del diseño
      },
      boxShadow: {
        'red-glow': '0px 8px 24px rgba(225, 29, 34, 0.34)',
        'red-glow-sm': '0px 8px 10px rgba(225, 29, 34, 0.34)',
        card: '0 1px 0 rgba(255,255,255,0.04) inset, 0 20px 40px -20px rgba(0,0,0,0.6)',
      },
      backgroundImage: {
        'hero-glow':
          'radial-gradient(60% 60% at 80% 0%, rgba(225,29,34,0.18) 0%, rgba(225,29,34,0) 60%)',
      },
      keyframes: {
        'pulse-dot': {
          '0%, 100%': { opacity: '1' },
          '50%': { opacity: '0.35' },
        },
        'fade-up': {
          from: { opacity: '0', transform: 'translateY(16px)' },
          to: { opacity: '1', transform: 'translateY(0)' },
        },
      },
      animation: {
        'pulse-dot': 'pulse-dot 1.6s ease-in-out infinite',
        'fade-up': 'fade-up 0.6s ease-out both',
      },
    },
  },
  plugins: [],
}
