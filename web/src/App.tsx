import { Header } from './components/layout/Header'
import { Footer } from './components/layout/Footer'
import { Hero } from './components/sections/Hero'
import { Salud } from './components/sections/Salud'
import { Como } from './components/sections/Como'
import { Retos } from './components/sections/Retos'
import { Aprender } from './components/sections/Aprender'
import { Testimonios } from './components/sections/Testimonios'
import { WebVersion } from './components/sections/WebVersion'
import { CTA } from './components/sections/CTA'
import { FAQ } from './components/sections/FAQ'

export default function App() {
  return (
    <>
      <Header />
      <main>
        <Hero />
        <Salud />
        <Como />
        <Retos />
        <Aprender />
        <Testimonios />
        <WebVersion />
        <CTA />
        <FAQ />
      </main>
      <Footer />
    </>
  )
}
