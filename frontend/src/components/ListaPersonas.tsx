import type { Persona } from '../types/persona'
import { EmptyState } from './EmptyState'
import { TarjetaPersona } from './TarjetaPersona'

type ListaPersonasProps = {
  personas: Persona[]
  onEditar: (persona: Persona) => void
}

export function ListaPersonas({ personas, onEditar }: ListaPersonasProps) {
  if (personas.length === 0) {
    return <EmptyState />
  }

  return (
    <ul className="person-list" aria-label="Listado de personas">
      {personas.map((persona) => <TarjetaPersona key={persona.id} persona={persona} onEditar={onEditar} />)}
    </ul>
  )
}
