import { useEffect, useState } from 'react'
import { FormularioPersona } from './components/FormularioPersona'
import { ListaPersonas } from './components/ListaPersonas'
import { obtenerPersonas } from './services/personasApi'
import type { Persona } from './types/persona'
import './App.css'

function App() {
  const [personas, setPersonas] = useState<Persona[]>([])
  const [personaEnEdicion, setPersonaEnEdicion] = useState<Persona | null>(null)
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
      <FormularioPersona
        personaEnEdicion={personaEnEdicion}
        onPersonaGuardada={(persona) => {
          setPersonas((actuales) => personaEnEdicion
            ? actuales.map((actual) => actual.id === persona.id ? persona : actual)
            : [...actuales, persona])
          setPersonaEnEdicion(null)
        }}
        onCancelarEdicion={() => setPersonaEnEdicion(null)}
      />
      {cargando && <p role="status">Cargando personas...</p>}
      {error && <p role="alert">{error}</p>}
      {!cargando && !error && <ListaPersonas personas={personas} onEditar={setPersonaEnEdicion} />}
    </main>
  )
}

export default App
