import { useEffect, useState } from 'react'
import { FormularioPersona } from './components/FormularioPersona'
import { ListaPersonas } from './components/ListaPersonas'
import { eliminarPersona, obtenerPersonas } from './services/personasApi'
import type { Persona } from './types/persona'
import './App.css'

function App() {
  const [personas, setPersonas] = useState<Persona[]>([])
  const [personaEnEdicion, setPersonaEnEdicion] = useState<Persona | null>(null)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [errorEliminacion, setErrorEliminacion] = useState<string | null>(null)

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

  async function manejarEliminacion(persona: Persona) {
    const confirmado = window.confirm(`¿Quieres eliminar a ${persona.nombre} ${persona.apellido}?`)
    if (!confirmado) {
      return
    }

    setErrorEliminacion(null)
    try {
      await eliminarPersona(persona.id)
      setPersonas((actuales) => actuales.filter((actual) => actual.id !== persona.id))
    } catch {
      setErrorEliminacion('No fue posible eliminar la persona.')
    }
  }

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
      {errorEliminacion && <p role="alert">{errorEliminacion}</p>}
      {!cargando && !error && <ListaPersonas personas={personas} onEditar={setPersonaEnEdicion} onEliminar={manejarEliminacion} />}
    </main>
  )
}

export default App
