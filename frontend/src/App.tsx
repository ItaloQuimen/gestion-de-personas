import { useEffect, useState } from 'react'
import { ListaPersonas } from './components/ListaPersonas'
import { obtenerPersonas } from './services/personasApi'
import type { Persona } from './types/persona'
import './App.css'

function App() {
  const [personas, setPersonas] = useState<Persona[]>([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let activo = true

    obtenerPersonas()
      .then((personasRecibidas) => {
        if (activo) {
          setPersonas(personasRecibidas)
          setCargando(false)
        }
      })
      .catch(() => {
        if (activo) {
          setError('No fue posible cargar las personas.')
          setCargando(false)
        }
      })

    return () => {
      activo = false
    }
  }, [])

  return (
    <main className="app-shell">
      <header className="app-header">
        <p className="eyebrow">Gestión de personas</p>
        <h1>Personas</h1>
        <p className="intro">Consulta la información de las personas registradas.</p>
      </header>
      {cargando && <p role="status">Cargando personas...</p>}
      {error && <p role="alert">{error}</p>}
      {!cargando && !error && <ListaPersonas personas={personas} />}
    </main>
  )
}

export default App
